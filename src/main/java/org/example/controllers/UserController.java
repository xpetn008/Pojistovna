package org.example.controllers;

import org.example.data.entitites.PojisteniEntity;
import org.example.data.entitites.UserEntity;
import org.example.data.repositories.UserRepository;
import org.example.models.dto.PojistenecDTO;
import org.example.models.exceptions.DuplicateRodneCisloException;
import org.example.models.exceptions.FalseRCException;
import org.example.models.exceptions.SpatnyUdajException;
import org.example.models.services.PojistenecService;
import org.example.models.services.PojisteniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PojistenecService pojistenecService;
    @Autowired
    private PojisteniService pojisteniService;
    @GetMapping()
    public String renderUserIndex(@ModelAttribute PojistenecDTO pojistenecDTO, Model model, Authentication authentication) {
        if (model.containsAttribute("success")){
            model.addAttribute("success", model.asMap().get("success"));
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername()).get();
        pojistenecDTO.setUserId(userEntity.getId());
        String email = userEntity.getEmail();
        String soucet = pojisteniService.vratSoucetMesicnichSplatek(pojistenecDTO);
        boolean hasPojistenecAccess = pojistenecService.dontHavePojistenec(authentication);
        model.addAttribute("hasPojistenecAccess", hasPojistenecAccess);
        model.addAttribute("email", email);
        model.addAttribute("soucet", soucet);
        return "user/userIndex";
    }
    @GetMapping("/create")
    @PreAuthorize("@pojistenecService.dontHavePojistenec(authentication)")
    public String renderCreate(@ModelAttribute PojistenecDTO pojistenecDTO){
        return "user/create";
    }
    @PostMapping("/create")
    public String create(@ModelAttribute PojistenecDTO pojistenecDTO, BindingResult result, RedirectAttributes redirectAttributes, Authentication authentication) throws DuplicateRodneCisloException, SpatnyUdajException, FalseRCException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername()).get();
        pojistenecDTO.setUserId(userEntity.getId());
        List<Exception> errors = pojistenecService.create(pojistenecDTO);
        if (errors!=null) {
            for (Exception error : errors) {
                if (error instanceof SpatnyUdajException) {
                    SpatnyUdajException udajException = (SpatnyUdajException) error;
                    result.rejectValue(udajException.getColumn(), "error", udajException.getMessage());
                } else if (error instanceof DuplicateRodneCisloException) {
                    result.rejectValue("rodneCislo", "error", "Pojištěnec s tímto rodným číslem je již registrován");
                } else if (error instanceof FalseRCException) {
                    result.rejectValue("rodneCislo", "error", error.getMessage());
                }
            }
        }else {
            pojistenecService.savePojistenecToUser(pojistenecDTO);
        }
        if (result.hasErrors()){
            return renderCreate(pojistenecDTO);
        }
        redirectAttributes.addFlashAttribute("success", "Profil pojištěnce byl úspěšně vytvořen");
        return "redirect:/user";
    }

    @GetMapping("/update")
    @PreAuthorize("not(@pojistenecService.dontHavePojistenec(authentication))")
    public String renderUpdate(@ModelAttribute PojistenecDTO pojistenecDTO, Authentication authentication, Model model){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername()).get();
        pojistenecDTO.setUserId(userEntity.getId());
        pojistenecService.nactiUdaje(pojistenecDTO);
        return  "user/update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute PojistenecDTO pojistenecDTO, BindingResult result, RedirectAttributes redirectAttributes, Authentication authentication, Model model){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername()).get();
        pojistenecDTO.setUserId(userEntity.getId());
        try {
            pojistenecService.update(pojistenecDTO);
        }catch (SpatnyUdajException e){
            result.rejectValue("data", "error", e.getMessage());
            return renderUpdate(pojistenecDTO, authentication, model);
        }
        redirectAttributes.addFlashAttribute("success", "Údaje pojištěnce jsou úspěšně obnoveny");
        return renderUpdate(pojistenecDTO, authentication, model);
    }
    @GetMapping("/delete")
    @PreAuthorize("not(@pojistenecService.dontHavePojistenec(authentication))")
    public String renderDelete(){
        return "user/delete";
    }
    @PostMapping("/delete")
    public String delete(@ModelAttribute PojistenecDTO pojistenecDTO, Authentication authentication, RedirectAttributes redirectAttributes){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername()).get();
        pojistenecDTO.setUserId(userEntity.getId());
        pojistenecService.delete(pojistenecDTO);
        redirectAttributes.addFlashAttribute("success", "Profil pojištěnce byl úspěšně vymazán");
        return "redirect:/user";
    }

    @GetMapping("/nabidka")
    @PreAuthorize("not(@pojistenecService.dontHavePojistenec(authentication))")
    public String renderPridejPojisteni(@ModelAttribute PojistenecDTO pojistenecDTO, Model model, Authentication authentication){
        boolean nabidkaJePrazdna = pojisteniService.nabidkaJePrazdna();
        if (model.containsAttribute("success")){
            model.addAttribute("success", model.asMap().get("success"));
        }
        model.addAttribute("nabidkaJePrazdna", nabidkaJePrazdna);
        if (!nabidkaJePrazdna) {
            pojistenecService.nactiNabidkuPojisteni(pojistenecDTO);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername()).get();
            pojistenecDTO.setUserId(userEntity.getId());
            HashMap<Long, PojisteniEntity> nabidka = pojistenecDTO.getNabidkaPojisteni();
            HashMap<Long, Boolean> maTotoPojisteni = new HashMap<>();
            Long nejvetsiIdPojisteni = nabidka.keySet().stream()
                    .max(Long::compare)
                    .orElse(null);
            for (long i = 0; i <= nejvetsiIdPojisteni; i++) {
                if (nabidka.containsKey(i)) {
                    pojistenecDTO.setIdVybranehoPojisteni(i);
                    maTotoPojisteni.put(i, pojistenecService.haveThisPojisteni(pojistenecDTO));
                }
            }
            model.addAttribute("nabidka", nabidka);
            model.addAttribute("maTotoPojisteni", maTotoPojisteni);
            return "user/nabidka";
        } else{
            return "user/nabidka";
        }
    }
    @PostMapping("/nabidka/{id}")
    public String pridejPojisteni(@PathVariable Long id, @ModelAttribute PojistenecDTO pojistenecDTO, Authentication authentication, @RequestParam("action") String action, RedirectAttributes redirectAttributes){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername()).get();
        pojistenecDTO.setUserId(userEntity.getId());
        pojistenecDTO.setIdVybranehoPojisteni(id);
        if("pridej".equals(action)) {
            pojistenecService.addPojisteniToPojistenec(pojistenecDTO);
            redirectAttributes.addFlashAttribute("success", "Byl jste úspěšně pojištěn");
        }else if("odstran".equals(action)){
            pojistenecService.deletePojisteniFromPojistenec(pojistenecDTO);
            redirectAttributes.addFlashAttribute("success", "Již nejste pojištěn");
        }
        return "redirect:/user/nabidka";
    }

}

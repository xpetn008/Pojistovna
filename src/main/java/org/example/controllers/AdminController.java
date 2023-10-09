package org.example.controllers;

import org.example.data.entitites.PojistenecEntity;
import org.example.data.entitites.PojisteniEntity;
import org.example.data.repositories.PojistenecRepository;
import org.example.data.repositories.PojisteniRepository;
import org.example.models.dto.PojistenecDTO;
import org.example.models.dto.PojisteniDTO;
import org.example.models.exceptions.DuplicateRodneCisloException;
import org.example.models.exceptions.FalseRCException;
import org.example.models.exceptions.SpatnyUdajException;
import org.example.models.services.PojistenecService;
import org.example.models.services.PojisteniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.HashMap;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private Long idUpravovanehoPojisteni;
    private Long idUpravovanehoPojistence;
    private boolean hledaniProbehlo;
    @Autowired
    private PojisteniService pojisteniService;
    @Autowired
    private PojisteniRepository pojisteniRepository;
    @Autowired
    private PojistenecRepository pojistenecRepository;
    @Autowired
    private PojistenecService pojistenecService;

    @GetMapping()
    public String renderAdminIndex(){
        return "admin/adminIndex";
    }
    @GetMapping("/pojistenci")
    public String renderPojistenci(@ModelAttribute PojistenecDTO pojistenecDTO, Model model){
        if (model.containsAttribute("success")){
            model.addAttribute("success", model.asMap().get("success"));
        }
        hledaniProbehlo = false;
        pojistenecService.nactiPojistence(pojistenecDTO);
        HashMap<Long, PojistenecEntity> pojistenci = pojistenecDTO.getPojistenci();
        model.addAttribute("pojistenci", pojistenci);
        model.addAttribute("hledaniProbehlo", hledaniProbehlo);
        return "admin/spravaPojistencu";
    }
    @PostMapping("/pojistenci")
    public String pojistenci(@ModelAttribute PojistenecDTO pojistenecDTO, Model model){
        hledaniProbehlo = true;
        model.addAttribute("hledaniProbehlo", hledaniProbehlo);
        HashMap<Long, PojistenecEntity> nalezenePojistenci = new HashMap<>();
        PojistenecEntity pojistenecEntity;
        try{
            pojistenecEntity = pojistenecRepository.findByRodneCislo(pojistenecDTO.getRodneCislo()).get();
            nalezenePojistenci.put(pojistenecEntity.getId(), pojistenecEntity);
        }catch (NoSuchElementException e){
            pojistenecEntity = null;
        }
        boolean nalezen = pojistenecEntity!=null;
        model.addAttribute("nalezenePojistenci", nalezenePojistenci);
        model.addAttribute("nalezen", nalezen);
        return "admin/spravaPojistencu";
    }
    @PostMapping("/pojistenci/uprav/{id}")
    public String upravaPojistence(@PathVariable Long id, @ModelAttribute PojistenecDTO pojistenecDTO, @RequestParam("action") String action, Model model, RedirectAttributes redirectAttributes){
        pojistenecDTO.setId(id);
        if ("update".equals(action)){
            return renderUpdate(id, pojistenecDTO);
        }else if("delete".equals(action)){
            pojistenecRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Profil pojištěnce byl vymazán");
        }
        return "redirect:/admin/pojistenci";
    }
    @GetMapping("/pojistenci/update")
    public String renderUpdate(Long id, @ModelAttribute PojistenecDTO pojistenecDTO){
        PojistenecEntity pojistenecEntity = pojistenecService.vratPojistencePodleId(id);
        pojistenecDTO.setId(id);
        pojistenecDTO.setJmeno(pojistenecEntity.getJmeno());
        pojistenecDTO.setPrijmeni(pojistenecEntity.getPrijmeni());
        pojistenecDTO.setRodneCislo(pojistenecEntity.getRodneCislo());
        pojistenecDTO.setTelefon(pojistenecEntity.getTelefon());
        pojistenecDTO.setUliceCp(pojistenecEntity.getUliceCp());
        pojistenecDTO.setMesto(pojistenecEntity.getMesto());
        pojistenecDTO.setPsc(pojistenecEntity.getPsc());
        idUpravovanehoPojistence = id;
        return "admin/updatePojistenec";
    }
    @PostMapping("/pojistenci/update")
    public String update (@ModelAttribute PojistenecDTO pojistenecDTO, BindingResult result){
        pojistenecDTO.setId(idUpravovanehoPojistence);
        try {
            pojistenecService.adminUpdate(pojistenecDTO);
        }catch (SpatnyUdajException e){
            result.rejectValue("data", "error", e.getMessage());
        }
        return renderUpdate(pojistenecDTO.getId(), pojistenecDTO);
    }
    @GetMapping("/pojistenci/new")
    public String renderNew(@ModelAttribute PojistenecDTO pojistenecDTO){
        return "admin/novyPojistenec";
    }
    @PostMapping("/pojistenci/new")
    public String novyPojistenec(@ModelAttribute PojistenecDTO pojistenecDTO, BindingResult result, RedirectAttributes redirectAttributes) throws DuplicateRodneCisloException, SpatnyUdajException, FalseRCException {
        List<Exception> errors = pojistenecService.create(pojistenecDTO);
        if(errors!=null) {
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
        }
        if(result.hasErrors()){
            return renderNew(pojistenecDTO);
        }
        redirectAttributes.addFlashAttribute("success", "Profil pojištěnce byl vytvořen");
        return "redirect:/admin/pojistenci";
    }
    @GetMapping("/pojisteni")
    public String renderPojisteni(@ModelAttribute PojisteniDTO pojisteniDTO, Model model){
        if (model.containsAttribute("success")){
            model.addAttribute("success", model.asMap().get("success"));
        }
        pojisteniService.nactiVsechnaPojisteni(pojisteniDTO);
        HashMap<Long, PojisteniEntity> pojisteni = pojisteniDTO.getPojisteni();
        model.addAttribute("pojisteni", pojisteni);
        return "admin/spravaPojisteni";
    }
    @PostMapping("/pojisteni/{id}")
    public String pojisteni(@PathVariable Long id, @ModelAttribute PojisteniDTO pojisteniDTO, @RequestParam("action") String action, Model model, @ModelAttribute PojistenecDTO pojistenecDTO, RedirectAttributes redirectAttributes){
        pojisteniDTO.setId(id);
        if ("update".equals(action)){
            return renderUpdatePojisteni(id, pojisteniDTO);
        }else if("delete".equals(action)){
            pojisteniRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Pojištění bylo vymazáno");
        }
        return "redirect:/admin/pojisteni";
    }
    @GetMapping("/pojisteni/update")
    public String renderUpdatePojisteni(Long id, @ModelAttribute PojisteniDTO pojisteniDTO){
        PojisteniEntity pojisteniEntity = pojisteniService.vratPojisteniPodleId(id);
        pojisteniDTO.setId(id);
        pojisteniDTO.setDruh(pojisteniEntity.getDruh());
        pojisteniDTO.setMaximalniCastka(pojisteniEntity.getMaximalniCastka());
        pojisteniDTO.setMesicniSplatka(pojisteniEntity.getMesicniCastka());
        pojisteniDTO.setData("");
        idUpravovanehoPojisteni = id;
        System.out.println("AdminController renderUpdatePojisteni method: id - "+idUpravovanehoPojisteni+", column - "+pojisteniDTO.getColumn()+", data - "+pojisteniDTO.getData());
        return "admin/updatePojisteni";
    }
    @PostMapping("/pojisteni/update")
    public String updatePojisteni (@ModelAttribute PojisteniDTO pojisteniDTO){
        pojisteniDTO.setId(idUpravovanehoPojisteni);
        pojisteniService.update(pojisteniDTO);
        return renderUpdatePojisteni(pojisteniDTO.getId(), pojisteniDTO);
    }
    @GetMapping("/pojisteni/new")
    public String renderNewPojisteni(@ModelAttribute PojisteniDTO pojisteniDTO){
        return "admin/novyPojisteni";
    }
    @PostMapping("/pojisteni/new")
    public String newPojisteni(@ModelAttribute PojisteniDTO pojisteniDTO, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("success", "Pojištění bylo vytvořeno");
        pojisteniService.create(pojisteniDTO);
        return "redirect:/admin/pojisteni";
    }

}

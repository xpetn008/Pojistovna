package org.example.controllers;

import jakarta.validation.Valid;
import org.example.models.dto.UserDTO;
import org.example.models.exceptions.DuplicateEmailException;
import org.example.models.exceptions.PasswordsDoNotEqualException;
import org.example.models.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccountController {
    @Autowired
    private UserService userService;
    @GetMapping("/prihlaseni")
    public String renderLogin(Model model) {
        if (model.containsAttribute("success")){
            model.addAttribute("success", model.asMap().get("success"));
        }
        return "account/login";
    }
    @GetMapping("/registrace")
    public String renderRegister(@ModelAttribute UserDTO userDTO){
        return "account/register";
    }
    @PostMapping("/registrace")
    public String register(@Valid @ModelAttribute UserDTO userDTO, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return renderRegister(userDTO);
        }
        try{
            userService.create(userDTO, false);
        }catch (DuplicateEmailException e){
            result.rejectValue("email", "error", e.getMessage());
            return "account/register";
        }catch (PasswordsDoNotEqualException e) {
            result.rejectValue("password", "error", "Hesla se nerovnají.");
            result.rejectValue("confirmPassword", "error", "Hesla se nerovnají.");
            return "account/register";
        }
        redirectAttributes.addFlashAttribute("success", "Uživatel zaregistrován");
        return "redirect:/prihlaseni";
    }
}

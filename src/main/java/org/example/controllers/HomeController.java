package org.example.controllers;

import org.example.data.entitites.UserEntity;
import org.example.data.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/")
    public String renderHomePage(Authentication authentication, Model model){
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername()).get();
            boolean admin = userEntity.isAdmin();
            model.addAttribute("admin", admin);
        }
        return "home/index";
    }
    @GetMapping("/kontakt")
    public String renderKontakt(Authentication authentication, Model model){
        if (authentication != null){
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername()).get();
            boolean admin = userEntity.isAdmin();
            model.addAttribute("admin", admin);
        }
        return "home/kontakt";
    }
}

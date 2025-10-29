package com.uade.ligafutbol.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    /**
     * 🏠 Página principal - redirige a gestión de BD
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/web/database-management";
    }
    

    

}
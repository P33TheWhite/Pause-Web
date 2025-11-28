package com.lapause.Pause_Web.controller;

import com.lapause.Pause_Web.repository.EvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventController {

    @Autowired
    private EvenementRepository evenementRepository;

    @GetMapping("/agenda")
    public String showAgenda(Model model) {
        var events = evenementRepository.findAll();
        
        model.addAttribute("events", events);
        
        return "event/agenda";
    }
}
package com.lapause.Pause_Web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeamController {

    @GetMapping("/equipe")
    public String team() {
        return "team";
    }
}

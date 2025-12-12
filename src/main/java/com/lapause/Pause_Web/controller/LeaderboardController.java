package com.lapause.Pause_Web.controller;

import com.lapause.Pause_Web.entity.User;
import com.lapause.Pause_Web.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class LeaderboardController {

    @Autowired
    private UserService userService;

    @GetMapping("/leaderboard")
    public String leaderboard(HttpSession session, Model model) {

        List<User> leaderboard = userService.getLeaderboard();
        model.addAttribute("leaderboard", leaderboard);
        return "leaderboard/leaderboard";
    }
}

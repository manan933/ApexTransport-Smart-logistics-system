package com.apextransport.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardingController {

    @GetMapping("/")
    public String index() {
        return "forward:/login.html";
    }

    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }

    @GetMapping(value = {"/transporter", "/transporter/**"})
    public String transporter() {
        return "forward:/transporter.html";
    }

    @GetMapping(value = {"/driver", "/driver/**"})
    public String driver() {
        return "forward:/driver.html";
    }

    @GetMapping(value = {"/admin", "/admin/**"})
    public String admin() {
        return "forward:/admin.html";
    }

    @GetMapping(value = {"/profile", "/profile/**"})
    public String profile() {
        return "forward:/profile.html";
    }
}

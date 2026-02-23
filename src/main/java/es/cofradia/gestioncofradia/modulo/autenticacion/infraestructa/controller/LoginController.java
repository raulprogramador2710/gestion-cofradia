package es.cofradia.gestioncofradia.modulo.autenticacion.infraestructa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // Esto buscará src/main/resources/templates/login.html
    }
}
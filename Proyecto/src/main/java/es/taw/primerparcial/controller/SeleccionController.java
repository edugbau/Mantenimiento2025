package es.taw.primerparcial.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SeleccionController {

    @GetMapping("/seleccion")
    public String seleccion() {
        return "seleccion";
    }
} 
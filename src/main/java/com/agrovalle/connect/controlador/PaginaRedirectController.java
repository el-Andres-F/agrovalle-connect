package com.agrovalle.connect.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaRedirectController {

    @GetMapping("/clientes/")
    public String paginaClientes() {
        return "forward:/clientes/index.html";
    }

    @GetMapping("/acceso/")
    public String paginaAcceso() {
        return "forward:/acceso/index.html";
    }

    @GetMapping("/registro/")
    public String paginaRegistro() {
        return "forward:/registro/index.html";
    }

    @GetMapping("/cuenta/")
    public String paginaCuenta() {
        return "forward:/cuenta/index.html";
    }

    @GetMapping("/carrito/")
    public String paginaCarrito() {
        return "forward:/carrito/index.html";
    }

    @GetMapping("/pedidos/")
    public String paginaPedidos() {
        return "forward:/pedidos/index.html";
    }

    @GetMapping("/clientes.html")
    public String clientes() {
        return "redirect:/clientes/";
    }

    @GetMapping("/acceso.html")
    public String acceso() {
        return "redirect:/acceso/";
    }

    @GetMapping("/registro.html")
    public String registro() {
        return "redirect:/registro/";
    }
}

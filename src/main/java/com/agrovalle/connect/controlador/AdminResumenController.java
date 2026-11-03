package com.agrovalle.connect.controlador;

import com.agrovalle.connect.servicio.AdminResumenService;
import com.agrovalle.connect.servicio.AutorizacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST adapter for the administrative summary view. */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminResumenController {

    private final AdminResumenService adminResumenService;

    private final AutorizacionService autorizacionService;

    public AdminResumenController(AdminResumenService adminResumenService, AutorizacionService autorizacionService) {
        this.adminResumenService = adminResumenService;
        this.autorizacionService = autorizacionService;
    }

    @GetMapping("/resumen")
    public AdminResumenService.Resumen resumen(HttpSession session) {
        autorizacionService.exigirAdministrador(session);
        return adminResumenService.resumen();
    }
}

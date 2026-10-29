package com.agrovalle.connect.controlador;

import com.agrovalle.connect.servicio.AutorizacionService;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    private final AutorizacionService autorizacionService;

    public AdminPageController(AutorizacionService autorizacionService) {
        this.autorizacionService = autorizacionService;
    }

    @GetMapping(value = "/admin.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<?> adminPage(HttpSession session) throws IOException {
        try {
            autorizacionService.exigirAdministrador(session);
        } catch (org.springframework.web.server.ResponseStatusException exception) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .location(URI.create("/acceso/?next=%2Fadmin.html"))
                    .build();
        }
        String page = new ClassPathResource("static/admin.html")
                .getContentAsString(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE)
                .body(page);
    }
}

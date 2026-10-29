package com.agrovalle.connect.servicio;

import com.agrovalle.connect.modelo.Administrador;
import com.agrovalle.connect.repositorio.AdministradorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InicializadorAdministrador implements ApplicationRunner {

    private final AdministradorRepository administradorRepository;
    private final String usuario;
    private final String contrasena;
    private final String nombre;
    private final String apellido;
    private final String correo;
    private final BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();

    public InicializadorAdministrador(
            AdministradorRepository administradorRepository,
            @Value("${agrovalle.admin.usuario:}") String usuario,
            @Value("${agrovalle.admin.contrasena:}") String contrasena,
            @Value("${agrovalle.admin.nombre:}") String nombre,
            @Value("${agrovalle.admin.apellido:}") String apellido,
            @Value("${agrovalle.admin.correo:}") String correo) {
        this.administradorRepository = administradorRepository;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuario.isBlank() || contrasena.isBlank()) {
            return;
        }
        String usuarioNormalizado = usuario.trim();
        Administrador administrador = administradorRepository.findByUsuario(usuarioNormalizado)
                .orElseGet(Administrador::new);
        administrador.setUsuario(usuarioNormalizado);
        administrador.setNombre(nombre.trim());
        administrador.setApellido(apellido.trim());
        administrador.setCorreo(correo.trim());
        if (administrador.getContrasenaHash() == null
                || !codificador.matches(contrasena, administrador.getContrasenaHash())) {
            administrador.setContrasenaHash(codificador.encode(contrasena));
        }
        administradorRepository.save(administrador);
    }
}

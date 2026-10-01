package com.agrovalle.connect.servicio;

import com.agrovalle.connect.dto.RegistroRequest;
import com.agrovalle.connect.dto.RegistroResponse;
import com.agrovalle.connect.dto.TipoUsuario;
import com.agrovalle.connect.excepcion.RegistroDuplicadoException;
import com.agrovalle.connect.excepcion.RegistroInvalidoException;
import com.agrovalle.connect.modelo.Comprador;
import com.agrovalle.connect.modelo.Productor;
import com.agrovalle.connect.repositorio.CompradorRepository;
import com.agrovalle.connect.repositorio.ProductorRepository;
import java.util.regex.Pattern;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Registro de productores (HU-01) y compradores (HU-06). */
@Service
public class RegistroService {

    private static final Pattern CEDULA = Pattern.compile("^\\d{6,10}$");
    private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern TELEFONO = Pattern.compile("^3\\d{9}$");
    private static final Pattern CONTRASENA = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");

    private final ProductorRepository productorRepository;
    private final CompradorRepository compradorRepository;
    private final BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();

    public RegistroService(ProductorRepository productorRepository,
            CompradorRepository compradorRepository) {
        this.productorRepository = productorRepository;
        this.compradorRepository = compradorRepository;
    }

    @Transactional
    public RegistroResponse registrar(RegistroRequest datos) {
        if (datos == null || datos.tipo() == null) {
            throw new RegistroInvalidoException("El campo 'tipo' es obligatorio (PRODUCTOR o COMPRADOR).");
        }
        return datos.tipo() == TipoUsuario.PRODUCTOR
                ? registrarProductor(datos)
                : registrarComprador(datos);
    }

    private RegistroResponse registrarProductor(RegistroRequest d) {
        exigirTexto(d.nombre(), "nombre");
        exigirTexto(d.ubicacion(), "ubicacion");
        if (d.cedula() == null || !CEDULA.matcher(d.cedula()).matches()) {
            throw new RegistroInvalidoException("La cedula debe tener entre 6 y 10 digitos.");
        }
        if (productorRepository.existsByCedula(d.cedula())) {
            throw new RegistroDuplicadoException("Ya existe un productor con esa cedula.");
        }
        // Ajustar a los campos reales de la entidad Productor si difieren.
        Productor productor = new Productor();
        productor.setNombre(d.nombre().trim());
        productor.setUbicacion(d.ubicacion().trim());
        productor.setCedula(d.cedula());
        Productor guardado = productorRepository.save(productor);
        return new RegistroResponse(guardado.getId(), TipoUsuario.PRODUCTOR, guardado.getNombre());
    }

    private RegistroResponse registrarComprador(RegistroRequest d) {
        exigirTexto(d.nombre(), "nombre");
        if (d.correo() == null || !CORREO.matcher(d.correo()).matches()) {
            throw new RegistroInvalidoException("El correo no tiene un formato valido.");
        }
        if (d.telefono() == null || !TELEFONO.matcher(d.telefono()).matches()) {
            throw new RegistroInvalidoException("El telefono debe ser un celular colombiano de 10 digitos.");
        }
        if (d.contrasena() == null || !CONTRASENA.matcher(d.contrasena()).matches()) {
            throw new RegistroInvalidoException(
                    "La contrasena debe tener minimo 8 caracteres, con letras y numeros.");
        }
        String correo = d.correo().trim().toLowerCase();
        if (compradorRepository.existsByCorreo(correo)) {
            throw new RegistroDuplicadoException("Ya existe una cuenta con ese correo.");
        }
        Comprador comprador = new Comprador();
        comprador.setNombre(d.nombre().trim());
        comprador.setCorreo(correo);
        comprador.setTelefono(d.telefono());
        comprador.setContrasenaHash(codificador.encode(d.contrasena()));
        Comprador guardado = compradorRepository.save(comprador);
        return new RegistroResponse(guardado.getId(), TipoUsuario.COMPRADOR, guardado.getNombre());
    }

    private void exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new RegistroInvalidoException("El campo '" + campo + "' es obligatorio.");
        }
    }
}

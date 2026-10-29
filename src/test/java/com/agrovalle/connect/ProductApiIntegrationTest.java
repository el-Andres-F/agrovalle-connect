package com.agrovalle.connect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import com.agrovalle.connect.modelo.Comprador;
import com.agrovalle.connect.modelo.Producto;
import com.agrovalle.connect.repositorio.CompradorRepository;
import com.agrovalle.connect.repositorio.ProductoRepository;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "agrovalle.admin.usuario=admin-test",
    "agrovalle.admin.contrasena=clave-temporal-test",
    "agrovalle.admin.nombre=Administrador",
    "agrovalle.admin.apellido=Prueba",
    "agrovalle.admin.correo=admin-test@example.com",
    "agrovalle.pse.public-key=",
    "agrovalle.pse.integrity-secret="
})
class ProductApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompradorRepository compradorRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void sessionSinAutenticarDebeSerPublicaYAnonima() throws Exception {
        mockMvc.perform(get("/api/v1/auth/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autenticado").value(false));
    }

    @Test
    void usuarioAnonimoNoPuedePublicarProductos() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Yuca","categoria":"VERDURAS","cantidad":3,
                                "fechaCosecha":"2026-10-20","precio":2000,"productorId":1}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void registroPublicoNoDebeCrearAdministradores() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"ADMIN","nombre":"Intruso","correo":"intruso@example.com",
                                "telefono":"3001234567","contrasena":"segura123"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void paginaDeGestionRedirigeSiNoHaySesionAdmin() throws Exception {
        mockMvc.perform(get("/admin.html"))
                .andExpect(status().isSeeOther())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                    .string("Location", "/acceso/?next=%2Fadmin.html"));
            }

            @Test
            void administradorPuedeIniciarSesionConNombreDeUsuario() throws Exception {
            var login = mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"tipo":"AUTO","usuario":"admin-test","contrasena":"clave-temporal-test"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("ADMIN"))
                .andReturn();

            MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
            mockMvc.perform(get("/api/v1/auth/profile").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Administrador"))
                .andExpect(jsonPath("$.apellido").value("Prueba"))
                .andExpect(jsonPath("$.correo").value("admin-test@example.com"));
            mockMvc.perform(get("/admin.html").session(session))
                .andExpect(status().isOk());
    }

            @Test
            void cerrarSesionAdminInvalidaAccesoAlPerfil() throws Exception {
            var login = mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"tipo":"AUTO","usuario":"admin-test","contrasena":"clave-temporal-test"}
                        """))
                .andExpect(status().isOk())
                .andReturn();
            MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

            mockMvc.perform(post("/api/v1/auth/logout").session(session))
                .andExpect(status().isNoContent());
            mockMvc.perform(get("/api/v1/auth/profile").session(session))
                .andExpect(status().isUnauthorized());
            }

    @Test
    void paginaInicialTiendaYAccesoOrganizadas() throws Exception {
        mockMvc.perform(get("/clientes/")).andExpect(status().isOk());
        mockMvc.perform(get("/acceso/")).andExpect(status().isOk());
        mockMvc.perform(get("/registro/")).andExpect(status().isOk());
        mockMvc.perform(get("/carrito/")).andExpect(status().isOk());
        mockMvc.perform(get("/cuenta/")).andExpect(status().isOk());
    }

    @Test
    @Transactional
    void pedidoSinPseSeGuardaPendienteSoloEnLaCuentaQueLoCreo() throws Exception {
        Comprador comprador = nuevoComprador("principal");
        Comprador otroComprador = nuevoComprador("secundario");
        Producto producto = productoRepository.save(new Producto(
                "Aguacate de prueba", "FRUTAS", 5, LocalDate.now(), 1200.0, null));

        MockHttpSession sesionComprador = sesionDe(comprador);
        String solicitud = """
                {"items":[{"productoId":%d,"cantidad":2}]}
                """.formatted(producto.getId());

        mockMvc.perform(post("/api/v1/pagos/pse")
                        .session(sesionComprador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(solicitud))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagoDisponible").value(false))
                .andExpect(jsonPath("$.referencia").isNotEmpty());

        mockMvc.perform(get("/api/v1/pedidos/mios").session(sesionComprador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PAGO_PENDIENTE"))
                .andExpect(jsonPath("$[0].detalle").value("Aguacate de prueba x 2"));

        mockMvc.perform(get("/api/v1/pedidos/mios").session(sesionDe(otroComprador)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    private Comprador nuevoComprador(String sufijo) {
        Comprador comprador = new Comprador();
        comprador.setNombre("Comprador " + sufijo);
        comprador.setApellido("Prueba");
        comprador.setCorreo("comprador-" + sufijo + "-" + java.util.UUID.randomUUID() + "@example.com");
        comprador.setTelefono("3001234567");
        comprador.setContrasenaHash("hash-no-usado-en-este-test");
        return compradorRepository.save(comprador);
    }

    private MockHttpSession sesionDe(Comprador comprador) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("tipoUsuario", "COMPRADOR");
        session.setAttribute("usuarioId", comprador.getId());
        return session;
    }
}

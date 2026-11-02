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
import com.agrovalle.connect.modelo.Productor;
import com.agrovalle.connect.repositorio.CompradorRepository;
import com.agrovalle.connect.repositorio.ProductoRepository;
import com.agrovalle.connect.repositorio.ProductorRepository;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=${TEST_DB_URL:jdbc:h2:mem:agrovalle-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1}",
    "spring.datasource.username=${TEST_DB_USERNAME:sa}",
    "spring.datasource.password=${TEST_DB_PASSWORD:}",
    "spring.sql.init.mode=never",
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

    @Autowired
    private ProductorRepository productorRepository;

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
            void compradorPuedeRegistrarseYUnCorreoNoSeDuplica() throws Exception {
            String correo = "comprador-" + java.util.UUID.randomUUID() + "@example.com";
            String solicitud = """
                {"tipo":"COMPRADOR","nombre":"Comprador API","correo":"%s",
                "telefono":"3001234567","contrasena":"segura123"}
                """.formatted(correo);

            mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(solicitud))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("COMPRADOR"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());

            mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(solicitud))
                .andExpect(status().isConflict());
            }

            @Test
            void administradorRegistraProductorYLaCedulaNoSeDuplica() throws Exception {
            String cedula = "9" + String.format("%09d",
                Math.floorMod(java.util.UUID.randomUUID().getLeastSignificantBits(), 1_000_000_000L));
            String solicitud = """
                {"nombre":"Productor de prueba","ubicacion":"Palmira","cedula":"%s","contacto":"3001234567"}
                """.formatted(cedula);
            MockHttpSession sesionAdmin = new MockHttpSession();
            sesionAdmin.setAttribute("tipoUsuario", "ADMIN");

            mockMvc.perform(post("/api/v1/productores")
                    .session(sesionAdmin)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(solicitud))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("PRODUCTOR"));
            assertTrue(productorRepository.existsByCedula(cedula));

            mockMvc.perform(post("/api/v1/productores")
                    .session(sesionAdmin)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(solicitud))
                .andExpect(status().isConflict());
            }

            @Test
            void administradorPublicaProductoYFiltraCatalogo() throws Exception {
            String cedula = "8" + String.format("%09d",
                Math.floorMod(java.util.UUID.randomUUID().getLeastSignificantBits(), 1_000_000_000L));
            Productor productor = productorRepository.save(new Productor("Productor API", "Palmira", cedula));
            MockHttpSession sesionAdmin = new MockHttpSession();
            sesionAdmin.setAttribute("tipoUsuario", "ADMIN");
            String solicitud = """
                {"nombre":"Limon","categoria":"frutas","cantidad":4,
                "fechaCosecha":"2026-10-20","precio":2500,"productorId":%d}
                """.formatted(productor.getId());

            mockMvc.perform(post("/api/v1/productos")
                    .session(sesionAdmin)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(solicitud))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoria").value("FRUTAS"));

            mockMvc.perform(get("/api/v1/productos").param("categoria", "frutas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Limon"));
            mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
            }

            @Test
            void administradorNoPuedeRegistrarCedulaInvalida() throws Exception {
            MockHttpSession sesionAdmin = new MockHttpSession();
            sesionAdmin.setAttribute("tipoUsuario", "ADMIN");

            mockMvc.perform(post("/api/v1/productores")
                    .session(sesionAdmin)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"nombre":"Productor de prueba","ubicacion":"Palmira","cedula":"12A"}
                        """))
                .andExpect(status().isBadRequest());
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
            mockMvc.perform(get("/api/v1/admin/resumen").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ventasSemana.length()").value(7));
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

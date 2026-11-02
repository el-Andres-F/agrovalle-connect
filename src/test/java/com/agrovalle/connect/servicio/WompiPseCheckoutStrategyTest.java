package com.agrovalle.connect.servicio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agrovalle.connect.modelo.Comprador;
import org.junit.jupiter.api.Test;

class WompiPseCheckoutStrategyTest {

    @Test
    void estrategiaSinCredencialesInformaIndisponibilidad() {
        CheckoutStrategy strategy = new WompiPseCheckoutStrategy("", "", "http://localhost/return",
                "https://checkout.wompi.co/p/");

        assertFalse(strategy.disponible());
    }

    @Test
    void estrategiaConCredencialesGeneraUrlFirmadaConDatosCodificados() {
        CheckoutStrategy strategy = new WompiPseCheckoutStrategy("pub_test", "secret_test",
                "https://app.example/retorno", "https://checkout.example/");
        Comprador comprador = new Comprador();
        comprador.setNombre("María José");
        comprador.setApellido("López");
        comprador.setCorreo("maria@example.com");

        String url = strategy.crearUrl("AV-123", 12500L, comprador);

        assertTrue(strategy.disponible());
        assertTrue(url.contains("public-key=pub_test"));
        assertTrue(url.contains("amount-in-cents=12500"));
        assertTrue(url.contains("reference=AV-123"));
        assertTrue(url.contains("signature%3Aintegrity="));
        assertTrue(url.contains("customer-data%3Aemail=maria%40example.com"));
    }
}

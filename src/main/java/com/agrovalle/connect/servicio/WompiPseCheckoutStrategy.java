package com.agrovalle.connect.servicio;

import com.agrovalle.connect.modelo.Comprador;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Wompi hosted-checkout implementation of the checkout strategy. */
@Component
public class WompiPseCheckoutStrategy implements CheckoutStrategy {

    private final String publicKey;
    private final String integritySecret;
    private final String redirectUrl;
    private final String checkoutBaseUrl;

    public WompiPseCheckoutStrategy(
            @Value("${agrovalle.pse.public-key:}") String publicKey,
            @Value("${agrovalle.pse.integrity-secret:}") String integritySecret,
            @Value("${agrovalle.pse.redirect-url:http://localhost:8080/clientes/?pago=retorno}") String redirectUrl,
            @Value("${agrovalle.pse.checkout-base-url:https://checkout.wompi.co/p/}") String checkoutBaseUrl) {
        this.publicKey = publicKey;
        this.integritySecret = integritySecret;
        this.redirectUrl = redirectUrl;
        this.checkoutBaseUrl = checkoutBaseUrl;
    }

    @Override
    public boolean disponible() {
        return !publicKey.isBlank() && !integritySecret.isBlank();
    }

    @Override
    public String crearUrl(String referencia, long totalCentavos, Comprador comprador) {
        String total = Long.toString(totalCentavos);
        String firma = sha256(referencia + total + "COP" + integritySecret);
        String nombreCompleto = (comprador.getNombre() + " "
                + (comprador.getApellido() == null ? "" : comprador.getApellido())).trim();
        return checkoutBaseUrl + "?public-key=" + encode(publicKey)
                + "&currency=COP&amount-in-cents=" + total
                + "&reference=" + encode(referencia)
                + "&signature%3Aintegrity=" + encode(firma)
                + "&redirect-url=" + encode(redirectUrl)
                + "&payment-method-type=PSE"
                + "&customer-data%3Aemail=" + encode(comprador.getCorreo())
                + "&customer-data%3Afull-name=" + encode(nombreCompleto);
    }

    private String sha256(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no está disponible.", exception);
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

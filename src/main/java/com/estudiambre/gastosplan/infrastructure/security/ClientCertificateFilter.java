package com.estudiambre.gastosplan.infrastructure.security;

import com.estudiambre.gastosplan.infrastructure.web.ErrorWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Patron del Lab 5: solo APIM puede llamar a este servicio.
 * App Service (con "Incoming client certificates = Require") pasa el certificado en el header
 * X-ARR-ClientCert (Base64). Se calcula su huella SHA-1 y se compara con CLIENT_CERT_THUMBPRINT.
 * /health y Swagger quedan fuera del filtro.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class ClientCertificateFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ClientCertificateFilter.class);
    static final String HEADER = "X-ARR-ClientCert";

    private final boolean requerido;
    private final String thumbprintEsperado;

    public ClientCertificateFilter(@Value("${app.security.client-cert.required:false}") boolean requerido,
                                   @Value("${app.security.client-cert.thumbprint:}") String thumbprint) {
        this.requerido = requerido;
        this.thumbprintEsperado = normalizar(thumbprint);
        if (!requerido) log.warn("Filtro de certificado DESACTIVADO (CLIENT_CERT_REQUIRED=false). Solo para local.");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !requerido
                || path.equals("/health")
                || path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HEADER);
        if (thumbprintEsperado.isEmpty() || header == null || header.isBlank()) {
            rechazar(response, "Certificado de cliente requerido");
            return;
        }
        String recibido;
        try {
            recibido = sha1(header);
        } catch (IllegalArgumentException e) {
            rechazar(response, "Certificado de cliente invalido");
            return;
        }
        if (!recibido.equals(thumbprintEsperado)) {
            log.warn("Thumbprint de certificado no coincide: {}", recibido);
            rechazar(response, "Certificado de cliente no autorizado");
            return;
        }
        chain.doFilter(request, response);
    }

    /** SHA-1 del certificado (DER) en hexadecimal mayuscula: es el thumbprint que muestra Azure. */
    static String sha1(String certBase64) {
        String limpio = certBase64
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(limpio);
        try {
            return HexFormat.of().withUpperCase().formatHex(MessageDigest.getInstance("SHA-1").digest(der));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    static String normalizar(String thumbprint) {
        return thumbprint == null ? "" : thumbprint.replace(":", "").replaceAll("\\s", "").toUpperCase();
    }

    private void rechazar(HttpServletResponse response, String mensaje) throws IOException {
        ErrorWriter.escribir(response, 403, mensaje);
    }
}

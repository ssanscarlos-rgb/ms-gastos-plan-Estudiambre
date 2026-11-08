package com.estudiambre.gastosplan.infrastructure.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * Saca el id del usuario (claim "sub") del token de Google que llega en Authorization: Bearer ...
 * No se revisa la firma: eso lo hace APIM con validate-jwt, y solo APIM puede llegar aqui (certificado).
 */
@Component
public class UsuarioActual {

    private final ObjectMapper json;

    public UsuarioActual(ObjectMapper json) {
        this.json = json;
    }

    public String id(String authorization) {
        if (authorization == null || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            throw new NoAutorizadoException("Falta el header Authorization: Bearer <token>");
        }
        String[] partes = authorization.substring(7).trim().split("\\.");
        if (partes.length < 2) throw new NoAutorizadoException("Token mal formado");
        try {
            JsonNode payload = json.readTree(Base64.getUrlDecoder().decode(partes[1]));
            String sub = payload.path("sub").asText("");
            if (sub.isBlank()) throw new NoAutorizadoException("El token no trae el claim sub");
            return sub;
        } catch (NoAutorizadoException e) {
            throw e;
        } catch (Exception e) {
            throw new NoAutorizadoException("Token mal formado");
        }
    }
}

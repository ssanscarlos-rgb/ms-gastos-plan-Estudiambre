package com.estudiambre.gastosplan.infrastructure.web;

import com.estudiambre.gastosplan.infrastructure.security.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

import java.io.IOException;

/** Escribe un error JSON {status, message, correlationId} desde un filtro (fuera de los controladores). */
public final class ErrorWriter {

    private ErrorWriter() {
    }

    public static void escribir(HttpServletResponse response, int status, String mensaje) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
        response.getWriter().write("{\"status\":" + status
                + ",\"message\":\"" + mensaje.replace("\"", "'") + "\""
                + ",\"correlationId\":" + (correlationId == null ? "null" : "\"" + correlationId + "\"") + "}");
    }
}

package com.estudiambre.gastosplan.infrastructure.web;

import com.estudiambre.gastosplan.application.port.in.ResumenUseCase;
import com.estudiambre.gastosplan.infrastructure.web.dto.ResumenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/resumen")
@Tag(name = "Resumen")
public class ResumenController {

    private final ResumenUseCase resumen;
    private final UsuarioActual usuario;

    public ResumenController(ResumenUseCase resumen, UsuarioActual usuario) {
        this.resumen = resumen;
        this.usuario = usuario;
    }

    @GetMapping
    @Operation(summary = "Datos del Panel: disponible, gastado, meta, mensaje y ultimos movimientos")
    public ResumenResponse obtener(
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth) {
        return ResumenResponse.de(resumen.obtener(usuario.id(auth)));
    }
}

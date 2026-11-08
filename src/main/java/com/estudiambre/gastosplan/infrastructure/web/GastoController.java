package com.estudiambre.gastosplan.infrastructure.web;

import com.estudiambre.gastosplan.application.port.in.GastosUseCase;
import com.estudiambre.gastosplan.domain.model.Pagina;
import com.estudiambre.gastosplan.infrastructure.web.dto.CrearGastoRequest;
import com.estudiambre.gastosplan.infrastructure.web.dto.EditarGastoRequest;
import com.estudiambre.gastosplan.infrastructure.web.dto.GastoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Adaptador de entrada (web): traduce HTTP a llamadas al puerto GastosUseCase. */
@RestController
@RequestMapping("/v1/gastos")
@Tag(name = "Gastos")
public class GastoController {

    private final GastosUseCase gastos;
    private final UsuarioActual usuario;

    public GastoController(GastosUseCase gastos, UsuarioActual usuario) {
        this.gastos = gastos;
        this.usuario = usuario;
    }

    @GetMapping
    @Operation(summary = "Lista paginada de gastos, mas recientes primero, filtrable por texto")
    public Pagina<GastoResponse> listar(
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String q) {
        return gastos.listar(usuario.id(auth), q, page, size).map(GastoResponse::de);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un gasto")
    public GastoResponse crear(
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @Valid @RequestBody CrearGastoRequest body) {
        return GastoResponse.de(gastos.crear(usuario.id(auth), body.descripcion(), body.monto(), body.categoria()));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Editar los campos enviados de un gasto")
    public GastoResponse editar(
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @PathVariable String id,
            @Valid @RequestBody EditarGastoRequest body) {
        return GastoResponse.de(gastos.editar(usuario.id(auth), id, body.descripcion(), body.monto(), body.categoria()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un gasto")
    public void eliminar(
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @PathVariable String id) {
        gastos.eliminar(usuario.id(auth), id);
    }
}

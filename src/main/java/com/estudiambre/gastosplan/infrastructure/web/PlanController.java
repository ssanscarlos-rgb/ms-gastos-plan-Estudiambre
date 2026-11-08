package com.estudiambre.gastosplan.infrastructure.web;

import com.estudiambre.gastosplan.application.port.in.PlanUseCase;
import com.estudiambre.gastosplan.domain.model.Categoria;
import com.estudiambre.gastosplan.infrastructure.web.dto.PlanPatchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/plan")
@Tag(name = "Plan")
public class PlanController {

    private final PlanUseCase plan;
    private final UsuarioActual usuario;

    public PlanController(PlanUseCase plan, UsuarioActual usuario) {
        this.plan = plan;
        this.usuario = usuario;
    }

    @GetMapping
    @Operation(summary = "Meta y categorias con presupuesto y gastado")
    public PlanUseCase.PlanConUso obtener(
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth) {
        return plan.obtener(usuario.id(auth));
    }

    @PatchMapping
    @Operation(summary = "Cambiar meta y/o categorias (arreglo completo)")
    public PlanUseCase.PlanConUso actualizar(
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @Valid @RequestBody PlanPatchRequest body) {
        List<Categoria> categorias = body.categorias() == null ? null
                : body.categorias().stream().map(c -> new Categoria(c.nombre(), c.presupuesto())).toList();
        return plan.actualizar(usuario.id(auth), body.meta(), categorias);
    }
}

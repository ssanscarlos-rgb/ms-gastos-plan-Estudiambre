package com.estudiambre.gastosplan.application.service;

/** Limites de paginacion comunes: pagina base 0, tamano entre 1 y 50 (10 por defecto). */
final class Paginacion {
    static final int SIZE_POR_DEFECTO = 10;
    static final int SIZE_MAXIMO = 50;

    private Paginacion() {
    }

    static int page(int page) {
        return Math.max(page, 0);
    }

    static int size(int size) {
        if (size <= 0) return SIZE_POR_DEFECTO;
        return Math.min(size, SIZE_MAXIMO);
    }
}

package com.usta.reparacionautomotriz.exceptions;

/**
 * campo  = campo del formulario donde se muestra el error (null = error general)
 * codigo = clave de messages_*.properties (así el mensaje sale en el idioma activo)
 * args   = valores que reemplazan {0}, {1}... en el mensaje
 */
public class ReglaNegocioException extends RuntimeException {
    private final String campo;
    private final String codigo;
    private final Object[] args;

    public ReglaNegocioException(String campo, String codigo, Object... args) {
        super(codigo);
        this.campo = campo;
        this.codigo = codigo;
        this.args = args;
    }

    public String getCampo() {
        return campo;
    }

    public String getCodigo() {
        return codigo;
    }

    public Object[] getArgs() {
        return args;
    }
}
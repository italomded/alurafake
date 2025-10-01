package br.com.alura.AluraFake.util;

import org.springframework.util.Assert;

public class ErrorItemException extends RuntimeException {
    private final String field;

    public ErrorItemException(String field, String message) {
        super(message);
        Assert.notNull(field, "field description must not be null");
        Assert.isTrue(!field.isEmpty(), "field description must not be null");
        Assert.notNull(message, "message description must not be null");
        Assert.isTrue(!message.isEmpty(), "message description must not be null");
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public ErrorItemDTO toModel() {
        return new ErrorItemDTO(field, getMessage());
    }
}

package br.com.alura.AluraFake.task;

import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NewChoiceTaskDto extends NewOpenTextTaskDto {
    @NotNull @Valid
    @Size(min = 2, max = 5)
    Set<NewOptionDto> options;

    public NewChoiceTaskDto() {
    }

    public NewChoiceTaskDto(Set<NewOptionDto> options) {
        super();
        this.options = options;
    }

    public Set<NewOptionDto> getOptions() {
        return options;
    }

    public void setOptions(Set<NewOptionDto> options) {
        this.options = options;
    }
}

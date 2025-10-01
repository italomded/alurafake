package br.com.alura.AluraFake.task;

import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NewMultipleChoiceTaskDto extends NewBaseTaskDto {
    @NotNull @Valid
    @Size(min = 3, max = 5)
    private Set<NewOptionDto> options;

    public NewMultipleChoiceTaskDto() {
    }

    public NewMultipleChoiceTaskDto(Set<NewOptionDto> options) {
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

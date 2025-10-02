package br.com.alura.AluraFake.task;

import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NewMultipleChoiceTaskDTO extends NewBaseTaskDTO {
    @NotNull @Valid
    @Size(min = 3, max = 5)
    private Set<NewOptionDTO> options;

    public NewMultipleChoiceTaskDTO() {
    }

    public NewMultipleChoiceTaskDTO(Set<NewOptionDTO> options) {
        super();
        this.options = options;
    }

    public Set<NewOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(Set<NewOptionDTO> options) {
        this.options = options;
    }
}

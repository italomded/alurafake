package br.com.alura.AluraFake.task;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NewOptionDto {
    @NotBlank
    @Length(min = 4, max = 80)
    String option;
    @NotNull
    Boolean isCorrect;

    public NewOptionDto() {
    }

    public NewOptionDto(String option, Boolean isCorrect) {
        this.option = option;
        this.isCorrect = isCorrect;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public Option toModel() {
        return new Option(option, isCorrect);
    }
}

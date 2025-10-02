package br.com.alura.AluraFake.task;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class NewBaseTaskDTO {
    @NotNull
    @Positive
    private Long courseId;
    @NotBlank
    @Length(min = 4, max = 255)
    private String statement;
    @NotNull
    @Positive
    private Integer order;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}

package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;

import java.util.Set;

import org.springframework.util.Assert;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SINGLE_CHOICE")
public class SingleChoiceTask extends TaskWithOptions {
    @Deprecated
    public SingleChoiceTask() {
    }

    public SingleChoiceTask(String statement, Course course, Integer order, Set<Option> options) {
        super(statement, course, order, Type.SINGLE_CHOICE, options);
        long correctAlternativesCount = options.stream().filter(Option::getCorrect).count();
        Assert.isTrue(correctAlternativesCount == 1, "The task must have only one correct option");
    }
}

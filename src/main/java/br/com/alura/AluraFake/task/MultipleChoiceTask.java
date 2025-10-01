package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;

import java.util.Set;

import org.springframework.util.Assert;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoiceTask extends TaskWithOptions {
    @Deprecated
    public MultipleChoiceTask() {
    }

    public MultipleChoiceTask(String statement, Course course, Integer order, Set<Option> options) {
        super(statement, course, order, Type.SINGLE_CHOICE, options);
        long correctAlternativesCount = 0;
        long wrongAlternativesCount = 0;
        for (Option option : options) {
            if (option.getCorrect()) {
                correctAlternativesCount++;
            } else {
                wrongAlternativesCount++;
            }
        }
        Assert.isTrue(correctAlternativesCount > 1, "The task must have more than one correct option");
        Assert.isTrue(wrongAlternativesCount > 0, "The task must have at least one wrong option");
    }
}

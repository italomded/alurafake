package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("OPEN_TEXT")
public class OpenTextTask extends Task {
    @Deprecated
    public OpenTextTask() {
    }

    public OpenTextTask(String statement, Course course, Integer order) {
        super(statement, course, order, Type.OPEN_TEXT);
    }
}

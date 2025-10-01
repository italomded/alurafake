package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private String statement;
    @Column(insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column
    private Integer taskOrder;

    @ManyToOne(optional = false)
    private Course course;

    @Deprecated
    public Task() {
    }

    protected Task(String statement, Course course, Integer taskOrder, Type type) {
        this.statement = statement;
        this.course = course;
        this.taskOrder = taskOrder;
        this.type = type;
    }

    public Integer getTaskOrder() {
        return taskOrder;
    }

    public Course getCourse() {
        return course;
    }

    public String getStatement() {
        return statement;
    }
}

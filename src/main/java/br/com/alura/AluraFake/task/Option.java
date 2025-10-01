package br.com.alura.AluraFake.task;

import java.time.LocalDateTime;

import org.springframework.util.Assert;

import jakarta.persistence.*;

@Entity
@Table(name = "Options")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "optionTitle")
    private String option;
    @Column
    private Boolean isCorrect;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Task task;

    @Deprecated
    public Option() {
    }

    public Option(String option, Boolean isCorrect, Task task) {
        this.option = option;
        this.isCorrect = isCorrect;
        this.task = task;
    }

    public Option(String option, Boolean isCorrect) {
        this.option = option;
        this.isCorrect = isCorrect;
    }

    public String getOption() {
        return option;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setTask(Task task) {
        Assert.isNull(this.task, "Task already defined");
        this.task = task;
    }
}

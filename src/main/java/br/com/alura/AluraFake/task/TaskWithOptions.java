package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.Assert;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;

@MappedSuperclass
public abstract class TaskWithOptions extends Task {
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "task")
    private Set<Option> options;

    @Deprecated
    public TaskWithOptions() {
    }

    protected TaskWithOptions(String statement, Course course, Integer taskOrder, Type type, Set<Option> options) {
        super(statement, course, taskOrder, type);

        Set<String> onlyUniqueOptionsTitle = new HashSet<>();
        for (Option option : options) {
            onlyUniqueOptionsTitle.add(option.getOption().toUpperCase());
            Assert.isTrue(!statement.equalsIgnoreCase(option.getOption()), "The option cannot be the same as the task statement");
            option.setTask(this);
        }

        Assert.isTrue(onlyUniqueOptionsTitle.size() == options.size(), "The options cannot be equal to each other");

        this.options = options;
    }

    public Set<Option> getOptions() {
        return Collections.unmodifiableSet(options);
    }
}

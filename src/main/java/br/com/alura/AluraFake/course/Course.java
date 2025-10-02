package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.user.User;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.util.Assert;

import jakarta.persistence.*;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String title;
    private String description;
    @ManyToOne
    private User instructor;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime publishedAt;
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Task> task;

    @Deprecated
    public Course() {
    }

    public Course(String title, String description, User instructor) {
        Assert.isTrue(instructor.isInstructor(), "Usuario deve ser um instrutor");
        this.title = title;
        this.instructor = instructor;
        this.description = description;
        this.status = Status.BUILDING;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

//    public void setStatus(Status status) {
//        this.status = status;
//    }

    public User getInstructor() {
        return instructor;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public boolean isOnBuilding() {
        return Status.BUILDING.equals(this.status);
    }

    protected boolean publish(boolean areTheTasksInOrder, boolean hasAllTaskTypes) {
        Assert.isTrue(areTheTasksInOrder, "The course can only be published if the tasks are in order");
        Assert.isTrue(hasAllTaskTypes, "Must contain at least one task of each type");
        Assert.isTrue(isOnBuilding(), "The course can only be published if the status is BUILDING");
        status = Status.PUBLISHED;
        publishedAt = LocalDateTime.now();
        return true;
    }

}

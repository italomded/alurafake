package br.com.alura.AluraFake.course;

import java.time.LocalDateTime;

public class CourseReportImplDTO implements CourseReportDto {
    Long id;
    String title;
    Status status;
    LocalDateTime publishedAt;
    Long taskQuantity;

    public CourseReportImplDTO(Long id, String title, Status status, LocalDateTime publishedAt, Long taskQuantity) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.publishedAt = publishedAt;
        this.taskQuantity = taskQuantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Long getTaskQuantity() {
        return taskQuantity;
    }

    public void setTaskQuantity(Long taskQuantity) {
        this.taskQuantity = taskQuantity;
    }
}

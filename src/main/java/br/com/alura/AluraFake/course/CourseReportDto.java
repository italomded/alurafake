package br.com.alura.AluraFake.course;

import java.time.LocalDateTime;

public interface CourseReportDto {
    Long getId();

    String getTitle();

    Status getStatus();

    LocalDateTime getPublishedAt();

    Long getTaskQuantity();
}

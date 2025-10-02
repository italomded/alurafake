package br.com.alura.AluraFake.course;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("""
            SELECT
                c.id AS id, c.title AS title, c.status AS status, c.publishedAt AS publishedAt, COUNT(t.id) AS taskQuantity
            FROM Course c
                LEFT JOIN c.task t
            WHERE
                c.instructor.id = :id
            GROUP BY
                c.id, c.title, c.status, c.publishedAt
            """)
    Set<CourseReportDTO> retrieveReportByInstructorId(Long id);

    long countByInstructorId(Long instructorId);
}

package br.com.alura.AluraFake.task;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByCourseIdAndStatement(Long courseId, String statement);

    Optional<Task> findTopByCourseIdOrderByTaskOrder_Desc(Long courseId);

    @Modifying
    @Query("update Task t set t.taskOrder = t.taskOrder + 1 where t.course.id = :courseId and t.taskOrder >= :cursor")
    void updateIncrementTaskOrderByCourseIdAndCursor(Long courseId, Integer cursor);
}

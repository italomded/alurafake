package br.com.alura.AluraFake.task;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByCourseIdAndStatement(Long courseId, String statement);

    Optional<Task> findTopByCourseIdOrderByTaskOrder_Desc(Long courseId);

    @Modifying
    @Query("UPDATE Task t SET t.taskOrder = t.taskOrder + 1 WHERE t.course.id = :courseId AND t.taskOrder >= :cursor")
    void updateIncrementTaskOrderByCourseIdAndCursor(Long courseId, Integer cursor);

    @Query("SELECT DISTINCT t.type FROM Task t WHERE t.course.id = :courseId")
    Set<Type> findDistinctTaskTypesForCourse(Long courseId);

    @Query("SELECT COUNT(t), MIN(t.taskOrder), MAX(t.taskOrder) FROM Task t WHERE t.course.id = :courseId")
    List<Long[]> validateTaskSequenceByCourse(Long courseId);
}

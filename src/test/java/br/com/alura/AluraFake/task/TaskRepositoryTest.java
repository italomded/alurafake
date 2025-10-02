package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void updateIncrementTaskOrderByCourseIdAndCursor__should_reorder_based_on_cursor() {
        User instructor = em.persist(new User("Caio", "caio@alura.com.br", Role.INSTRUCTOR));
        Course course = em.persist(new Course("Test course", "Course description", instructor));

        OpenTextTask task1 = em.persist(new OpenTextTask("Task one", course, 1));
        OpenTextTask task2 = em.persist(new OpenTextTask("Task two", course, 2));
        OpenTextTask task3 = em.persist(new OpenTextTask("Task three", course, 3));
        OpenTextTask task4 = em.persist(new OpenTextTask("Task four", course, 4));

        taskRepository.updateIncrementTaskOrderByCourseIdAndCursor(course.getId(), 2);
        em.refresh(task1);
        em.refresh(task2);
        em.refresh(task3);
        em.refresh(task4);

        assertThat(task1.getTaskOrder()).isEqualTo(1);
        assertThat(task2.getTaskOrder()).isEqualTo(3);
        assertThat(task3.getTaskOrder()).isEqualTo(4);
        assertThat(task4.getTaskOrder()).isEqualTo(5);

    }

    @Test
    void existsByCourseIdAndStatement__should_return_true_when_statement_already_exists() {
        User instructor = em.persist(new User("Caio", "caio@alura.com.br", Role.INSTRUCTOR));
        Course course = em.persist(new Course("Test course", "Course description", instructor));
        em.persist(new OpenTextTask("Test task", course, 1));
        boolean existsTest1 = taskRepository.existsByCourseIdAndStatement(course.getId(), "Test task");
        assertThat(existsTest1).isTrue();
        boolean existsTest2 = taskRepository.existsByCourseIdAndStatement(course.getId(), "Other task");
        assertThat(existsTest2).isFalse();
    }

    @Test
    void findTopByCourseIdOrderByTaskOrder_Desc__should_return_task_with_highest_order() {
        User instructor = em.persist(new User("Caio", "caio@alura.com.br", Role.INSTRUCTOR));
        Course course = em.persist(new Course("Test course", "Course description", instructor));
        em.persist(new OpenTextTask("Task one", course, 1));
        em.persist(new OpenTextTask("Task two", course, 2));
        em.persist(new OpenTextTask("Task three", course, 3));
        em.persist(new OpenTextTask("Task four", course, 4));
        Optional<Task> topTask = taskRepository.findTopByCourseIdOrderByTaskOrder_Desc(course.getId());
        assertThat(topTask.isPresent()).isTrue();
        assertThat(topTask.map(Task::getTaskOrder).orElse(null)).isEqualTo(4);
    }

    @Test
    void findDistinctTaskTypesForCourse_Desc__should_return_three_types_of_task() {
        User instructor = em.persist(new User("Caio", "caio@alura.com.br", Role.INSTRUCTOR));
        Course course = em.persist(new Course("Test course", "Course description", instructor));
        em.persist(new OpenTextTask("Task one", course, 1));
        em.persist(new MultipleChoiceTask("Task two", course, 2, Set.of(
                new Option("ABC", false),
                new Option("ABCD", true),
                new Option("ABCDE", true)
        )));
        em.persist(new SingleChoiceTask("Task three", course, 3, Set.of(
                new Option("ABC", false),
                new Option("ABCD", true)
        )));
        Set<Type> taskTypes = taskRepository.findDistinctTaskTypesForCourse(course.getId());
        assertThat(taskTypes.containsAll(Arrays.stream(Type.values()).toList())).isTrue();
    }

    @Test
    void findDistinctTaskTypesForCourse_Desc__should_return_two_types_of_task() {
        User instructor = em.persist(new User("Caio", "caio@alura.com.br", Role.INSTRUCTOR));
        Course course = em.persist(new Course("Test course", "Course description", instructor));
        em.persist(new OpenTextTask("Task one", course, 1));
        em.persist(new OpenTextTask("Task two", course, 2));
        em.persist(new SingleChoiceTask("Task three", course, 3, Set.of(
                new Option("ABC", false),
                new Option("ABCD", true)
        )));
        Set<Type> taskTypes = taskRepository.findDistinctTaskTypesForCourse(course.getId());
        assertThat(taskTypes.containsAll(Set.of(Type.OPEN_TEXT, Type.SINGLE_CHOICE))).isTrue();
    }

    @Test
    void validateTaskSequenceByCourse__should_return_three_fields_with_count_min_and_max() {
        User instructor = em.persist(new User("Caio", "caio@alura.com.br", Role.INSTRUCTOR));
        Course course = em.persist(new Course("Test course", "Course description", instructor));
        em.persist(new OpenTextTask("Task one", course, 1));
        em.persist(new OpenTextTask("Task two", course, 2));
        em.persist(new SingleChoiceTask("Task three", course, 3, Set.of(
                new Option("ABC", false),
                new Option("ABCD", true)
        )));
        List<Long[]> values = taskRepository.validateTaskSequenceByCourse(course.getId());
        assertThat(values.isEmpty()).isFalse();
        assertThat(values.getFirst().length).isEqualTo(3);
    }
}
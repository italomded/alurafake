package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.NewBaseTaskDTO;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.util.ErrorItemException;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseTaskDomainServiceTest {
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CourseTaskDomainService service;

    @Test
    void validateUniqueStatementForCourse__should_throw_exception_when_statement_already_exists() {
        NewBaseTaskDTO taskDTO = new NewBaseTaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("What is the JVM in Java?");

        doReturn(true).when(taskRepository).existsByCourseIdAndStatement(eq(taskDTO.getCourseId()), eq(taskDTO.getStatement()));
        Course course = mock(Course.class);
        doReturn(taskDTO.getCourseId()).when(course).getId();

        ErrorItemException errorItemException = assertThrows(ErrorItemException.class, () -> service.validateUniqueStatementForCourse(course, taskDTO.getStatement()));

        verify(taskRepository, times(1)).existsByCourseIdAndStatement(eq(taskDTO.getCourseId()), eq(taskDTO.getStatement()));
        assertEquals("statement", errorItemException.getField());
        assertNotNull(errorItemException.getMessage());
    }

    @Test
    void validateUniqueStatementForCourse__should_pass_if_statement_does_not_exist() {
        NewBaseTaskDTO taskDTO = new NewBaseTaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("What is the JVM in Java?");

        doReturn(false).when(taskRepository).existsByCourseIdAndStatement(eq(taskDTO.getCourseId()), eq(taskDTO.getStatement()));
        Course course = mock(Course.class);
        doReturn(taskDTO.getCourseId()).when(course).getId();

        service.validateUniqueStatementForCourse(course, taskDTO.getStatement());

        verify(taskRepository, times(1)).existsByCourseIdAndStatement(eq(taskDTO.getCourseId()), eq(taskDTO.getStatement()));
    }

    @Test
    void getCourseIfCanReceiveTask__should_return_bad_request_when_course_not_in_building_status() {
        NewBaseTaskDTO taskDTO = new NewBaseTaskDTO();
        taskDTO.setCourseId(1L);

        Course course = mock(Course.class);
        doReturn(false).when(course).isOnBuilding();
        doReturn(Optional.of(course)).when(courseRepository).findById(taskDTO.getCourseId());

        ErrorItemException errorItemException = assertThrows(ErrorItemException.class, () -> service.getCourseIfCanReceiveTask(taskDTO.getCourseId()));

        verify(courseRepository, times(1)).findById(eq(taskDTO.getCourseId()));
        assertEquals("courseId", errorItemException.getField());
        assertNotNull(errorItemException.getMessage());
    }

    @Test
    void getCourseIfCanReceiveTask__should_pass_when_course_on_building_status() {
        NewBaseTaskDTO taskDTO = new NewBaseTaskDTO();
        taskDTO.setCourseId(1L);

        Course course = mock(Course.class);
        doReturn(true).when(course).isOnBuilding();
        doReturn(Optional.of(course)).when(courseRepository).findById(eq(taskDTO.getCourseId()));

        Course returnedCourse = service.getCourseIfCanReceiveTask(taskDTO.getCourseId());

        verify(courseRepository, times(1)).findById(eq(taskDTO.getCourseId()));
        assertNotNull(returnedCourse);
    }

    @Test
    void validateTaskOrderAndReorder__should_return_bad_request_when_out_of_order() {
        Course course = mock(Course.class);
        doReturn(1L).when(course).getId();

        Task topTask = mock(Task.class);
        doReturn(1).when(topTask).getTaskOrder();
        doReturn(Optional.of(topTask)).when(taskRepository).findTopByCourseIdOrderByTaskOrder_Desc(eq(1L));

        ErrorItemException errorItemException = assertThrows(ErrorItemException.class, () -> service.validateTaskOrderAndReorder(course, 3));

        verify(taskRepository, times(1)).findTopByCourseIdOrderByTaskOrder_Desc(eq(1L));
        assertEquals("order", errorItemException.getField());
        assertNotNull(errorItemException.getMessage());
    }

    @Test
    void validateTaskOrderAndReorder__should_pass_and_reorders_when_order_already_exists() {
        Course course = mock(Course.class);
        doReturn(1L).when(course).getId();

        Task topTask = mock(Task.class);
        doReturn(1).when(topTask).getTaskOrder();
        doReturn(Optional.of(topTask)).when(taskRepository).findTopByCourseIdOrderByTaskOrder_Desc(eq(1L));

        service.validateTaskOrderAndReorder(course, 1);

        verify(taskRepository, times(1)).findTopByCourseIdOrderByTaskOrder_Desc(eq(1L));
        verify(taskRepository, times(1)).updateIncrementTaskOrderByCourseIdAndCursor(eq(1L), eq(1));
    }

    @Test
    void validateCourseContainsAllTaskTypes__should_return_false_when_doesnt_have_all_types() {
        Course course = mock(Course.class);
        doReturn(1L).when(course).getId();
        doReturn(Set.of(Type.SINGLE_CHOICE, Type.MULTIPLE_CHOICE)).when(taskRepository).findDistinctTaskTypesForCourse(eq(1L));

        boolean validated = service.validateCourseContainsAllTaskTypes(course);
        assertFalse(validated);
        verify(taskRepository, times(1)).findDistinctTaskTypesForCourse(eq(1L));
    }

    @Test
    void validateCourseContainsAllTaskTypes__should_return_true_has_all_types() {
        Course course = mock(Course.class);
        doReturn(1L).when(course).getId();
        doReturn(Arrays.stream(Type.values()).collect(Collectors.toSet())).when(taskRepository).findDistinctTaskTypesForCourse(eq(1L));

        boolean validated = service.validateCourseContainsAllTaskTypes(course);
        assertTrue(validated);
        verify(taskRepository, times(1)).findDistinctTaskTypesForCourse(eq(1L));
    }

    @Test
    void validateTaskOrderForCourse__should_return_false_when_is_not_in_order() {
        Course course = mock(Course.class);
        doReturn(1L).when(course).getId();
        doReturn(List.<Long[]>of(new Long[]{5L, 1L, 4L})).when(taskRepository).validateTaskSequenceByCourse(eq(1L));
        boolean validated = service.validateTaskOrderForCourse(course);
        assertFalse(validated);
        verify(taskRepository, times(1)).validateTaskSequenceByCourse(eq(1L));
    }

    @Test
    void validateTaskOrderForCourse__should_return_false_when_has_no_elements() {
        Course course = mock(Course.class);
        doReturn(1L).when(course).getId();
        doReturn(Collections.emptyList()).when(taskRepository).validateTaskSequenceByCourse(eq(1L));
        boolean validated = service.validateTaskOrderForCourse(course);
        assertFalse(validated);
        verify(taskRepository, times(1)).validateTaskSequenceByCourse(eq(1L));
    }

    @Test
    void validateTaskOrderForCourse__should_return_false_when_min_order_is_not_1() {
        Course course = mock(Course.class);
        doReturn(1L).when(course).getId();
        doReturn(List.<Long[]>of(new Long[]{5L, 3L, 5L})).when(taskRepository).validateTaskSequenceByCourse(eq(1L));
        boolean validated = service.validateTaskOrderForCourse(course);
        assertFalse(validated);
        verify(taskRepository, times(1)).validateTaskSequenceByCourse(eq(1L));
    }

    @Test
    void validateTaskOrderForCourse__should_return_true_if_tasks_are_in_order() {
        Course course = mock(Course.class);
        doReturn(1L).when(course).getId();
        doReturn(List.<Long[]>of(new Long[]{5L, 1L, 5L})).when(taskRepository).validateTaskSequenceByCourse(eq(1L));
        boolean validated = service.validateTaskOrderForCourse(course);
        assertTrue(validated);
        verify(taskRepository, times(1)).validateTaskSequenceByCourse(eq(1L));
    }
}

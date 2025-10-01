package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.NewBaseTaskDto;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.util.ErrorItemException;

import java.util.Optional;

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
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");

        doReturn(true).when(taskRepository).existsByCourseIdAndStatement(eq(taskDto.getCourseId()), eq(taskDto.getStatement()));
        Course course = mock(Course.class);
        doReturn(taskDto.getCourseId()).when(course).getId();

        ErrorItemException errorItemException = assertThrows(ErrorItemException.class, () -> service.validateUniqueStatementForCourse(course, taskDto.getStatement()));

        verify(taskRepository, times(1)).existsByCourseIdAndStatement(eq(taskDto.getCourseId()), eq(taskDto.getStatement()));
        assertEquals("statement", errorItemException.getField());
        assertNotNull(errorItemException.getMessage());
    }

    @Test
    void validateUniqueStatementForCourse__should_pass_if_statement_does_not_exist() {
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");

        doReturn(false).when(taskRepository).existsByCourseIdAndStatement(eq(taskDto.getCourseId()), eq(taskDto.getStatement()));
        Course course = mock(Course.class);
        doReturn(taskDto.getCourseId()).when(course).getId();

        service.validateUniqueStatementForCourse(course, taskDto.getStatement());

        verify(taskRepository, times(1)).existsByCourseIdAndStatement(eq(taskDto.getCourseId()), eq(taskDto.getStatement()));
    }

    @Test
    void getCourseIfCanReceiveTask__should_return_bad_request_when_course_not_in_building_status() {
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
        taskDto.setCourseId(1L);

        Course course = mock(Course.class);
        doReturn(false).when(course).isOnBuilding();
        doReturn(Optional.of(course)).when(courseRepository).findById(taskDto.getCourseId());

        ErrorItemException errorItemException = assertThrows(ErrorItemException.class, () -> service.getCourseIfCanReceiveTask(taskDto.getCourseId()));

        verify(courseRepository, times(1)).findById(eq(taskDto.getCourseId()));
        assertEquals("courseId", errorItemException.getField());
        assertNotNull(errorItemException.getMessage());
    }

    @Test
    void getCourseIfCanReceiveTask__should_pass_when_course_on_building_status() {
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
        taskDto.setCourseId(1L);

        Course course = mock(Course.class);
        doReturn(true).when(course).isOnBuilding();
        doReturn(Optional.of(course)).when(courseRepository).findById(eq(taskDto.getCourseId()));

        Course returnedCourse = service.getCourseIfCanReceiveTask(taskDto.getCourseId());

        verify(courseRepository, times(1)).findById(eq(taskDto.getCourseId()));
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

}

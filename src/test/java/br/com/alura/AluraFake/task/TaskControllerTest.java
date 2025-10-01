package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.CourseTaskDomainService;
import br.com.alura.AluraFake.util.ErrorItemException;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseTaskDomainService courseTaskDomainService;

    @MockBean
    private CourseRepository courseRepository;
    @MockBean
    private TaskRepository taskRepository;

    @Test
    void newOpenTextTaskDto__should_return_bad_request_when_statement_already_registered() throws Exception {
        NewOpenTextTaskDto taskDto = new NewOpenTextTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");

        Course course = mock(Course.class);
        doReturn(course).when(courseTaskDomainService).getCourseIfCanReceiveTask(eq(1L));
        doThrow(new ErrorItemException("statement", "msg")).when(courseTaskDomainService).validateUniqueStatementForCourse(eq(course), eq(taskDto.getStatement()));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("statement"))
                .andExpect(jsonPath("$.message").isNotEmpty());

        verify(courseTaskDomainService, times(1)).validateUniqueStatementForCourse(any(), anyString());
    }

    @Test
    void newOpenTextTaskDto__should_return_bad_request_when_course_not_in_building_status() throws Exception {
        NewOpenTextTaskDto taskDto = new NewOpenTextTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");

        doThrow(new ErrorItemException("courseId", "msg")).when(courseTaskDomainService).getCourseIfCanReceiveTask(eq(1L));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").isNotEmpty());

        verify(courseTaskDomainService, times(1)).getCourseIfCanReceiveTask(anyLong());
    }

    @Test
    void newOpenTextTaskDto__should_return_bad_request_when_out_of_order() throws Exception {
        NewOpenTextTaskDto taskDto = new NewOpenTextTaskDto();
        taskDto.setOrder(3);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");

        Course course = mock(Course.class);
        doReturn(course).when(courseTaskDomainService).getCourseIfCanReceiveTask(eq(1L));
        doThrow(new ErrorItemException("order", "msg")).when(courseTaskDomainService).validateTaskOrderAndReorder(eq(course), eq(3));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("order"))
                .andExpect(jsonPath("$.message").isNotEmpty());

        verify(courseTaskDomainService, times(1)).validateTaskOrderAndReorder(any(), eq(3));
    }

    @Test
    void newOpenTextTaskDto__should_return_created_when_new_open_text_task_is_valid() throws Exception {
        NewOpenTextTaskDto taskDto = new NewOpenTextTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated());

        verify(taskRepository, times(1)).save(any(OpenTextTask.class));
    }

    @Test
    void newSingleChoiceTaskDto__should_return_bad_request_when_more_than_one_option_is_correct() throws Exception {
        Set<NewOptionDto> options = new HashSet<>();
        NewChoiceTaskDto taskDto = new NewChoiceTaskDto(options);
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What comes after 1?");

        options.add(new NewOptionDto("Comes number 1", false));
        options.add(new NewOptionDto("Comes number 2", true));
        options.add(new NewOptionDto("Comes number 3", true));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("illegal argument"))
                .andExpect(jsonPath("$.message").value("The task must have only one correct option"));
    }

    @Test
    void newSingleChoiceTaskDto__should_return_bad_request_when_more_than_one_option_has_same_title() throws Exception {
        Set<NewOptionDto> options = new HashSet<>();
        NewChoiceTaskDto taskDto = new NewChoiceTaskDto(options);
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What comes after 1?");

        options.add(new NewOptionDto("Comes number 1", false));
        options.add(new NewOptionDto("Comes number 1", false));
        options.add(new NewOptionDto("Comes number 2", true));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("illegal argument"))
                .andExpect(jsonPath("$.message").value("The options cannot be equal to each other"));
    }

    @Test
    void newSingleChoiceTaskDto__should_return_bad_request_when_more_than_one_option_are_equal_to_statement() throws Exception {
        Set<NewOptionDto> options = new HashSet<>();
        NewChoiceTaskDto taskDto = new NewChoiceTaskDto(options);
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What comes after 1?");

        options.add(new NewOptionDto("Comes number 1", false));
        options.add(new NewOptionDto("What comes after 1?", false));
        options.add(new NewOptionDto("Comes number 2", true));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("illegal argument"))
                .andExpect(jsonPath("$.message").value("The option cannot be the same as the task statement"));
    }

    @Test
    void newSingleChoiceTaskDto__should_return_created_when_single_choice_task_is_valid() throws Exception {
        Set<NewOptionDto> options = new HashSet<>();
        NewChoiceTaskDto taskDto = new NewChoiceTaskDto(options);
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What comes after 1?");

        options.add(new NewOptionDto("Comes number 1", false));
        options.add(new NewOptionDto("Comes number 2", true));
        options.add(new NewOptionDto("Comes number 3", false));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated());

        verify(taskRepository, times(1)).save(any());
    }

    /// /

    @Test
    void newMultipleChoiceTaskDto__should_return_bad_request_when_has_one_option_correct() throws Exception {
        Set<NewOptionDto> options = new HashSet<>();
        NewMultipleChoiceTaskDto taskDto = new NewMultipleChoiceTaskDto(options);
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What comes after 1?");

        options.add(new NewOptionDto("Comes number 1", false));
        options.add(new NewOptionDto("Comes number 2", false));
        options.add(new NewOptionDto("Comes number 3", true));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("illegal argument"))
                .andExpect(jsonPath("$.message").value("The task must have more than one correct option"));
    }

    @Test
    void newMultipleChoiceTaskDto__should_return_bad_request_when_has_no_wrong_options() throws Exception {
        Set<NewOptionDto> options = new HashSet<>();
        NewMultipleChoiceTaskDto taskDto = new NewMultipleChoiceTaskDto(options);
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What comes after 1?");

        options.add(new NewOptionDto("Comes number 2 + 0", true));
        options.add(new NewOptionDto("Comes number 2", true));
        options.add(new NewOptionDto("Comes number 1 + 1", true));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("illegal argument"))
                .andExpect(jsonPath("$.message").value("The task must have at least one wrong option"));
    }

    @Test
    void newMultipleChoiceTaskDto__should_return_bad_request_when_more_than_one_option_has_same_title() throws Exception {
        Set<NewOptionDto> options = new HashSet<>();
        NewMultipleChoiceTaskDto taskDto = new NewMultipleChoiceTaskDto(options);
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What comes after 1?");

        options.add(new NewOptionDto("Comes number 1", false));
        options.add(new NewOptionDto("Comes number 2", true));
        options.add(new NewOptionDto("Comes number 2", true));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("illegal argument"))
                .andExpect(jsonPath("$.message").value("The options cannot be equal to each other"));
    }

    @Test
    void newMultipleChoiceTaskDto__should_return_bad_request_when_more_than_one_option_are_equal_to_statement() throws Exception {
        Set<NewOptionDto> options = new HashSet<>();
        NewMultipleChoiceTaskDto taskDto = new NewMultipleChoiceTaskDto(options);
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What comes after 1?");

        options.add(new NewOptionDto("Comes number 1", false));
        options.add(new NewOptionDto("What comes after 1?", true));
        options.add(new NewOptionDto("Comes number 2", true));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("illegal argument"))
                .andExpect(jsonPath("$.message").value("The option cannot be the same as the task statement"));
    }

    @Test
    void newMultipleChoiceTaskDto__should_return_created_when_single_choice_task_is_valid() throws Exception {
        Set<NewOptionDto> options = new HashSet<>();
        NewMultipleChoiceTaskDto taskDto = new NewMultipleChoiceTaskDto(options);
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What comes after 1?");

        options.add(new NewOptionDto("Comes number 1", false));
        options.add(new NewOptionDto("Comes number 2", true));
        options.add(new NewOptionDto("Comes number 3", true));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated());

        verify(taskRepository, times(1)).save(any());
    }
}

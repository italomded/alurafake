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
    void newOpenTextTaskDto__should_return_bad_request_when_order_is_null() throws Exception {
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("order"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenTextTaskDto__should_return_bad_request_when_order_is_negative() throws Exception {
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
        taskDto.setOrder(-1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("order"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenTextTaskDto__should_return_bad_request_when_statement_is_null() throws Exception {
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenTextTaskDto__should_return_bad_request_when_statement_is_blank() throws Exception {
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("                             ");
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenTextTaskDto__should_return_bad_request_when_statement_has_less_than_4_characters() throws Exception {
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("You");
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenTextTaskDto__should_return_bad_request_when_statement_has_more_than_255_characters() throws Exception {
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("""
                Text with more than 255 characters for the task statement
                should return an error to the user informing that the size
                must be between 4 characters and 255 characters in the base
                registration form for the tasks of the AluraFake test project,
                this test should return an error.
                """);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenTextTaskDto__should_return_bad_request_when_course_id_is_null() throws Exception {
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
        taskDto.setOrder(1);
        taskDto.setStatement("What is the JVM in Java?");
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("courseId"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenTextTaskDto__should_return_bad_request_when_course_id_is_negative() throws Exception {
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(-1L);
        taskDto.setStatement("What is the JVM in Java?");
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("courseId"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newSingleChoiceTaskDto__should_return_bad_request_when_options_is_null() throws Exception {
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newSingleChoiceTaskDto__should_return_bad_request_when_options_option_is_null() throws Exception {
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");
        taskDto.setOptions(Set.of(new NewOptionDto(null, false), new NewOptionDto("Test", false)));
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options[].option"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newSingleChoiceTaskDto__should_return_bad_request_when_options_is_blank() throws Exception {
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");
        taskDto.setOptions(Set.of(new NewOptionDto("     ", false), new NewOptionDto("Test", false)));
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options[].option"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newSingleChoiceTaskDto__should_return_bad_request_when_options_option_is_correct_is_null() throws Exception {
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");
        taskDto.setOptions(Set.of(new NewOptionDto("Test", null), new NewOptionDto("Test", false)));
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options[].isCorrect"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newSingleChoiceTaskDto__should_return_bad_request_when_options_has_less_than_4_characters() throws Exception {
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");
        taskDto.setOptions(Set.of(new NewOptionDto("Tes", false), new NewOptionDto("Test", false)));
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options[].option"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newSingleChoiceTaskDto__should_return_bad_request_when_options_has_more_than_80_characters() throws Exception {
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");
        taskDto.setOptions(Set.of(new NewOptionDto("Option with more than 80 characters should not be allowed to save in the task registry", false), new NewOptionDto("Test", false)));
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options[].option"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newSingleChoiceTaskDto__should_return_bad_request_when_options_is_has_less_than_2_elements() throws Exception {
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");
        taskDto.setOptions(Set.of(new NewOptionDto("Test", false)));
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newSingleChoiceTaskDto__should_return_bad_request_when_options_is_has_more_than_5_elements() throws Exception {
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");
        taskDto.setOptions(Set.of(
                new NewOptionDto("Test", false),
                new NewOptionDto("Test", false),
                new NewOptionDto("Test", false),
                new NewOptionDto("Test", false),
                new NewOptionDto("Test", false),
                new NewOptionDto("Test", false)
        ));
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newMultipleChoiceTaskDto__should_return_bad_request_when_options_is_has_less_than_3_elements() throws Exception {
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");
        taskDto.setOptions(Set.of(new NewOptionDto("Test", false), new NewOptionDto("Test", false)));
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newMultipleChoiceTaskDto__should_return_bad_request_when_options_is_has_more_than_5_elements() throws Exception {
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");
        taskDto.setOptions(Set.of(
                new NewOptionDto("Test", false),
                new NewOptionDto("Test", false),
                new NewOptionDto("Test", false),
                new NewOptionDto("Test", false),
                new NewOptionDto("Test", false),
                new NewOptionDto("Test", false)
        ));
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newMultipleChoiceTaskDto__should_return_bad_request_when_options_is_null() throws Exception {
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto();
        taskDto.setOrder(1);
        taskDto.setCourseId(1L);
        taskDto.setStatement("What is the JVM in Java?");
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenTextTaskDto__should_return_bad_request_when_statement_already_registered() throws Exception {
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
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
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
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
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
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
        NewBaseTaskDto taskDto = new NewBaseTaskDto();
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
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto(options);
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
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto(options);
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
    void newSingleChoiceTaskDto__should_return_bad_request_when_one_option_are_equal_to_statement() throws Exception {
        Set<NewOptionDto> options = new HashSet<>();
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto(options);
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
        NewSingleChoiceTaskDto taskDto = new NewSingleChoiceTaskDto(options);
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
    void newMultipleChoiceTaskDto__should_return_created_when_multiple_choice_task_is_valid() throws Exception {
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

package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.security.JwtService;
import br.com.alura.AluraFake.security.SecurityConfig;
import br.com.alura.AluraFake.security.UserAuthenticated;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
@Import({SecurityConfig.class, JwtService.class})
class CourseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CourseRepository courseRepository;
    @MockBean
    private CourseTaskDomainService courseTaskDomainService;
    @Autowired
    private ObjectMapper objectMapper;

    void setupSecurity(Role role) {
        var details = new UserAuthenticated(new User("teste", "teste", role, "12345"));
        Authentication auth = new UsernamePasswordAuthenticationToken(
                details, details.getPassword(), details.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(authorities = "STUDENT")
    void newCourseDTO__should_return_forbidden_when_is_no_instructor() throws Exception {
        setupSecurity(Role.STUDENT);

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");

        User user = mock(User.class);
        when(user.isInstructor()).thenReturn(false);

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isForbidden());

        clearSecurity();
    }

    @Test
    @WithMockUser(authorities = "INSTRUCTOR")
    void newCourseDTO__should_return_created_when_new_course_request_is_valid() throws Exception {
        setupSecurity(Role.INSTRUCTOR);

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");

        User user = mock(User.class);
        when(user.isInstructor()).thenReturn(true);

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isCreated());

        verify(courseRepository, times(1)).save(any(Course.class));

        clearSecurity();
    }

    @Test
    @WithMockUser
    void listAllCourses__should_list_all_courses() throws Exception {
        User paulo = new User("Paulo", "paulo@alua.com.br", Role.INSTRUCTOR);

        Course java = new Course("Java", "Curso de java", paulo);
        Course hibernate = new Course("Hibernate", "Curso de hibernate", paulo);
        Course spring = new Course("Spring", "Curso de spring", paulo);

        when(courseRepository.findAll()).thenReturn(Arrays.asList(java, hibernate, spring));

        mockMvc.perform(get("/course/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java"))
                .andExpect(jsonPath("$[0].description").value("Curso de java"))
                .andExpect(jsonPath("$[1].title").value("Hibernate"))
                .andExpect(jsonPath("$[1].description").value("Curso de hibernate"))
                .andExpect(jsonPath("$[2].title").value("Spring"))
                .andExpect(jsonPath("$[2].description").value("Curso de spring"));
    }

    @Test
    @WithMockUser(authorities = "INSTRUCTOR")
    void publishCourse__should_return_ok_and_publish_course() throws Exception {
        User user = mock(User.class);
        when(user.isInstructor()).thenReturn(true);
        Course course = new Course("Test course", "Test description", user);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));
        when(courseTaskDomainService.validateTaskOrderForCourse(eq(course))).thenReturn(true);
        when(courseTaskDomainService.validateCourseContainsAllTaskTypes(eq(course))).thenReturn(true);

        mockMvc.perform(post("/course/" + 1 + "/publish")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(courseRepository, times(1)).save(eq(course));
        assertThat(course.getStatus()).isEqualTo(Status.PUBLISHED);
        assertThat(course.getPublishedAt()).isNotNull();
    }

    @Test
    @WithMockUser(authorities = "INSTRUCTOR")
    void publishCourse__should_return_bad_request_when_tasks_are_out_of_order() throws Exception {
        Course course = mock(Course.class);

        when(course.publish(anyBoolean(), anyBoolean())).thenCallRealMethod();
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));
        when(courseTaskDomainService.validateTaskOrderForCourse(eq(course))).thenReturn(false);
        when(courseTaskDomainService.validateCourseContainsAllTaskTypes(eq(course))).thenReturn(true);

        mockMvc.perform(post("/course/" + 1 + "/publish")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("illegal argument"))
                .andExpect(jsonPath("$.message").value("The course can only be published if the tasks are in order"));
    }

    @Test
    @WithMockUser(authorities = "INSTRUCTOR")
    void publishCourse__should_return_bad_request_when_doesnt_contains_tasks_of_all_types() throws Exception {
        Course course = mock(Course.class);

        when(course.publish(anyBoolean(), anyBoolean())).thenCallRealMethod();
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));
        when(courseTaskDomainService.validateTaskOrderForCourse(eq(course))).thenReturn(true);
        when(courseTaskDomainService.validateCourseContainsAllTaskTypes(eq(course))).thenReturn(false);

        mockMvc.perform(post("/course/" + 1 + "/publish")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("illegal argument"))
                .andExpect(jsonPath("$.message").value("Must contain at least one task of each type"));
    }

    @Test
    @WithMockUser(authorities = "INSTRUCTOR")
    void publishCourse__should_return_bad_request_when_course_status_is_not_building() throws Exception {
        Course course = mock(Course.class);

        when(course.publish(anyBoolean(), anyBoolean())).thenCallRealMethod();
        when(course.isOnBuilding()).thenReturn(false);
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));
        when(courseTaskDomainService.validateTaskOrderForCourse(eq(course))).thenReturn(true);
        when(courseTaskDomainService.validateCourseContainsAllTaskTypes(eq(course))).thenReturn(true);

        mockMvc.perform(post("/course/" + 1 + "/publish")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("illegal argument"))
                .andExpect(jsonPath("$.message").value("The course can only be published if the status is BUILDING"));
    }

    @Test
    @WithMockUser(authorities = "INSTRUCTOR")
    void publishCourse__should_return_not_found_when_course_id_is_invalid() throws Exception {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(post("/course/" + 1 + "/publish")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
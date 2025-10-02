package br.com.alura.AluraFake.user;

import br.com.alura.AluraFake.course.CourseReportImplDTO;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.security.JwtService;
import br.com.alura.AluraFake.security.SecurityConfig;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtService.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void newUser__should_return_bad_request_when_email_is_blank() throws Exception {
        NewUserDTO newUserDTO = new NewUserDTO();
        newUserDTO.setEmail("");
        newUserDTO.setName("Caio Bugorin");
        newUserDTO.setRole(Role.STUDENT);

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("email"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser
    void newUser__should_return_bad_request_when_email_is_invalid() throws Exception {
        NewUserDTO newUserDTO = new NewUserDTO();
        newUserDTO.setEmail("caio");
        newUserDTO.setName("Caio Bugorin");
        newUserDTO.setRole(Role.STUDENT);

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("email"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser
    void newUser__should_return_bad_request_when_email_already_exists() throws Exception {
        NewUserDTO newUserDTO = new NewUserDTO();
        newUserDTO.setEmail("caio.bugorin@alura.com.br");
        newUserDTO.setName("Caio Bugorin");
        newUserDTO.setRole(Role.STUDENT);

        when(userRepository.existsByEmail(newUserDTO.getEmail())).thenReturn(true);

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("email"))
                .andExpect(jsonPath("$.message").value("Email já cadastrado no sistema"));
    }

    @Test
    @WithMockUser
    void newUser__should_return_created_when_user_request_is_valid() throws Exception {
        NewUserDTO newUserDTO = new NewUserDTO();
        newUserDTO.setEmail("caio.bugorin@alura.com.br");
        newUserDTO.setName("Caio Bugorin");
        newUserDTO.setRole(Role.STUDENT);

        when(userRepository.existsByEmail(newUserDTO.getEmail())).thenReturn(false);

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void listAllUsers__should_list_all_users() throws Exception {
        User user1 = new User("User 1", "user1@test.com", Role.STUDENT);
        User user2 = new User("User 2", "user2@test.com", Role.STUDENT);
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/user/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[1].name").value("User 2"));
    }

    @Test
    @WithMockUser(authorities = "INSTRUCTOR")
    void listAllInstructorCourses__should_list_all_instructor_courses() throws Exception {
        CourseReportImplDTO java = new CourseReportImplDTO(1L, "Java", Status.PUBLISHED, LocalDateTime.now(), 1L);

        User user = mock(User.class);
        when(courseRepository.retrieveReportByInstructorId(anyLong())).thenReturn(Set.of(java));
        when(courseRepository.countByInstructorId(anyLong())).thenReturn(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(user.isInstructor()).thenReturn(true);

        mockMvc.perform(get("/instructor/" + 1 + "/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.report[0].id").value("1"))
                .andExpect(jsonPath("$.report[0].title").value("Java"))
                .andExpect(jsonPath("$.report[0].status").value("PUBLISHED"))
                .andExpect(jsonPath("$.report[0].publishedAt").isNotEmpty())
                .andExpect(jsonPath("$.report[0].taskQuantity").value("1"))
                .andExpect(jsonPath("$.totalCourses").value("1"));
    }

    @Test
    @WithMockUser(authorities = "INSTRUCTOR")
    void listAllInstructorCourses__should_return_empty_list_when_has_no_courses() throws Exception {
        User user = mock(User.class);
        when(courseRepository.retrieveReportByInstructorId(anyLong())).thenReturn(Collections.emptySet());
        when(courseRepository.countByInstructorId(anyLong())).thenReturn(0L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(user.isInstructor()).thenReturn(true);
        mockMvc.perform(get("/instructor/" + 1 + "/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.report").isEmpty())
                .andExpect(jsonPath("$.totalCourses").value("0"));
    }

    @Test
    @WithMockUser(authorities = "INSTRUCTOR")
    void listAllInstructorCourses__should_return_not_found_if_user_not_reached() throws Exception {
        doReturn(Optional.empty()).when(userRepository).findById(anyLong());
        mockMvc.perform(get("/instructor/" + 1 + "/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "INSTRUCTOR")
    void listAllInstructorCourses__should_return_bad_request_user_not_instructor() throws Exception {
        User user = mock(User.class);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(user.isInstructor()).thenReturn(false);

        mockMvc.perform(get("/instructor/" + 1 + "/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
package br.com.alura.AluraFake.user;

import br.com.alura.AluraFake.course.CourseReportDTO;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public UserController(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    @PostMapping("/user/new")
    public ResponseEntity newStudent(@RequestBody @Valid NewUserDTO newUser) {
        if(userRepository.existsByEmail(newUser.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("email", "Email já cadastrado no sistema"));
        }
        User user = newUser.toModel();
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/user/all")
    public List<UserListItemDTO> listAllUsers() {
        return userRepository.findAll().stream().map(UserListItemDTO::new).toList();
    }

    @GetMapping("/instructor/{id}/courses")
    public ResponseEntity listAllInstructorCourses(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        if(!user.isInstructor()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("id", "User is not a instructor"));
        }

        // Pageable era uma opção, mas optei por seguir estritamente o requisito.
        Set<CourseReportDTO> courseReportDTO = this.courseRepository.retrieveReportByInstructorId(user.getId());
        long count = this.courseRepository.countByInstructorId(user.getId());
        return ResponseEntity.ok(
                Map.of("totalCourses", count, "report", courseReportDTO)
        );
    }

}

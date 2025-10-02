package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void retrieveReportByInstructorId__should_return_only_right_instructor_courses() {
        User caio = em.persist(new User("Caio", "caio@alura.com.br", Role.INSTRUCTOR));
        User robert = em.persist(new User("Robert", "robert@alura.com.br", Role.INSTRUCTOR));
        Long oneIdCaioCourse = em.persist(new Course("Test course 1", "Course description", caio)).getId();
        Long twoIdCaioCourse = em.persist(new Course("Test course 2", "Course description", caio)).getId();
        Long threeIdCaioCourse = em.persist(new Course("Test course 3", "Course description", caio)).getId();
        Long fourtIdCaioCourse = em.persist(new Course("Test course 4", "Course description", caio)).getId();
        Long idRobertCourse = em.persist(new Course("Test course 5", "Course description", robert)).getId();
        Set<CourseReportDTO> courseReportDTO = this.courseRepository.retrieveReportByInstructorId(caio.getId());
        assertThat(courseReportDTO.size()).isEqualTo(4);
        List<Long> idsReturned = courseReportDTO.stream().map(CourseReportDTO::getId).toList();
        assertThat(idsReturned.containsAll(Set.of(oneIdCaioCourse, twoIdCaioCourse, threeIdCaioCourse, fourtIdCaioCourse))).isTrue();
        assertThat(idsReturned.contains(idRobertCourse)).isFalse();
    }

    @Test
    void countByInstructorId__should_return_only_return_the_number_of_courses_created_by_instructor() {
        User caio = em.persist(new User("Caio", "caio@alura.com.br", Role.INSTRUCTOR));
        User robert = em.persist(new User("Robert", "robert@alura.com.br", Role.INSTRUCTOR));
        em.persist(new Course("Test course 1", "Course description", caio));
        em.persist(new Course("Test course 2", "Course description", caio));
        em.persist(new Course("Test course 3", "Course description", caio));
        em.persist(new Course("Test course 4", "Course description", caio));
        em.persist(new Course("Test course 5", "Course description", robert));
        Long quantity = this.courseRepository.countByInstructorId(caio.getId());
        assertThat(quantity).isEqualTo(4);
    }
}
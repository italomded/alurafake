package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.util.ErrorItemException;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class CourseTaskDomainService {
    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    public CourseTaskDomainService(TaskRepository taskRepository, CourseRepository courseRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
    }

    public void validateTaskOrderAndReorder(Course course, Integer order) {
        Optional<Task> optionalLastTask = this.taskRepository.findTopByCourseIdOrderByTaskOrder_Desc(course.getId());

        final int lastOrder;
        if (optionalLastTask.isEmpty()) {
            lastOrder = 0;
        } else {
            lastOrder = optionalLastTask.get().getTaskOrder();
        }

        if (order > lastOrder + 1) {
            throw new ErrorItemException(
                    "order", "The order given is not a direct continuation"
            );
        }

        if (lastOrder >= order) {
            this.taskRepository.updateIncrementTaskOrderByCourseIdAndCursor(course.getId(), order);
        }
    }

    public void validateUniqueStatementForCourse(Course course, String statement) {
        if (taskRepository.existsByCourseIdAndStatement(course.getId(), statement)) {
            throw new ErrorItemException(
                    "statement", "The task statement already exists in the course"
            );
        }
    }

    public Course getCourseIfCanReceiveTask(Long courseId) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isEmpty()) {
            throw new ErrorItemException(
                    "courseId", "Course not found"
            );
        }

        Course course = optionalCourse.get();
        if (!course.isOnBuilding()) {
            throw new ErrorItemException(
                    "courseId", "The course is not in BUILDING status"
            );
        }

        return course;
    }
}

package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import br.com.alura.AluraFake.util.ErrorItemException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public boolean validateTaskOrderForCourse(Course course) {
        List<Long[]> resultList = taskRepository.validateTaskSequenceByCourse(course.getId());
        if (resultList.isEmpty()) {
            return false;
        }

        Long[] countMinAndMaxOrders = resultList.getFirst();
        Long tasksQtd = countMinAndMaxOrders[0];
        Long minTaskOrder = countMinAndMaxOrders[1];
        Long maxTaskOrder = countMinAndMaxOrders[2];

        if (minTaskOrder != 1) {
            return false;
        }

        long sumValidation = maxTaskOrder - minTaskOrder + 1;
        return sumValidation == tasksQtd;
    }

    public boolean validateCourseContainsAllTaskTypes(Course course) {
        Set<Type> taskTypes = taskRepository.findDistinctTaskTypesForCourse(course.getId());
        return taskTypes.containsAll(Arrays.stream(Type.values()).toList());
    }
}

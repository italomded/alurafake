package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.CourseTaskDomainService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
public class TaskController {
    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    private final CourseTaskDomainService courseTaskDomainService;

    @Autowired
    public TaskController(TaskRepository taskRepository, CourseRepository courseRepository, CourseTaskDomainService courseTaskDomainService) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
        this.courseTaskDomainService = courseTaskDomainService;
    }

    @Transactional
    @PostMapping("/task/new/opentext")
    public ResponseEntity newOpenTextExercise(@RequestBody @Valid NewOpenTextTaskDto newOpenTextTask) {
        Course course = courseTaskDomainService.getCourseIfCanReceiveTask(newOpenTextTask.getCourseId());
        courseTaskDomainService.validateUniqueStatementForCourse(course, newOpenTextTask.getStatement());
        courseTaskDomainService.validateTaskOrderAndReorder(course, newOpenTextTask.getOrder());

        OpenTextTask task = new OpenTextTask(newOpenTextTask.getStatement(), course, newOpenTextTask.getOrder());

        taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice() {
        return ResponseEntity.ok().build();
    }

}
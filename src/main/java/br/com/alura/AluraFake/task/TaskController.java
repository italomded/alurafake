package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseTaskDomainService;

import java.util.stream.Collectors;

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

    private final CourseTaskDomainService courseTaskDomainService;

    @Autowired
    public TaskController(TaskRepository taskRepository, CourseTaskDomainService courseTaskDomainService) {
        this.taskRepository = taskRepository;
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

    @Transactional
    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice(@RequestBody @Valid NewChoiceTaskDto newChoiceTaskDto) {
        Course course = courseTaskDomainService.getCourseIfCanReceiveTask(newChoiceTaskDto.getCourseId());
        courseTaskDomainService.validateUniqueStatementForCourse(course, newChoiceTaskDto.getStatement());
        courseTaskDomainService.validateTaskOrderAndReorder(course, newChoiceTaskDto.getOrder());

        SingleChoiceTask task = new SingleChoiceTask(
                newChoiceTaskDto.getStatement(),
                course,
                newChoiceTaskDto.getOrder(),
                newChoiceTaskDto.options.stream().map(NewOptionDto::toModel).collect(Collectors.toSet())
        );

        this.taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice() {
        return ResponseEntity.ok().build();
    }

}
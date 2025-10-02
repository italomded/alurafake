package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseTaskDomainService;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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

    @Secured("INSTRUCTOR")
    @Transactional
    @PostMapping("/task/new/opentext")
    public ResponseEntity newOpenTextExercise(@RequestBody @Valid NewBaseTaskDTO newOpenTextTask) {
        Course course = courseTaskDomainService.getCourseIfCanReceiveTask(newOpenTextTask.getCourseId());
        courseTaskDomainService.validateUniqueStatementForCourse(course, newOpenTextTask.getStatement());
        courseTaskDomainService.validateTaskOrderAndReorder(course, newOpenTextTask.getOrder());

        OpenTextTask task = new OpenTextTask(newOpenTextTask.getStatement(), course, newOpenTextTask.getOrder());

        taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Secured("INSTRUCTOR")
    @Transactional
    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice(@RequestBody @Valid NewSingleChoiceTaskDTO newSingleChoiceTaskDTO) {
        Course course = courseTaskDomainService.getCourseIfCanReceiveTask(newSingleChoiceTaskDTO.getCourseId());
        courseTaskDomainService.validateUniqueStatementForCourse(course, newSingleChoiceTaskDTO.getStatement());
        courseTaskDomainService.validateTaskOrderAndReorder(course, newSingleChoiceTaskDTO.getOrder());

        SingleChoiceTask task = new SingleChoiceTask(
                newSingleChoiceTaskDTO.getStatement(),
                course,
                newSingleChoiceTaskDTO.getOrder(),
                newSingleChoiceTaskDTO.getOptions().stream().map(NewOptionDTO::toModel).collect(Collectors.toSet())
        );

        this.taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Secured("INSTRUCTOR")
    @Transactional
    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice(@RequestBody @Valid NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO) {
        Course course = courseTaskDomainService.getCourseIfCanReceiveTask(newMultipleChoiceTaskDTO.getCourseId());
        courseTaskDomainService.validateUniqueStatementForCourse(course, newMultipleChoiceTaskDTO.getStatement());
        courseTaskDomainService.validateTaskOrderAndReorder(course, newMultipleChoiceTaskDTO.getOrder());

        MultipleChoiceTask task = new MultipleChoiceTask(
                newMultipleChoiceTaskDTO.getStatement(),
                course,
                newMultipleChoiceTaskDTO.getOrder(),
                newMultipleChoiceTaskDTO.getOptions().stream().map(NewOptionDTO::toModel).collect(Collectors.toSet())
        );

        this.taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
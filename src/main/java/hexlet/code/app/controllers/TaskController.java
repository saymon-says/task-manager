package hexlet.code.app.controllers;

import com.querydsl.core.types.Predicate;
import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.service.TaskServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static hexlet.code.app.controllers.UserController.ID_PATH;

@RestController
@AllArgsConstructor
@RequestMapping("/${base-url}" + "/tasks")
public class TaskController {

    private TaskRepository taskRepository;
    private TaskServiceImpl taskService;

    private static final String SEARCH = "/by";

    private static final String ONLY_TASK_OWNER_BY_ID = """
            @taskRepository.findById(#id).get().getCreatedBy() == authentication.getName()
        """;

    @GetMapping(ID_PATH)
    public Task getTask(@PathVariable long id) {
        return taskRepository.findById(id).get();
    }

    @GetMapping
    public Iterable<Task> getTasks() {
        return taskRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTask(@RequestBody @Valid TaskDto taskDto) {
        taskService.create(taskDto);
    }

    @PutMapping(ID_PATH)
    public void updateTask(@PathVariable long id, @RequestBody @Valid TaskDto taskDto) {
        taskService.update(id, taskDto);
    }

    @DeleteMapping(ID_PATH)
    @PreAuthorize(ONLY_TASK_OWNER_BY_ID)
    public void deleteTask(@PathVariable long id) {
        taskRepository.deleteById(id);
    }

    @GetMapping(SEARCH)
    public Iterable<Task> getFilteredTask(@QuerydslPredicate(root = Task.class) Predicate predicate) {
        return taskRepository.findAll(predicate);
    }
}
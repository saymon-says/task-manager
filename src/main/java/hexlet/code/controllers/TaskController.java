package hexlet.code.controllers;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import static hexlet.code.controllers.TaskController.TASKS_PATH;
import static hexlet.code.controllers.UserController.ID_PATH;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + TASKS_PATH)
public class TaskController {

    private TaskRepository taskRepository;
    private TaskServiceImpl taskService;

    public static final String TASKS_PATH = "/tasks";

    private static final String ONLY_TASK_OWNER_BY_ID = """
                @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
            """;

    @Operation(summary = "Get task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task with that id not found")
    })
    @GetMapping(ID_PATH)
    public Task getTask(@Parameter(description = "Id of task to be found")
                        @PathVariable final long id) {
        return this.taskRepository.findById(id).get();
    }

    @Operation(summary = "Create new task")
    @ApiResponse(responseCode = "201", description = "Task created")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody @Valid TaskDto taskDto) {
       return this.taskService.create(taskDto);
    }

    @Operation(summary = "Update task by his id")
    @ApiResponse(responseCode = "200", description = "Task updated")
    @PutMapping(ID_PATH)
    public Task updateTask(@Parameter(description = "Id of task to be updated")
                           @PathVariable final long id, @RequestBody @Valid TaskDto taskDto) {
        return this.taskService.update(id, taskDto);
    }

    @Operation(summary = "Delete task by his id")
    @ApiResponse(responseCode = "200", description = "Task deleted")
    @DeleteMapping(ID_PATH)
    @PreAuthorize(ONLY_TASK_OWNER_BY_ID)
    public void deleteTask(@Parameter(description = "Id of task to be deleted")
                           @PathVariable final long id) {
        this.taskRepository.deleteById(id);
    }

    @Operation(summary = "Get list of all task or filtered")
    @ApiResponse(responseCode = "200", description = "List of all tasks or filtered")
    @GetMapping
    public Iterable<Task> getFilteredTask(@QuerydslPredicate(root = Task.class) Predicate predicate) {
        return this.taskRepository.findAll(predicate);
    }
}

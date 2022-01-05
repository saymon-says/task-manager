package hexlet.code.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusCreatedDto {

    @NotEmpty(message = "Task_status_dto name field must not be empty")
    @Size(min = 1, message = "name longer than 1 character")
    private String name;

}

package ru.practicum.explorewme.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long id;

    @NotBlank
    private String name;

    @Email(message = "Некорректный формат email")
    @NotBlank
    private String email;
}

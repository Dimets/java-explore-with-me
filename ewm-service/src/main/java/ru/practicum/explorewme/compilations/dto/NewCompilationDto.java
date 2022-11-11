package ru.practicum.explorewme.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private Set<Long> events;

    private Boolean pinned;

    @NotBlank
    private String title;
}

package ru.practicum.explorewme.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequest {
    @Size(min = 20, max = 2000)
    @NotBlank
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    @NotBlank
    private String description;

    @NotBlank
    private String eventDate;

    @NotNull
    private Long eventId;

    private Boolean paid;

    private int participantLimit;

    @Size(min = 3, max = 120)
    @NotBlank
    private String title;
}

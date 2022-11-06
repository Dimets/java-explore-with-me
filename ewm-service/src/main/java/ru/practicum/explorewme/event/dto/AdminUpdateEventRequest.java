package ru.practicum.explorewme.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewme.location.dto.LocationShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateEventRequest {
    @Size(min = 20, max = 2000)
    @NotBlank
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    @NotBlank
    private String description;

    @NotBlank
    private String eventDate;

    private LocationShortDto location;

    private Boolean paid;

    @PositiveOrZero
    private int participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 120)
    @NotBlank
    private String title;
}

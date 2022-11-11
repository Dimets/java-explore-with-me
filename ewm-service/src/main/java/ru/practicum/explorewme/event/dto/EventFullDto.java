package ru.practicum.explorewme.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewme.category.dto.CategoryDto;
import ru.practicum.explorewme.location.dto.LocationShortDto;
import ru.practicum.explorewme.user.dto.UserShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private String annotation;

    private CategoryDto category;

    private long confirmedRequests;

    private String createdOn;

    private String description;

    private String eventDate;

    private Long id;

    private UserShortDto initiator;

    private LocationShortDto location;

    private Boolean paid;

    private int participantLimit;

    private String publishedOn;

    private Boolean requestModeration;

    private String state;

    private String title;

    private Long views;

}

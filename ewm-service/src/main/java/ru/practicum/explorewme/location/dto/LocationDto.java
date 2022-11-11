package ru.practicum.explorewme.location.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationDto {
    private Long id;

    @NotBlank
    private Double lat;

    @NotBlank
    private Double lon;
}

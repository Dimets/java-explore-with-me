package ru.practicum.explorewme.location.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationDto {
    private Long id;

    @NotNull
    @NotBlank
    private Double lat;

    @NotNull
    @NotBlank
    private Double lon;
}

package ru.practicum.explorewme.stat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewStatDto {
    String app;

    String uri;

    Long count;
}

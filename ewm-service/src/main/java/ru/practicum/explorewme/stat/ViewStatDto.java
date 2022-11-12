package ru.practicum.explorewme.stat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewStatDto {
    private final String app;

    private final String uri;

    private final Long hits;
}

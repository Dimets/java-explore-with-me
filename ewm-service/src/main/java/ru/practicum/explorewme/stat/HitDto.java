package ru.practicum.explorewme.stat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HitDto {
    private String app;

    private String uri;

    private String ip;

    private String timestamp;
}


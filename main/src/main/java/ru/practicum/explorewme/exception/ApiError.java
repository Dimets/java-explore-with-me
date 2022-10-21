package ru.practicum.explorewme.exception;

import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.List;

@Data
@AllArgsConstructor
public class ApiError {
    private final List<Error> errors;

    private final String message;

    private final String reason;

    private final String status;

    private final String timestamp;
}

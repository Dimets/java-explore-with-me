package ru.practicum.explorewme.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Error {
    private  final String fieldName;

    private  final String message;
}

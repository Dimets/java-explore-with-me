package ru.practicum.explorewme.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewme.user.dto.UserShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto {
    private UserShortDto subscriber;

    private UserShortDto user;

    private String status;
}

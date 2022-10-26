package ru.practicum.explorewme.user;

import org.springframework.stereotype.Component;
import ru.practicum.explorewme.user.dto.UserDto;
import ru.practicum.explorewme.user.dto.UserShortDto;
import ru.practicum.explorewme.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public List<UserDto> toUserDto(List<User> users) {
        return users.stream()
                .map(x -> toUserDto(x))
                .collect(Collectors.toList());
    }

    public User toUser(UserDto userDto) {
        User user = new User();
        if (userDto.getId() != null) {
            user.setId(userDto.getId());
        }
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }


}

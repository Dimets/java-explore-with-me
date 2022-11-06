package ru.practicum.explorewme.user;

import ru.practicum.explorewme.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create (UserDto userDto);

    List<UserDto> findAll(Long[] ids, int from, int size);

    UserDto findById(Long id);

    void deleteById(Long id);
}

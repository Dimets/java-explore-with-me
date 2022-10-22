package ru.practicum.explorewme.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewme.user.dto.UserDto;

import javax.persistence.EntityNotFoundException;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Override
    public List<UserDto> findAll(Long[] ids, int from, int size) {
        Sort newFirst = Sort.by(Sort.Direction.DESC, "id");

        Pageable pageable = PageRequest.of(from / size, size, newFirst);

        if (ids.length > 0) {
            return userMapper.toUserDto(userRepository.findAllByIdIn(ids, pageable).stream().collect(Collectors.toList()));
        } else {
            return userMapper.toUserDto(userRepository.findAll(pageable).stream().collect(Collectors.toList()));
        }
    }

    @Override
    public UserDto findById(Long id) {
        return userMapper.toUserDto(userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id=%d not found", id))));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}

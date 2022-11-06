package ru.practicum.explorewme.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@Validated
@RequiredArgsConstructor
@Slf4j
public class UserAdminController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("POST /admin/users");
        log.debug("POST /admin/users userDto={}", userDto);
        return userService.create(userDto);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam (name = "ids", defaultValue = "") Long[] ids,
                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET /admin/users ids={} from={}, size={}", ids, from, size);
        return userService.findAll(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.info("DELETE /users/{}", userId);
        userService.deleteById(userId);
    }

}

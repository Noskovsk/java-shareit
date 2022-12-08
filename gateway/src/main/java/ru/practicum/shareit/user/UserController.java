package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoPatch;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PositiveOrZero @PathVariable long userId) {
        log.info("gateway: Ищем пользователя с id {}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> listUsers() {
        return userClient.listUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("gateway: Создаем пользователя {}, email={}", userDto.getName(), userDto.getEmail());
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PositiveOrZero @PathVariable Long userId, @Valid @RequestBody UserDtoPatch userDtoPatch) {
        log.info("gateway: Обновляем пользователя с id {}, данные на обновление: ", userId, userDtoPatch);
        return userClient.updateUser(userId, userDtoPatch);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PositiveOrZero @PathVariable Long userId) {
        log.info("gateway: Удаляем пользователя с id {}", userId);
        return userClient.deleteUser(userId);
    }
}

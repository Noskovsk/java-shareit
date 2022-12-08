package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getUserById(long userId) {
        log.info("Получен запрос на поиск пользователя с id: {}", userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.error("Ошибка при поиске пользователя с userId: {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при поиске пользователя!");
        } else {
            return userOptional.get();
        }
    }

    @Override
    @Transactional
    public User createUser(User user) {
        log.info("Получен запрос на создание пользователя: name = {}, email = {}", user.getName(), user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User updateUser(long userId, User patchUser) {
        log.info("Получен запрос на изменение данных пользователя с id {}", userId);
        if (patchUser.getName() != null && patchUser.getName().isBlank()) {
            log.error("Ошибка при изменении данных пользователя. Имя  не может быть пустым.");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при изменении данных пользователя. Имя  не может быть пустым.");
        }
        patchUser = UserMapper.patchUser(patchUser, getUserById(userId));
        return userRepository.save(patchUser);
    }

    @Override
    public List<User> listUsers() {
        log.info("Получен запрос на получение списка пользователей");
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Получен запрос на удаление пользователя с id {}", userId);
        userRepository.deleteById(userId);
    }
}

package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = {"db.name=testUser"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceImplTest {
    private final EntityManager entityManager;
    private final UserService userService;
    private List<Long> userIdList;

    protected List<Long> createTestUserIntoDb(Integer count) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            userList.add(new User());
            userList.get(i).setName("user" + (i + 1));
            userList.get(i).setEmail("email" + (i + 1) + "@email.com");
            entityManager.persist(userList.get(i));
        }
        entityManager.flush();
        return userList.stream().map(user -> user.getId()).collect(Collectors.toList());
    }

    @Test
    void shouldGetUserById() {
        userIdList = createTestUserIntoDb(1);
        User user1 = userService.getUserById(userIdList.get(0));
        assertEquals(userIdList.get(0), user1.getId(), "id не совпадает");
        assertEquals("user1", user1.getName(), "поле name не совпадает");
    }

    @Test
    void shouldNotGetUserById() {
        Throwable throwable = assertThrows(ResponseStatusException.class,
                () -> userService.getUserById(99L));
        assertTrue(throwable.getMessage().contains("404"));
    }

    @Test
    void sholdCreateUser() {
        User user = new User();
        user.setName("user1");
        user.setEmail("email1@email.com");

        userService.createUser(user);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.email = :email", User.class);
        User findUser = query
                .setParameter("email", "email1@email.com")
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.hashCode(), equalTo(findUser.hashCode()));
        assertThat(user.getEmail(), equalTo("email1@email.com"));
        assertThat(user.getName(), equalTo("user1"));
    }

    @Test
    void shouldUpdateUser() {
        userIdList = createTestUserIntoDb(1);

        User patchUser = new User();
        patchUser.setName("updatedName");
        User userToBePatched = userService.updateUser(userIdList.get(0), patchUser);

        assertThat(userToBePatched.getId(), notNullValue());
        assertThat(userToBePatched.getEmail(), notNullValue());
        assertThat(userToBePatched.getName(), equalTo("updatedName"));
    }

    @Test
    void shouldReturnListOfUser() {
        userIdList = createTestUserIntoDb(2);
        List<User> userList = userService.listUsers();
        assertEquals(2, userList.size(), "Количество пользователей не совпадает.");
        assertEquals(userIdList.get(0), userList.get(0).getId(), "id не совпадает.");
        assertEquals(userIdList.get(1), userList.get(1).getId(), "id не совпадает.");
    }

    @Test
    void shouldDeleteUser() {
        userIdList = createTestUserIntoDb(1);
        userService.deleteUser(userIdList.get(0));
        List<User> userList = userService.listUsers();
        assertEquals(0, userList.size(), "Количество пользователей не совпадает.");
    }
}

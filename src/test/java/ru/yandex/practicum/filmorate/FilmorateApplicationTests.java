package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;

    @Test
    public void testFindUserById() {
        // Создаем пользователя с уникальными данными
        User user = User.builder()
                .email("find@test.com")
                .login("findlogin")
                .name("Find User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User savedUser = userStorage.add(user);

        // Ищем пользователя по id
        Optional<User> userOptional = userStorage.getById(savedUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userFound ->
                        assertThat(userFound).hasFieldOrPropertyWithValue("id", savedUser.getId())
                );
    }

    @Test
    public void testCreateUser() {
        User user = User.builder()
                .email("new@test.com")
                .login("newlogin")
                .name("New User")
                .birthday(LocalDate.of(1995, 5, 15))
                .build();

        User savedUser = userStorage.add(user);

        assertThat(savedUser.getId()).isPositive();
        assertThat(savedUser.getEmail()).isEqualTo("new@test.com");
        assertThat(savedUser.getLogin()).isEqualTo("newlogin");
    }

    @Test
    public void testUpdateUser() {
        // Получаем существующего пользователя из data.sql
        Optional<User> existingUser = userStorage.getById(1L);
        assertThat(existingUser).isPresent();

        User userToUpdate = existingUser.get();

        // Обновляем пользователя
        userToUpdate.setName("Updated Name");
        userToUpdate.setEmail("updateduser@test.com");

        User updatedUser = userStorage.update(userToUpdate);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updateduser@test.com");
    }

    @Test
    public void testGetAllUsers() {
        // Получаем всех пользователей (включая тех, что уже есть в data.sql)
        List<User> allUsers = userStorage.getAll();

        // Проверяем, что список не пустой (должен содержать хотя бы пользователя из data.sql)
        assertThat(allUsers.size()).isGreaterThan(0);

        // Проверяем, что есть пользователь с ID 1 из data.sql
        boolean hasTestUser = allUsers.stream()
                .anyMatch(user -> user.getId() == 1L && "test@test.com".equals(user.getEmail()));
        assertThat(hasTestUser).isTrue();
    }

    @Test
    public void testAddAndRemoveFriend() {
        // Используем существующего пользователя из data.sql
        Optional<User> existingUser = userStorage.getById(1L);
        assertThat(existingUser).isPresent();

        // Создаем только одного нового пользователя для тестирования дружбы
        User newUser = User.builder()
                .email("friend2@test.com")
                .login("friend2")
                .name("Friend 2")
                .birthday(LocalDate.of(1995, 5, 15))
                .build();

        User savedNewUser = userStorage.add(newUser);

        // Добавляем дружбу между существующим и новым пользователем
        userStorage.addFriend(existingUser.get().getId(), savedNewUser.getId());

        List<Long> friends = userStorage.getFriends(existingUser.get().getId());
        assertThat(friends).asList().contains(savedNewUser.getId());

        userStorage.removeFriend(existingUser.get().getId(), savedNewUser.getId());

        friends = userStorage.getFriends(existingUser.get().getId());
        assertThat(friends).asList().doesNotContain(savedNewUser.getId());
    }
}
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
        // Создаем пользователя
        User user = User.builder()
                .email("test@test.com")
                .login("testlogin")
                .name("Test User")
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
        // Создаем пользователя
        User user = User.builder()
                .email("update@test.com")
                .login("updatelogin")
                .name("Update User")
                .birthday(LocalDate.of(1985, 3, 10))
                .build();

        User savedUser = userStorage.add(user);
        
        // Обновляем пользователя
        savedUser.setName("Updated Name");
        savedUser.setEmail("updated@test.com");

        User updatedUser = userStorage.update(savedUser);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@test.com");
    }

    @Test
    public void testGetAllUsers() {
        // Создаем несколько пользователей
        User user1 = User.builder()
                .email("user1@test.com")
                .login("user1")
                .name("User 1")
                                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User user2 = User.builder()
                .email("user2@test.com")
                .login("user2")
                .name("User 2")
                .birthday(LocalDate.of(1995, 5, 15))
                .build();
        
        userStorage.add(user1);
        userStorage.add(user2);

        List<User> allUsers = userStorage.getAll();

        assertThat(allUsers.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void testAddAndRemoveFriend() {
        // Создаем двух пользователей
        User user1 = User.builder()
                .email("friend1@test.com")
                .login("friend1")
                .name("Friend 1")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        
        User user2 = User.builder()
                .email("friend2@test.com")
                .login("friend2")
                .name("Friend 2")
                .birthday(LocalDate.of(1995, 5, 15))
                .build();
        
        User savedUser1 = userStorage.add(user1);
        User savedUser2 = userStorage.add(user2);

        // Добавляем друга
        userStorage.addFriend(savedUser1.getId(), savedUser2.getId());
        
        List<Long> friends = userStorage.getFriends(savedUser1.getId());
        assertThat(friends).asList().contains(savedUser2.getId());

        // Удаляем друга
        userStorage.removeFriend(savedUser1.getId(), savedUser2.getId());
        
        friends = userStorage.getFriends(savedUser1.getId());
        assertThat(friends).asList().doesNotContain(savedUser2.getId());
    }
}
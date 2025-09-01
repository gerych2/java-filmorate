package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final UserDbStorage userDbStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, UserDbStorage userDbStorage) {
        this.userStorage = userStorage;
        this.userDbStorage = userDbStorage;
    }

    public User createUser(User user) {
        user.fillNameIfEmpty();
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        user.fillNameIfEmpty();
        return userStorage.update(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User getUserById(Long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с id " + id + " не найден."));
    }

    public void addFriend(Long userId, Long friendId) {
        getUserById(userId); // Проверяем существование пользователя
        getUserById(friendId); // Проверяем существование друга
        userDbStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        getUserById(userId); // Проверяем существование пользователя
        getUserById(friendId); // Проверяем существование друга
        userDbStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        getUserById(userId); // Проверяем существование пользователя
        List<Long> friendIds = userDbStorage.getFriends(userId);
        return friendIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        getUserById(userId); // Проверяем существование пользователя
        getUserById(otherId); // Проверяем существование другого пользователя
        
        List<Long> userFriends = userDbStorage.getFriends(userId);
        List<Long> otherFriends = userDbStorage.getFriends(otherId);

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
    }
}
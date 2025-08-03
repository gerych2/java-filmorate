package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(int userId) {
        User user = getUserById(userId);
        return user.getFriends().stream()
                .map(id -> userStorage.getById(id)
                        .orElseThrow(() -> new UserNotFoundException("Пользователь не найден")))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        Set<Integer> friends1 = getUserById(userId).getFriends();
        Set<Integer> friends2 = getUserById(otherId).getFriends();
        return friends1.stream()
                .filter(friends2::contains)
                .map(id -> userStorage.getById(id)
                        .orElseThrow(() -> new UserNotFoundException("Пользователь не найден")))
                .collect(Collectors.toList());
    }

    private User getUserById(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }
}

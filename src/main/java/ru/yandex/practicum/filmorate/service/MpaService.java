package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MpaService {

    private final MpaStorage mpaStorage;
    private final MpaDbStorage mpaDbStorage;

    public MpaService(@Qualifier("mpaDbStorage") MpaStorage mpaStorage, MpaDbStorage mpaDbStorage) {
        this.mpaStorage = mpaStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAll();
    }

    public Mpa getMpaById(Long id) {
        return mpaStorage.getById(id)
                .orElseThrow(() -> new NoSuchElementException("Рейтинг MPA с id " + id + " не найден."));
    }
}

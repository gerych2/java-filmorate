package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class})
class MpaDbStorageTest {
    private final MpaDbStorage mpaStorage;

    @Test
    public void testGetAllMpa() {
        List<Mpa> mpaList = mpaStorage.getAll();

        assertThat(mpaList).isNotNull();
        assertThat(mpaList.size()).isGreaterThan(0);

        // Проверяем, что рейтинги отсортированы по id
        for (int i = 1; i < mpaList.size(); i++) {
            assertThat(mpaList.get(i).getId()).isGreaterThan(mpaList.get(i - 1).getId());
        }
    }

    @Test
    public void testGetMpaById() {
        // Получаем первый рейтинг из списка
        List<Mpa> allMpa = mpaStorage.getAll();
        assertThat(allMpa.size()).isGreaterThan(0);

        Mpa firstMpa = allMpa.get(0);
        Optional<Mpa> foundMpa = mpaStorage.getById(firstMpa.getId());

        assertThat(foundMpa)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa.getId()).isEqualTo(firstMpa.getId())
                );
    }

    @Test
    public void testGetMpaByIdNotFound() {
        Optional<Mpa> mpa = mpaStorage.getById(999L);

        assertThat(mpa).isEmpty();
    }
}

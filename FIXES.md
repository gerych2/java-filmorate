# Исправления для H2 базы данных

## Проблема
При запуске приложения возникала ошибка:
```
Failed to execute SQL script statement #1 of file [data.sql]: INSERT INTO mpa (name) VALUES ('G') ON DUPLICATE KEY UPDATE name = name
```

## Причина
H2 база данных не поддерживает синтаксис `ON DUPLICATE KEY UPDATE`, который используется в MySQL.

## Исправления

### 1. Обновлен файл `schema.sql`
- Заменен `AUTO_INCREMENT` на `IDENTITY` (правильный синтаксис для H2)
- Оставлены все остальные настройки без изменений

### 2. Обновлен файл `data.sql`
- Заменен `INSERT ... ON DUPLICATE KEY UPDATE` на `MERGE INTO`
- Указаны конкретные ID для жанров и рейтингов MPA

### 3. Обновлен файл `src/test/resources/application.properties`
- Добавлены параметры для корректной работы H2 в памяти:
  - `DB_CLOSE_DELAY=-1` - база не закрывается при последнем соединении
  - `DB_CLOSE_ON_EXIT=FALSE` - база не закрывается при выходе

## Синтаксис H2 vs MySQL

| MySQL | H2 |
|-------|----|
| `AUTO_INCREMENT` | `BIGINT AUTO_INCREMENT` |
| `INTEGER IDENTITY` | `BIGINT AUTO_INCREMENT` |
| `INSERT ... ON DUPLICATE KEY UPDATE` | `MERGE INTO` |
| `IF NOT EXISTS` | `IF NOT EXISTS` (поддерживается) |

## Дополнительные исправления

### 4. Обновлены типы данных
- Заменены `int` на `Long` для всех ID в моделях
- Обновлены все DAO классы для работы с `Long` ID
- Обновлены сервисы и контроллеры для работы с `Long` ID
- В схеме БД использован `BIGINT` вместо `INTEGER` для ID

## Проверка исправлений

1. Удалите папку `db` (если существует)
2. Запустите приложение: `mvn spring-boot:run`
3. Приложение должно запуститься без ошибок
4. Проверьте работу API эндпоинтов

## Тестирование

Запустите тесты:
```bash
mvn test
```

Все интеграционные тесты должны пройти успешно.

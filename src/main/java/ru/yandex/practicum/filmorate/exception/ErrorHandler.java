package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ErrorHandler {

    // Ошибки валидации (аннотации @Valid, @Email, @NotBlank и т.п.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );
        return errors;
    }

    // Своя проверка и кастомные ошибки
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCustomValidation(ValidationException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return error;
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotFound(NoSuchElementException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Пользователь не найден");
        return error;
    }

    // Ловим все остальные исключения, чтобы они не падали 500 без ответа
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleAll(Exception e) {
        e.printStackTrace(); // лог ошибки в консоль
        Map<String, String> error = new HashMap<>();
        error.put("error", "Ошибка сервера");
        return error;
    }
}

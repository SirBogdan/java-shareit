package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ErrorHandlerTest {
    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleValidationTest() {
        ValidationException exception = new ValidationException("Неверный запрос");
        ErrorResponse response = errorHandler.handleValidation(exception);

        assertThat(response, notNullValue());
        assertThat(response.getError(), equalTo(exception.getMessage()));
    }

    @Test
    void handleObjectNotFoundTest() {
        ObjectNotFoundException exception = new ObjectNotFoundException("Запрашиваемый объект не найден");
        ErrorResponse response = errorHandler.handleObjectNotFound(exception);

        assertThat(response, notNullValue());
        assertThat(response.getError(), equalTo(exception.getMessage()));
    }

    @Test
    void handleConflictException() {
        ConflictException exception = new ConflictException("Внутренний конфликт");
        ErrorResponse response = errorHandler.handleConflict(exception);

        assertThat(response, notNullValue());
        assertThat(response.getError(), equalTo(exception.getMessage()));
    }
}

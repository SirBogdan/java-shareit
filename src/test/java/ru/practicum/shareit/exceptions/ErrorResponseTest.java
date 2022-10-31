package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ErrorResponseTest {
    private final ErrorResponse errorResponse = new ErrorResponse("Текст ошибки");

    @Test
    void errorResponseTest() {
        assertThat(errorResponse.getError(), equalTo("Текст ошибки"));
    }
}

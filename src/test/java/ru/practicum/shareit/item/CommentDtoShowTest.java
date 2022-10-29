package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDtoShow;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@JsonTest
public class CommentDtoShowTest {

    @Autowired
    private JacksonTester<CommentDtoShow> json;

    @Test
    void commentDtoShowSerializeTest() throws IOException {
        CommentDtoShow actual = new CommentDtoShow(1L, "text", "authorName",
                LocalDateTime.of(2022, 10, 25, 12, 34));

        JsonContent<CommentDtoShow> result = json.write(actual);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("authorName");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(
                "25 октября 2022, 12:34");
    }

    @Test
    void commentDtoShowDeserializeTest() throws IOException {
        String actualJson = "{\"id\":1,\"text\":\"text\",\"authorName\":\"authorName\"," +
                "\"created\":\"25 октября 2022, 12:34\"}";

        CommentDtoShow result = json.parse(actualJson).getObject();

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getText(), equalTo("text"));
        assertThat(result.getAuthorName(), equalTo("authorName"));
        assertThat(result.getCreated(), equalTo(
                LocalDateTime.of(2022, 10, 25, 12, 34)));
    }
}

package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoShowRequests;
import ru.practicum.shareit.request.dto.ItemRequestDtoShow;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@JsonTest
public class ItemRequestDtoShowTest {
    @Autowired
    private JacksonTester<ItemRequestDtoShow> json;

    @Test
    void itemRequestDtoShowSerializeTest() throws IOException {
        ItemRequestDtoShow actual = new ItemRequestDtoShow(1L, "description", 2L,
                LocalDateTime.of(2022, 10, 25, 12, 34, 56),
                List.of(new ItemDtoShowRequests(
                                3L, "item1", "item1Description", true, 1L),
                        new ItemDtoShowRequests(
                                4L, "item2", "item2Description", false, 1L
                        )));

        JsonContent<ItemRequestDtoShow> result = json.write(actual);
        System.out.println("QQQQQQQQQQQQQQ" + result);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2022-10-25T12:34:56");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("item1");
        assertThat(result).extractingJsonPathBooleanValue("$.items[1].available").isEqualTo(false);
    }

    @Test
    void itemRequestDtoShowDeserializeTest() throws IOException {
        String actualJson = "{\"id\":1,\"description\":\"description\",\"requestorId\":2,\"created\":" +
                "\"2022-10-25T12:34:56\",\"items\":[{\"id\":3,\"name\":\"item1\",\"description\":\"item1Description\"" +
                ",\"available\":true,\"requestId\":1},{\"id\":4,\"name\":\"item2\",\"description\":\"item2Description\"" +
                ",\"available\":false,\"requestId\":1}]}";

        ItemRequestDtoShow result = json.parse(actualJson).getObject();
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getDescription(), equalTo("description"));
        assertThat(result.getCreated(), equalTo(LocalDateTime.of(
                2022, 10, 25, 12, 34, 56)));
        assertThat(result.getItems().get(0).getName(), equalTo("item1"));
        assertThat(result.getItems().get(0).getAvailable(), equalTo(true));
        assertThat(result.getItems().get(0).getRequestId(), equalTo(1L));
    }
}

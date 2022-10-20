package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoForItemShow;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoShow;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoShowBookings;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ItemDtoCreate createItem(long userId, ItemDtoCreate itemDto) {
        itemDto.setOwner(UserMapper.fromUserDtoUpdate(userService.getUserById(userId)));
        Item item = itemRepository.save(ItemMapper.fromItemDtoCreate(itemDto));
        return ItemMapper.toItemDtoCreate(item);
    }

    @Transactional
    public ItemDtoUpdate updateItem(long userId, ItemDtoUpdate itemDto) {
        userService.checkUserExistsById(userId);
        checkItemBelongToUser(userId, itemDto.getId());
        Item item = getItemFromRepository(itemDto.getId());

        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        itemRepository.save(item);
        return ItemMapper.toItemDtoUpdate(item);
    }

    public ItemDtoShowBookings getItemById(long userId, long itemId) {
        Item item = getItemFromRepository(itemId);

        BookingDtoForItemShow lastBooking = BookingMapper.toBookingDtoForItemShow(bookingRepository.
                findFirstByItem_IdAndItem_Owner_IdAndEndBeforeOrderByEndDesc(item.getId(), userId, LocalDateTime.now()));
        BookingDtoForItemShow nextBooking = BookingMapper.toBookingDtoForItemShow(bookingRepository.
                findFirstByItem_IdAndItem_Owner_IdAndStartAfterOrderByEndDesc(item.getId(), userId, LocalDateTime.now()));

        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        List<CommentDtoShow> commentDtoShows = comments.stream().
                map(CommentMapper::toCommentDtoShow).collect(Collectors.toList());

        return ItemMapper.toItemDtoShowBookings(item, lastBooking, nextBooking, commentDtoShows);
    }

    public List<ItemDtoShowBookings> getAllItemsByUser(long userId) {
        userService.checkUserExistsById(userId);
        return itemRepository.findItemsByOwner_Id(userId).stream()
                .map(item -> getItemById(userId, item.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteItem(long userId, long itemId) {
        userService.checkUserExistsById(userId);
        checkItemBelongToUser(userId, itemId);
        itemRepository.deleteById(itemId);
    }

    public List<ItemDtoUpdate> searchItems(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findItemsByText(text).stream()
                .map(ItemMapper::toItemDtoUpdate)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDtoShow createComment(long userId, long itemId, CommentDtoShow commentDtoShow) {
        User user = UserMapper.fromUserDtoUpdate(userService.getUserById(userId));
        Item item = getItemFromRepository(itemId);

        if (commentDtoShow.getText().isBlank()) {
            throw new ValidationException("Комментарий не может быть пустым");
        }
        if (bookingRepository.findBookingsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new ValidationException(String.format(
                    "Пользователь с id %d никогда не арендовал вещь с id %d", userId, itemId));
        }

        Comment comment = CommentMapper.fromCommentDtoShow(commentDtoShow, item, user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        return CommentMapper.toCommentDtoShow(comment);
    }

    public void checkItemBelongToUser(long userId, long itemId) {
        if (getItemFromRepository(itemId).getOwner().getId() != userId) {
            throw new ObjectNotFoundException(String.format(
                    "Ошибка: вещь с id %d не принадлежит пользователю с id %d", itemId, userId));
        }
    }

    private Item getItemFromRepository(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Ошибка: вещь с id %d не существует", itemId)));
    }
}

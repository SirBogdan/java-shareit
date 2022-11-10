package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.CustomPageRequest;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;
import ru.practicum.shareit.request.dto.ItemRequestDtoShow;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestDtoShort createItemRequest(Long userId, ItemRequestDtoShort itemRequestDtoShort) {
        userService.checkUserExistsById(userId);
        if (itemRequestDtoShort.getDescription() == null || itemRequestDtoShort.getDescription().isBlank()) {
            throw new ValidationException("Ошибка: описание не может быть пустым");
        }

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDtoShort.getDescription());
        itemRequest.setRequestorId(userId);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDtoShort(itemRequest);
    }

    public List<ItemRequestDtoShow> getAllItemRequestsByUser(long userId) {
        userService.checkUserExistsById(userId);
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorId(userId);
        if (itemRequestList.isEmpty()) return Collections.emptyList();

        return addItemsAndMap(itemRequestList);
    }

    public List<ItemRequestDtoShow> getAllItemRequests(long userId, int from, int size) {
        userService.checkUserExistsById(userId);
        Pageable pageable = CustomPageRequest.of(from, size);
        List<ItemRequest> itemRequestList = itemRequestRepository.findAll(pageable)
                .stream()
                .filter(itemRequest -> itemRequest.getRequestorId() != userId)
                .collect(Collectors.toList());
        return addItemsAndMap(itemRequestList);
    }

    public ItemRequestDtoShow getItemRequestById(Long userId, Long requestId) {
        userService.checkUserExistsById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Ошибка: запрос с id %d не существует", requestId
                )));
        List<ItemRequestDtoShow> list = addItemsAndMap(List.of(itemRequest));
        return list.get(0);
    }

    private List<ItemRequestDtoShow> addItemsAndMap(List<ItemRequest> itemRequestList) {
        return itemRequestList.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDtoShow(
                        itemRequest,
                        itemRepository
                                .findAllByRequest_Id(itemRequest.getId())
                                .stream()
                                .map(ItemMapper::toItemDtoShowRequests)
                                .collect(Collectors.toList())))
                .sorted(Comparator.comparing(ItemRequestDtoShow::getCreated).reversed())
                .collect(Collectors.toList());
    }
}

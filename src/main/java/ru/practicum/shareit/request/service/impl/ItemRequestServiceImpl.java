package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto addItemRequest(int requesterId, NewItemRequestDto newItemRequestDto) {
        log.info("ItemRequestServiceImpl:addItemRequest(): запрос на создание нового запроса вещи {}", newItemRequestDto);
        ItemRequest newItemRequest = ItemRequestMapper.newItemRequestDtoToItemRequest(newItemRequestDto);
        newItemRequest.setRequesterId(requesterId);
        newItemRequest.setCreated(LocalDateTime.now());
        ItemRequest createdItemRequest = itemRequestRepository.save(newItemRequest);
        log.info("ItemRequestServiceImpl:addItemRequest(): создан новый запрос вещи {}", createdItemRequest);
        return ItemRequestMapper.itemRequestToItemRequestDto(createdItemRequest);
    }
}

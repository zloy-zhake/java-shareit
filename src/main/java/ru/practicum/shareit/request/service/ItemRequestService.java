package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(int requesterId, NewItemRequestDto newItemRequestDto);
}

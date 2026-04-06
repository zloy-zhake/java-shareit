package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NewItemRequestDto {
    private String name;
    private String description;
    private Boolean available;
    @JsonProperty("requestId")
    private Integer request;
}

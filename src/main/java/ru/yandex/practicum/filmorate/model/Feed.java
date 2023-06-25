package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Feed {
    private Integer eventId;
    private Long userId;
    private Long entityId;
    private EventType eventType;
    private Operation operation;
    private Long timestamp;
}
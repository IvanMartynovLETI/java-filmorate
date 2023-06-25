package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

public interface FeedStorage {

    void addFeedList(Long userId, Long entityId, EventType type, Operation operation);

    List<Feed> getFeedList(Long id);
}

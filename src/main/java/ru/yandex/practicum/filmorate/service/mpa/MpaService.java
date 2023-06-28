package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public Mpa getMpaById(int id) {
        log.info("Service layer: get MPA with id: '{}'.", id);

        return mpaStorage.getMpaById(id);
    }

    public Collection<Mpa> findAllMpa() {
        log.info("Service layer: get all MPA.");

        return mpaStorage.findAllMpa();
    }
}
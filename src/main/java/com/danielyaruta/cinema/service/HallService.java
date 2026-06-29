package com.danielyaruta.cinema.service;

import com.danielyaruta.cinema.dao.HallDao;
import com.danielyaruta.cinema.model.Hall;

import java.util.List;
import java.util.Optional;

// SRP: содержит только бизнес-логику для залов — без SQL и без I/O.
// DIP: зависит от интерфейса HallDao, конкретный класс инъецируется через конструктор.
public class HallService {

    private final HallDao hallDao;

    public HallService(HallDao hallDao) {
        this.hallDao = hallDao;
    }

    public Hall addHall(String name, int capacity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Hall name cannot be blank");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Hall name is too long (max 100 characters)");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be a positive number");
        }
        if (capacity > 5000) {
            throw new IllegalArgumentException("Capacity seems unrealistic (max 5000 seats)");
        }
        return hallDao.save(new Hall(0, name.trim(), capacity));
    }

    public Optional<Hall> getHall(long id) {
        return hallDao.findById(id);
    }

    public List<Hall> getAllHalls() {
        return hallDao.findAll();
    }
}

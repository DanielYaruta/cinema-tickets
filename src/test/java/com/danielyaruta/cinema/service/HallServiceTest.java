package com.danielyaruta.cinema.service;

import com.danielyaruta.cinema.dao.InMemoryHallDao;
import com.danielyaruta.cinema.model.Hall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HallServiceTest {

    private HallService service;

    @BeforeEach
    void setUp() {
        service = new HallService(new InMemoryHallDao());
    }

    // --- Успешные сценарии ---

    @Test
    void addHall_validData_returnsHallWithGeneratedId() {
        Hall hall = service.addHall("Hall A", 100);

        assertTrue(hall.getId() > 0);
        assertEquals("Hall A", hall.getName());
        assertEquals(100, hall.getCapacity());
    }

    @Test
    void addHall_nameWithSpaces_isTrimmed() {
        Hall hall = service.addHall("  Hall B  ", 50);
        assertEquals("Hall B", hall.getName());
    }

    @Test
    void getAllHalls_afterAddingTwo_returnsBoth() {
        service.addHall("Hall A", 100);
        service.addHall("Hall B", 50);
        List<Hall> halls = service.getAllHalls();
        assertEquals(2, halls.size());
    }

    @Test
    void getHall_existingId_returnsHall() {
        Hall saved = service.addHall("Hall A", 100);
        assertTrue(service.getHall(saved.getId()).isPresent());
    }

    @Test
    void getHall_nonExistingId_returnsEmpty() {
        assertTrue(service.getHall(999L).isEmpty());
    }

    // --- Валидация: название ---

    @Test
    void addHall_nullName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addHall(null, 100));
    }

    @Test
    void addHall_blankName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addHall("  ", 100));
    }

    @Test
    void addHall_nameTooLong_throwsIllegalArgumentException() {
        String longName = "H".repeat(101);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addHall(longName, 100));
        assertTrue(ex.getMessage().contains("100"));
    }

    // --- Валидация: вместимость ---

    @Test
    void addHall_zeroCapacity_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addHall("Hall A", 0));
        assertTrue(ex.getMessage().contains("positive"));
    }

    @Test
    void addHall_negativeCapacity_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addHall("Hall A", -1));
    }

    @Test
    void addHall_capacityExceedsMax_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addHall("Hall A", 5001));
        assertTrue(ex.getMessage().contains("5000"));
    }
}

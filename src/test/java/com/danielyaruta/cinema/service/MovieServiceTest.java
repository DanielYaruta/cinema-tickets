package com.danielyaruta.cinema.service;

import com.danielyaruta.cinema.dao.InMemoryMovieDao;
import com.danielyaruta.cinema.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieServiceTest {

    private MovieService service;

    @BeforeEach
    void setUp() {
        service = new MovieService(new InMemoryMovieDao());
    }

    // --- Успешные сценарии ---

    @Test
    void addMovie_validData_returnsMovieWithGeneratedId() {
        Movie movie = service.addMovie("Inception", "Sci-Fi", 148);

        assertTrue(movie.getId() > 0);
        assertEquals("Inception", movie.getTitle());
        assertEquals("Sci-Fi", movie.getGenre());
        assertEquals(148, movie.getDurationMinutes());
    }

    @Test
    void addMovie_titleWithExtraSpaces_isTrimmed() {
        Movie movie = service.addMovie("  Inception  ", "Sci-Fi", 148);
        assertEquals("Inception", movie.getTitle());
    }

    @Test
    void addMovie_nullGenre_savedAsEmptyString() {
        Movie movie = service.addMovie("Inception", null, 148);
        assertEquals("", movie.getGenre());
    }

    @Test
    void getAllMovies_afterAddingTwo_returnsBoth() {
        service.addMovie("Movie A", "Drama", 90);
        service.addMovie("Movie B", "Comedy", 100);

        List<Movie> movies = service.getAllMovies();
        assertEquals(2, movies.size());
    }

    @Test
    void getMovie_existingId_returnsMovie() {
        Movie saved = service.addMovie("Inception", "Sci-Fi", 148);
        assertTrue(service.getMovie(saved.getId()).isPresent());
    }

    @Test
    void getMovie_nonExistingId_returnsEmpty() {
        assertTrue(service.getMovie(999L).isEmpty());
    }

    // --- Валидация: заголовок ---

    @Test
    void addMovie_nullTitle_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addMovie(null, "Drama", 90));
        assertTrue(ex.getMessage().contains("title"));
    }

    @Test
    void addMovie_blankTitle_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addMovie("   ", "Drama", 90));
    }

    @Test
    void addMovie_titleTooLong_throwsIllegalArgumentException() {
        String longTitle = "A".repeat(256);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addMovie(longTitle, "Drama", 90));
        assertTrue(ex.getMessage().contains("255"));
    }

    @Test
    void addMovie_genreTooLong_throwsIllegalArgumentException() {
        String longGenre = "G".repeat(101);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addMovie("Title", longGenre, 90));
        assertTrue(ex.getMessage().contains("100"));
    }

    // --- Валидация: продолжительность ---

    @Test
    void addMovie_zeroDuration_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addMovie("Title", "Drama", 0));
        assertTrue(ex.getMessage().contains("positive"));
    }

    @Test
    void addMovie_negativeDuration_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addMovie("Title", "Drama", -10));
    }

    @Test
    void addMovie_durationExceedsMax_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addMovie("Title", "Drama", 601));
        assertTrue(ex.getMessage().contains("600"));
    }
}

package com.danielyaruta.cinema.cli.commands;

import com.danielyaruta.cinema.cli.Command;
import com.danielyaruta.cinema.cli.ConsoleInput;
import com.danielyaruta.cinema.model.Hall;
import com.danielyaruta.cinema.model.Movie;
import com.danielyaruta.cinema.model.Session;
import com.danielyaruta.cinema.service.HallService;
import com.danielyaruta.cinema.service.MovieService;
import com.danielyaruta.cinema.service.SessionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AddSessionCommand implements Command {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final SessionService sessionService;
    private final MovieService   movieService;
    private final HallService    hallService;

    public AddSessionCommand(SessionService sessionService,
                             MovieService movieService,
                             HallService hallService) {
        this.sessionService = sessionService;
        this.movieService   = movieService;
        this.hallService    = hallService;
    }

    @Override public String getKey()         { return "3"; }
    @Override public String getDescription() { return "Add session"; }

    @Override
    public void execute(ConsoleInput input) {
        List<Movie> movies = movieService.getAllMovies();
        if (movies.isEmpty()) {
            System.out.println("No movies available. Add a movie first (command 1).");
            return;
        }
        System.out.println("Available movies:");
        movies.forEach(m -> System.out.printf("  [%d] %s (%s, %d min)%n",
                m.getId(), m.getTitle(), m.getGenre(), m.getDurationMinutes()));

        List<Hall> halls = hallService.getAllHalls();
        if (halls.isEmpty()) {
            System.out.println("No halls available. Add a hall first (command 2).");
            return;
        }
        System.out.println("Available halls:");
        halls.forEach(h -> System.out.printf("  [%d] %s (capacity: %d)%n",
                h.getId(), h.getName(), h.getCapacity()));

        long        movieId   = input.readLong("Movie ID: ");
        long        hallId    = input.readLong("Hall ID: ");
        LocalDateTime start   = input.readLocalDateTime("Start time (yyyy-MM-dd HH:mm): ", FMT);
        BigDecimal  price     = input.readBigDecimal("Ticket price: ");

        Session session = sessionService.addSession(movieId, hallId, start, price);
        System.out.println("Session added: " + session);
    }
}

package com.danielyaruta.cinema.cli.commands;

import com.danielyaruta.cinema.cli.Command;
import com.danielyaruta.cinema.cli.ConsoleInput;
import com.danielyaruta.cinema.model.Session;
import com.danielyaruta.cinema.service.HallService;
import com.danielyaruta.cinema.service.MovieService;
import com.danielyaruta.cinema.service.SessionService;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListSessionsCommand implements Command {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final SessionService sessionService;
    private final MovieService   movieService;
    private final HallService    hallService;

    public ListSessionsCommand(SessionService sessionService,
                               MovieService movieService,
                               HallService hallService) {
        this.sessionService = sessionService;
        this.movieService   = movieService;
        this.hallService    = hallService;
    }

    @Override public String getKey()         { return "4"; }
    @Override public String getDescription() { return "List sessions"; }

    @Override
    public void execute(ConsoleInput input) {
        List<Session> sessions = sessionService.getAllSessions();
        if (sessions.isEmpty()) {
            System.out.println("No sessions available.");
            return;
        }

        System.out.printf("%-5s %-28s %-16s %-18s %-10s%n",
                "ID", "Movie", "Hall", "Start time", "Price");
        System.out.println("-".repeat(80));

        for (Session s : sessions) {
            String movieTitle = movieService.getMovie(s.getMovieId())
                    .map(m -> m.getTitle()).orElse("(unknown)");
            String hallName = hallService.getHall(s.getHallId())
                    .map(h -> h.getName()).orElse("(unknown)");

            System.out.printf("%-5d %-28s %-16s %-18s %-10.2f%n",
                    s.getId(),
                    truncate(movieTitle, 27),
                    truncate(hallName, 15),
                    s.getStartTime().format(FMT),
                    s.getPrice());
        }
    }

    private String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}

package com.danielyaruta.cinema.cli.commands;

import com.danielyaruta.cinema.cli.Command;
import com.danielyaruta.cinema.cli.ConsoleInput;
import com.danielyaruta.cinema.model.Movie;
import com.danielyaruta.cinema.service.MovieService;

import java.util.List;

public class ListMoviesCommand implements Command {

    private final MovieService movieService;

    public ListMoviesCommand(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override public String getKey()         { return "9"; }
    @Override public String getDescription() { return "List all movies"; }

    @Override
    public void execute(ConsoleInput input) {
        List<Movie> movies = movieService.getAllMovies();
        if (movies.isEmpty()) {
            System.out.println("No movies found.");
            return;
        }
        System.out.printf("%-5s %-30s %-15s %-10s%n", "ID", "Title", "Genre", "Duration");
        System.out.println("-".repeat(63));
        for (Movie m : movies) {
            System.out.printf("%-5d %-30s %-15s %-10s%n",
                    m.getId(), truncate(m.getTitle(), 29),
                    truncate(m.getGenre(), 14), m.getDurationMinutes() + " min");
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}

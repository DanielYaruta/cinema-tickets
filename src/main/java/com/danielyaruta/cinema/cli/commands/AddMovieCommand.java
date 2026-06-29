package com.danielyaruta.cinema.cli.commands;

import com.danielyaruta.cinema.cli.Command;
import com.danielyaruta.cinema.cli.ConsoleInput;
import com.danielyaruta.cinema.model.Movie;
import com.danielyaruta.cinema.service.MovieService;

public class AddMovieCommand implements Command {

    private final MovieService movieService;

    public AddMovieCommand(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override public String getKey()         { return "1"; }
    @Override public String getDescription() { return "Add movie"; }

    @Override
    public void execute(ConsoleInput input) {
        String title  = input.readLine("Title: ");
        String genre  = input.readLine("Genre: ");
        int duration  = input.readInt("Duration (minutes): ");

        Movie movie = movieService.addMovie(title, genre, duration);
        System.out.println("Movie added: " + movie);
    }
}

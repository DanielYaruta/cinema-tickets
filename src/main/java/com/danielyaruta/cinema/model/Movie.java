package com.danielyaruta.cinema.model;

public class Movie {

    private long id;
    private String title;
    private String genre;
    private int durationMinutes;

    public Movie() {}

    public Movie(long id, String title, String genre, int durationMinutes) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    @Override
    public String toString() {
        return String.format("Movie{id=%d, title='%s', genre='%s', duration=%d min}",
                id, title, genre, durationMinutes);
    }
}

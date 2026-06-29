package com.danielyaruta.cinema.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Session {

    private long id;
    private long movieId;
    private long hallId;
    private LocalDateTime startTime;
    private BigDecimal price;

    public Session() {}

    public Session(long id, long movieId, long hallId, LocalDateTime startTime, BigDecimal price) {
        this.id = id;
        this.movieId = movieId;
        this.hallId = hallId;
        this.startTime = startTime;
        this.price = price;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getMovieId() { return movieId; }
    public void setMovieId(long movieId) { this.movieId = movieId; }

    public long getHallId() { return hallId; }
    public void setHallId(long hallId) { this.hallId = hallId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override
    public String toString() {
        return String.format("Session{id=%d, movieId=%d, hallId=%d, startTime=%s, price=%s}",
                id, movieId, hallId, startTime, price);
    }
}

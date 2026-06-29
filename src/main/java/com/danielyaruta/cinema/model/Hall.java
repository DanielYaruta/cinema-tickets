package com.danielyaruta.cinema.model;

public class Hall {

    private long id;
    private String name;
    private int capacity;

    public Hall() {}

    public Hall(long id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    @Override
    public String toString() {
        return String.format("Hall{id=%d, name='%s', capacity=%d}", id, name, capacity);
    }
}

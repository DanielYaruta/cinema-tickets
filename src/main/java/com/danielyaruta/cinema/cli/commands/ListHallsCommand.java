package com.danielyaruta.cinema.cli.commands;

import com.danielyaruta.cinema.cli.Command;
import com.danielyaruta.cinema.cli.ConsoleInput;
import com.danielyaruta.cinema.model.Hall;
import com.danielyaruta.cinema.service.HallService;

import java.util.List;

public class ListHallsCommand implements Command {

    private final HallService hallService;

    public ListHallsCommand(HallService hallService) {
        this.hallService = hallService;
    }

    @Override public String getKey()         { return "10"; }
    @Override public String getDescription() { return "List all halls"; }

    @Override
    public void execute(ConsoleInput input) {
        List<Hall> halls = hallService.getAllHalls();
        if (halls.isEmpty()) {
            System.out.println("No halls found.");
            return;
        }
        System.out.printf("%-5s %-25s %-10s%n", "ID", "Name", "Capacity");
        System.out.println("-".repeat(42));
        for (Hall h : halls) {
            System.out.printf("%-5d %-25s %-10d%n", h.getId(), h.getName(), h.getCapacity());
        }
    }
}

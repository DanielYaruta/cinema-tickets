package com.danielyaruta.cinema.cli.commands;

import com.danielyaruta.cinema.cli.Command;
import com.danielyaruta.cinema.cli.ConsoleInput;
import com.danielyaruta.cinema.model.Hall;
import com.danielyaruta.cinema.service.HallService;

public class AddHallCommand implements Command {

    private final HallService hallService;

    public AddHallCommand(HallService hallService) {
        this.hallService = hallService;
    }

    @Override public String getKey()         { return "2"; }
    @Override public String getDescription() { return "Add hall"; }

    @Override
    public void execute(ConsoleInput input) {
        String name  = input.readLine("Hall name: ");
        int capacity = input.readInt("Capacity (number of seats): ");

        Hall hall = hallService.addHall(name, capacity);
        System.out.println("Hall added: " + hall);
    }
}

package com.danielyaruta.cinema.cli.commands;

import com.danielyaruta.cinema.cli.Command;
import com.danielyaruta.cinema.cli.ConsoleInput;
import com.danielyaruta.cinema.service.TicketService;

public class CancelTicketCommand implements Command {

    private final TicketService ticketService;

    public CancelTicketCommand(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override public String getKey()         { return "6"; }
    @Override public String getDescription() { return "Cancel ticket"; }

    @Override
    public void execute(ConsoleInput input) {
        long ticketId = input.readLong("Ticket ID to cancel: ");
        ticketService.cancelTicket(ticketId);
        System.out.println("Ticket " + ticketId + " has been cancelled.");
    }
}

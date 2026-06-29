package com.danielyaruta.cinema.cli.commands;

import com.danielyaruta.cinema.cli.Command;
import com.danielyaruta.cinema.cli.ConsoleInput;
import com.danielyaruta.cinema.model.Ticket;
import com.danielyaruta.cinema.service.TicketService;

import java.util.List;

public class ListTicketsBySessionCommand implements Command {

    private final TicketService ticketService;

    public ListTicketsBySessionCommand(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override public String getKey()         { return "7"; }
    @Override public String getDescription() { return "List tickets by session"; }

    @Override
    public void execute(ConsoleInput input) {
        long sessionId = input.readLong("Session ID: ");

        List<Ticket> tickets = ticketService.getTicketsBySession(sessionId);
        if (tickets.isEmpty()) {
            System.out.println("No tickets found for session " + sessionId + ".");
            return;
        }

        System.out.printf("%-6s %-6s %-12s %-20s%n", "ID", "Seat", "Status", "Customer");
        System.out.println("-".repeat(48));
        for (Ticket t : tickets) {
            System.out.printf("%-6d %-6d %-12s %-20s%n",
                    t.getId(), t.getSeatNumber(), t.getStatus(), t.getCustomerName());
        }
    }
}

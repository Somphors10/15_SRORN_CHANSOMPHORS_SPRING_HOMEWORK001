package org.example.homework001.controller;

import jakarta.websocket.server.PathParam;
import org.example.homework001.model.entity.Ticket;
import org.example.homework001.model.request.TicketRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("api/v1/tickets")
public class TicketController {

    //Create Array to store data
    private final static List<Ticket> TICKETS = new ArrayList<>();

    // Create ID auto increment
    private final static AtomicLong ATOMIC_LONG = new AtomicLong(3L);

    public TicketController() {
        TICKETS.add(new Ticket(1L, "hii",
                LocalDate.of(2025, 10, 3),
                "cambodia",
                "Phnom Penh",
                15.0
                ));
        TICKETS.add(new Ticket(2L,
                "Hello",
                LocalDate.of(2025, 5, 3),
                "South Korea",
                "Seoul",
                40.5
                ));

    }

    //Create a Ticket
    @PostMapping
    public Ticket creatTicket(@RequestBody TicketRequest request){
        Ticket ticket = new Ticket(ATOMIC_LONG.getAndIncrement(),
                request.getPassengerName(),
                request.getTravelDate(),
                request.getSourceStation(),
                request.getDestinationStation(),
                request.getPrice());
        TICKETS.add(ticket);
        return ticket;
    }

    // Retrieve all Ticket
    @GetMapping
    public List<Ticket> getTickets(){
        return TICKETS;
    }

    // Retrieve a Ticket by ID
    @GetMapping("/{ticket-id}")
    public Ticket getTicketByID(@PathVariable("ticket-id") Long ticketId){
        for (Ticket ticket : TICKETS) {
            if (ticket.getTicketId().equals(ticketId)) {
                return ticket;
            }
        }
        return null;
    }

    // Search ticket by passengerName
    @GetMapping("/search")
    public List<Ticket> searchTicketByName(@RequestParam("search") String passengerName){
        List<Ticket> tickets = new ArrayList<>();
        for (Ticket ticket : TICKETS){
            if (ticket.getPassengerName().toLowerCase().contains(passengerName)){
                tickets.add(ticket);
            }
        }
        return tickets;
    }

    //Filter Tickets by Ticket Status and Travel Date


    //Update ticket by ID
    @PutMapping("/{ticket-id}")
    public Ticket updateTicketById(@PathVariable("ticket-id") Long ticketId,
                                   @RequestBody TicketRequest request){
        for (Ticket ticket : TICKETS) {
            if (ticket.getTicketId().equals(ticketId)) {
                ticket.setPassengerName(request.getPassengerName());
                ticket.setTravelDate(request.getTravelDate());
                ticket.setSourceStation(request.getSourceStation());
                ticket.setDestinationStation(request.getDestinationStation());
                ticket.setPrice(request.getPrice());
                return ticket;

            }
        }
        return null;
    }

    // Delete ticket by ID
    @DeleteMapping("/{ticket-id}")
    public String deleteTicketById(@PathVariable("ticket-id") Long ticketId){
        for (Ticket ticket : TICKETS) {
            if (ticket.getTicketId().equals(ticketId)) {
                TICKETS.remove(ticket);
                return "Deleted ticket has been successfully ";
            }
        }
        return null;
    }
}

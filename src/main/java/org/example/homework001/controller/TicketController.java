package org.example.homework001.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.websocket.server.PathParam;
import org.example.homework001.model.apiRespone.ApiResponse;
import org.example.homework001.model.entity.Ticket;
import org.example.homework001.model.request.PaymentStausRequest;
import org.example.homework001.model.request.TicketRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/tickets")
public class TicketController {

    //Create Array to store data
    private final static List<Ticket> TICKETS = new ArrayList<>();

    // Create ID auto increment
    private final static AtomicLong ATOMIC_LONG = new AtomicLong(4L);

    //Default Data
    public TicketController() {
        TICKETS.add(new Ticket(1L, "Heesung", LocalDate.of(2025, 10, 3), "Station G", "Station H", 150.0, true, "COMPLETED", "D4"));
        TICKETS.add(new Ticket(2L, "Sunghoon", LocalDate.of(2025, 5, 3), "Station E", "Station G", 40.5, true, "BOOKED", "D5"));
        TICKETS.add(new Ticket(3L, "Jake", LocalDate.of(2025, 5, 3), "Siem Reab", "Psa Tmey", 40.5, false, "CANCEL", "D6"));
    }


    //Create a Ticket
    @PostMapping
    @Operation(summary = "Create a new ticket")
    public ResponseEntity<ApiResponse<Ticket>> creatTicket(@RequestBody TicketRequest request){
        Ticket ticket = new Ticket(ATOMIC_LONG.getAndIncrement(),
                request.getPassengerName(),
                request.getTravelDate(),
                request.getSourceStation(),
                request.getDestinationStation(),
                request.getPrice(),
                request.getPaymentStatus(),
                request.getTicketStatus(),
                request.getSeatNumber());
        TICKETS.add(ticket);
        ApiResponse<Ticket> response = new ApiResponse<>(
                true,
                "Create successfully",
                HttpStatus.CREATED,
                ticket,
                LocalDateTime.now()
        );
        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }



    //Retrieve all tickets with dynamic pagination
    @GetMapping
    @Operation(summary = "Get all tickets")
    public List<Ticket> getTickets(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        int start = page * size;
        int end = Math.min(start + size, TICKETS.size());
        return (start >= TICKETS.size()) ? new ArrayList<>() : TICKETS.subList(start, end);
    }

    // Retrieve a Ticket by ID
    @GetMapping("/{ticket-id}")
    @Operation(summary = "Get ticket by ID")
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
    @Operation(summary = "Search tickets by passenger name")
    public ResponseEntity<ApiResponse<Ticket>> searchTicketByName(@RequestParam("search") String passengerName){
        List<Ticket> tickets = new ArrayList<>();
        for (Ticket ticket : TICKETS){
            if (ticket.getPassengerName().toLowerCase().contains(passengerName)){
                tickets.add(ticket);
                ApiResponse<Ticket> response = new ApiResponse<>(
                        true,
                        "Search success",
                        HttpStatus.OK,
                        ticket,
                        LocalDateTime.now()
                );
                return  ResponseEntity.status(HttpStatus.OK).body(response);

            }
        }
        return null;
    }

    //Filter Tickets by Ticket Status and Travel Date
    @GetMapping("/filter")
    @Operation(summary = "Filter tickets by status and travel date")
    public ResponseEntity<ApiResponse<List<Ticket>>> filterTickets(
            @RequestParam(value = "status", required = false) String ticketStatus,
            @RequestParam(value = "date", required = false) LocalDate travelDate) {

        List<Ticket> filteredTickets = TICKETS.stream()
                .filter(ticket -> (ticketStatus == null || ticket.getTicketStatus().equalsIgnoreCase(ticketStatus)))
                .filter(ticket -> (travelDate == null || ticket.getTravelDate().equals(travelDate)))
                .collect(Collectors.toList());

        ApiResponse<List<Ticket>> response = new ApiResponse<>(
                true,
                "Tickets filtered successfully",
                HttpStatus.OK,
                filteredTickets,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //Update ticket by ID
    @PutMapping("/{ticket-id}")
    @Operation(summary = "Update an existing ticket by ID")
    public ResponseEntity<ApiResponse<Ticket>> updateTicketById(@PathVariable("ticket-id") Long ticketId, @RequestBody TicketRequest request) {
        for (Ticket ticket : TICKETS) {
            if (ticket.getTicketId().equals(ticketId)) {
                ticket.setPassengerName(request.getPassengerName());
                ticket.setTravelDate(request.getTravelDate());
                ticket.setSourceStation(request.getSourceStation());
                ticket.setDestinationStation(request.getDestinationStation());
                ticket.setPrice(request.getPrice());
                ticket.setPaymentStatus(request.getPaymentStatus());
                ticket.setTicketStatus(request.getTicketStatus());
                ticket.setSeatNumber(request.getSeatNumber());
                return ResponseEntity.ok(new ApiResponse<>(true, "Ticket updated successfully", HttpStatus.OK, ticket, LocalDateTime.now()));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Ticket not found", HttpStatus.NOT_FOUND, null, LocalDateTime.now()));
    }


    // Delete ticket by ID
    @DeleteMapping("/{ticket-id}")
    @Operation(summary = "Deleted ticket by ID")
    public String deleteTicketById(@PathVariable("ticket-id") Long ticketId){
        for (Ticket ticket : TICKETS) {
            if (ticket.getTicketId().equals(ticketId)) {
                TICKETS.remove(ticket);
                return "Deleted ticket has been successfully ";
            }
        }
        return null;
    }

    //Create a multiple Ticket in single request
    @PostMapping("/bulk")
    @Operation(summary = "Bulk create tickets")
    public List<TicketRequest> creatTicket(@RequestBody List<TicketRequest>  requests){

        for (TicketRequest request : requests){
            Ticket ticket = new Ticket(ATOMIC_LONG.getAndIncrement(),
                    request.getPassengerName(),
                    request.getTravelDate(),
                    request.getSourceStation(),
                    request.getDestinationStation(),
                    request.getPrice(),
                    request.getPaymentStatus(),
                    request.getTicketStatus(),
                    request.getSeatNumber());
            TICKETS.add(ticket);
        }
        return requests;
    }


    // Update ticket by payment status for multiple ticket IDs
    @PutMapping
    @Operation(summary = "Bulk update payment status for multiple tickets")
    public ResponseEntity<ApiResponse<List<Ticket>>> updateTicketByPaymentStatus(
            @RequestBody List<PaymentStausRequest> requests) {

        List<Ticket> updatedTickets = new ArrayList<>();

        for (PaymentStausRequest request : requests) {
            for (Integer ticketId : request.getTicketId()) {
                for (Ticket ticket : TICKETS) {
                    if (ticket.getTicketId().equals(ticketId.longValue())) {
                        ticket.setPaymentStatus(request.getPaymentStatus());
                        updatedTickets.add(ticket);
                    }
                }
            }
        }

        ApiResponse<List<Ticket>> response = new ApiResponse<>(
                true,
                "Payment status updated successfully",
                HttpStatus.OK,
                updatedTickets,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

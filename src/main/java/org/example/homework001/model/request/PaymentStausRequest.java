package org.example.homework001.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStausRequest {
    List<Integer> ticketId;
    private Boolean paymentStatus;
}

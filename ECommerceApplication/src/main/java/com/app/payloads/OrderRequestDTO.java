package com.app.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private String paymentMethod;
    private AddressDTO addressDTO;
    private BankTransferDTO bankTransferDTO;
}

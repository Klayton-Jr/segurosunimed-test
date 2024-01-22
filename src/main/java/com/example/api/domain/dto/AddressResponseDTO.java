package com.example.api.domain.dto;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDTO {
    private String zipCode;
    private String street;
    private String complement;
    private String city;
    private String state;
}

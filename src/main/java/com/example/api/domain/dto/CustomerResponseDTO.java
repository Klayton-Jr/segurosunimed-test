package com.example.api.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String gender;
    private List<AddressResponseDTO> addresses;
}
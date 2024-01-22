package com.example.api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerDTO {
    private String name;
    @Email(message = "Email inválido")
    private String email;
    @Size(max = 8, message = "CEP não pode ter mais de 8 dígitos")
    private String zipCode;
}

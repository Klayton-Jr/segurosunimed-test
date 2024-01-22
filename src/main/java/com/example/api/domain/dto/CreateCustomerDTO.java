package com.example.api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCustomerDTO {

    @NotEmpty(message = "O nome é obrigatório")
    private String name;
    @NotEmpty(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    @NotEmpty(message = "O gênero é obrigatório")
    @Pattern(regexp = "[FM]", message = "O gênero deve ser F para Feminino ou M para Masculino")
    private String gender;
    @Size(max = 8, message = "CEP não pode ter mais de 8 dígitos")
    private String zipCode;
}

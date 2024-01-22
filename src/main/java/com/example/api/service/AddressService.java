package com.example.api.service;

import com.example.api.domain.dto.AddressResponseDTO;
import com.example.api.domain.dto.AddressViaCepDTO;
import com.example.api.exception.CustomValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;

@Service
@AllArgsConstructor
public class AddressService {

    private static final String VIA_CEP_URL_REQUEST = "https://viacep.com.br/ws/%s/json/";
    private final RestTemplate restTemplate;

    public AddressResponseDTO getAddressFromZipCode(String zipCode) {
        String url = String.format(VIA_CEP_URL_REQUEST, zipCode);

        try {
            ResponseEntity<AddressViaCepDTO> response = restTemplate.getForEntity(url, AddressViaCepDTO.class);
            if (response.getBody() != null) {
                return convertToAddressResponseDTO(response.getBody());
            } else {
                throw new EntityNotFoundException("CEP não encontrado");
            }
        } catch (HttpClientErrorException e) {
            throw new CustomValidationException("Erro ao consultar CEP", e.getStatusCode());
        }
    }

    private AddressResponseDTO convertToAddressResponseDTO(AddressViaCepDTO addressViaCepDTO) {
        return AddressResponseDTO.builder()
                .city(addressViaCepDTO.getLocalidade())
                .state(addressViaCepDTO.getUf())
                .complement(addressViaCepDTO.getComplemento())
                .street(addressViaCepDTO.getLogradouro())
                .zipCode(addressViaCepDTO.getCep())
                .build();
    }
}

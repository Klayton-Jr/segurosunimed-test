package com.example.api.service;

import com.example.api.domain.dto.AddressResponseDTO;
import com.example.api.domain.dto.AddressViaCepDTO;
import com.example.api.exception.CustomValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private AddressService service;

    @BeforeEach
    void setUp() {
        this.service = new AddressService(restTemplate);
    }

    @Test
    void deveBuscarEnderecoPeloCep() {
        String cep = "74000000";

        var mockResponseAddress = new AddressViaCepDTO();
        mockResponseAddress.setCep(cep);
        mockResponseAddress.setUf("GO");
        mockResponseAddress.setLocalidade("Goiania");

        when(restTemplate.getForEntity("https://viacep.com.br/ws/74000000/json/", AddressViaCepDTO.class))
                .thenReturn(ResponseEntity.ok().body(mockResponseAddress));

        var response = service.getAddressFromZipCode(cep);

        assertEquals("GO", response.getState());
        assertEquals("Goiania", response.getCity());
    }

    @Test
    void deveBuscarEnderecoPeloCepComCepInexistente() {
        String cep = "74000000";

        var mockResponseAddress = new AddressViaCepDTO();
        mockResponseAddress.setCep(cep);
        mockResponseAddress.setUf("GO");
        mockResponseAddress.setLocalidade("Goiania");

        when(restTemplate.getForEntity("https://viacep.com.br/ws/74000000/json/", AddressViaCepDTO.class))
                .thenReturn(ResponseEntity.ok().body(null));

        assertThrows(EntityNotFoundException.class, () -> service.getAddressFromZipCode(cep));
    }

    @Test
    void deveBuscarEnderecoPeloCepComErroNoServico() {
        String cep = "74000000";

        var mockResponseAddress = new AddressViaCepDTO();
        mockResponseAddress.setCep(cep);
        mockResponseAddress.setUf("GO");
        mockResponseAddress.setLocalidade("Goiania");

        when(restTemplate.getForEntity("https://viacep.com.br/ws/74000000/json/", AddressViaCepDTO.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Erro no servico"));

        assertThrows(CustomValidationException.class, () -> service.getAddressFromZipCode(cep));
    }
}

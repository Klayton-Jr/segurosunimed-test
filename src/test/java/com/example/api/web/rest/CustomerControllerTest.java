package com.example.api.web.rest;

import com.example.api.domain.dto.CreateCustomerDTO;
import com.example.api.domain.dto.CustomerResponseDTO;
import com.example.api.domain.dto.UpdateCustomerDTO;
import com.example.api.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
public class CustomerControllerTest {

    @MockBean
    private CustomerService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void deveListarTodosClientes() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).findAll(any(), any(), any(), any(), any(), any());
    }

    @Test
    void deveDetalharUmCliente() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/customers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).findById(any());
    }

    @Test
    void deveRemoverUmCliente() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/customers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service, times(1)).removeCustomer(any());
    }

    @Test
    void deveCriarUmNovoCliente() throws Exception {
        var customerDto = new CreateCustomerDTO("nome", "123@gmail.com", "F", null);

        var response = new CustomerResponseDTO();
        response.setId(1L);
        when(service.create(any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .content(mapper.writeValueAsString(customerDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(service, times(1)).create(any());
    }

    @Test
    void deveAtualizarParcialUmCliente() throws Exception {
        var updateCustomerDto = new UpdateCustomerDTO();

        var response = new CustomerResponseDTO();
        response.setId(1L);
        when(service.patch(any(), any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.patch("/customers/{id}", 1L)
                        .content(mapper.writeValueAsString(updateCustomerDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).patch(any(), any());
    }

    @Test
    void deveAtualizarTodosDadosCliente() throws Exception {
        var updateCustomerDto = new UpdateCustomerDTO();

        var response = new CustomerResponseDTO();
        response.setId(1L);
        when(service.patch(any(), any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.put("/customers/{id}", 1L)
                        .content(mapper.writeValueAsString(updateCustomerDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).put(any(), any());
    }
}

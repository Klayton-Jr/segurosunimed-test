package com.example.api.service;

import com.example.api.domain.Customer;
import com.example.api.domain.dto.AddressResponseDTO;
import com.example.api.domain.dto.CreateCustomerDTO;
import com.example.api.domain.dto.CustomerResponseDTO;
import com.example.api.domain.dto.UpdateCustomerDTO;
import com.example.api.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;
    @Mock
    private AddressService addressService;

    private CustomerService service;

    @BeforeEach
    void setUp() {
        this.service = new CustomerService(repository, new ModelMapper(), addressService);
    }

    @Test
    void deveCriarCliente() {

        var createCustomer = CreateCustomerDTO.builder()
                .name("nome")
                .email("123@gmail.com")
                .gender("M")
                .build();
        var mockCustomer = new Customer();
        mockCustomer.setName(createCustomer.getName());
        mockCustomer.setEmail(createCustomer.getEmail());
        mockCustomer.setGender(createCustomer.getGender());

        when(repository.save(any())).thenReturn(mockCustomer);


        CustomerResponseDTO customerResponseDTO = service.create(createCustomer);

        assertEquals("nome", customerResponseDTO.getName());
        assertEquals("123@gmail.com", customerResponseDTO.getEmail());
        assertEquals("M", customerResponseDTO.getGender());
    }

    @Test
    void deveCriarClienteComCepInformado() {

        var createCustomer = CreateCustomerDTO.builder()
                .name("nome")
                .email("123@gmail.com")
                .gender("M")
                .zipCode("74000000")
                .build();
        var mockCustomer = new Customer();
        mockCustomer.setName(createCustomer.getName());
        mockCustomer.setEmail(createCustomer.getEmail());
        mockCustomer.setGender(createCustomer.getGender());
        mockCustomer.setId(1L);

        when(repository.save(any())).thenReturn(mockCustomer);
        when(addressService.getAddressFromZipCode(any())).thenReturn(AddressResponseDTO.builder().zipCode("74000000").build());


        CustomerResponseDTO customerResponseDTO = service.create(createCustomer);

        assertEquals("nome", customerResponseDTO.getName());
        assertEquals("123@gmail.com", customerResponseDTO.getEmail());
        assertEquals("M", customerResponseDTO.getGender());
        assertThat(customerResponseDTO.getAddresses()).isNotEmpty();
    }

    @Test
    void deveBuscarClientesSemFiltroAplicado() {
        Pageable pageable = mock(Pageable.class);

        Page<Customer> customerPageMock = new PageImpl<>(List.of(new Customer(), new Customer()), pageable, 2);

        when(repository.findAllByOrderByNameAsc(any())).thenReturn(customerPageMock);

        Page<CustomerResponseDTO> responseDTOPageable = service.findAll(null, null, null, null, null, pageable);

        assertThat(responseDTOPageable).isNotEmpty();
    }

    @Test
    void deveBuscarClientesComFiltroAplicado() {
        String gender = "M";

        Pageable pageable = mock(Pageable.class);

        Page<Customer> customerPageMock = new PageImpl<>(List.of(new Customer(), new Customer()), pageable, 2);

        when(repository.findByNameContainsAndEmailContainsAndGenderContainsAndAddresses_CityContainsAndAddresses_StateContains(
                any(),any(),any(),any(),any(),any()
        )).thenReturn(customerPageMock);

        Page<CustomerResponseDTO> responseDTOPageable = service.findAll(null, null, gender, null, null, pageable);

        assertThat(responseDTOPageable).isNotEmpty();
    }

    @Test
    void deveBuscarClientePeloId() {
        var customer = new Customer();
        customer.setName("nome");

        when(repository.findById(any())).thenReturn(Optional.of(customer));

        CustomerResponseDTO responseDTO = service.findById(1L);

        assertThat(responseDTO).isNotNull();
        assertEquals("nome", responseDTO.getName());
    }

    @Test
    void deveBuscarClientePeloIdEVoltarException() {
        assertThrows(EntityNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void deveRemoverCliente() {
        var customer = new Customer();
        customer.setName("nome");

        when(repository.findById(any())).thenReturn(Optional.of(customer));

        service.removeCustomer(1L);
    }

    @Test
    void deveRemoverClienteNaoExistente() {
        assertThrows(EntityNotFoundException.class, () -> service.removeCustomer(1L));
    }

    @Test
    void deveAtualizarNomeCliente() {
        var updatedCustomerDto = UpdateCustomerDTO.builder()
                .name("nome novo")
                .build();
        var customer = new Customer();
        customer.setName("nome");
        customer.setGender("M");
        customer.setEmail("123@gmail.com");

        when(repository.findById(any())).thenReturn(Optional.of(customer));

        var updated = service.patch(updatedCustomerDto, 1L);

        assertEquals("nome novo", updated.getName());
    }

    @Test
    void deveAtualizarEmailCliente() {
        var updatedCustomerDto = UpdateCustomerDTO.builder()
                .email("novo@gmail.com")
                .build();
        var customer = new Customer();
        customer.setName("nome");
        customer.setGender("M");
        customer.setEmail("123@gmail.com");

        when(repository.findById(any())).thenReturn(Optional.of(customer));

        var updated = service.patch(updatedCustomerDto, 1L);

        assertEquals("novo@gmail.com", updated.getEmail());
    }

    @Test
    void deveAtualizarEnderecoCliente() {
        var updatedCustomerDto = UpdateCustomerDTO.builder()
                .zipCode("73000000")
                .build();
        var customer = new Customer();
        customer.setName("nome");
        customer.setGender("M");
        customer.setEmail("123@gmail.com");
        customer.setId(1L);

        when(repository.findById(any())).thenReturn(Optional.of(customer));
        when(addressService.getAddressFromZipCode(any())).thenReturn(AddressResponseDTO.builder().zipCode("74000000").build());

        var updated = service.patch(updatedCustomerDto, 1L);

        assertThat(updated.getAddresses()).isNotEmpty();
    }

    @Test
    void deveAtualizarTodosDadosCliente() {
        var updatedCustomerDto = UpdateCustomerDTO.builder()
                .zipCode("73000000")
                .email("novo@gmail.com")
                .name("nome novo")
                .build();
        var customer = new Customer();
        customer.setName("nome");
        customer.setGender("M");
        customer.setEmail("123@gmail.com");
        customer.setId(1L);

        when(repository.findById(any())).thenReturn(Optional.of(customer));
        when(addressService.getAddressFromZipCode(any())).thenReturn(AddressResponseDTO.builder().zipCode("74000000").build());

        var updated = service.put(updatedCustomerDto, 1L);

        assertThat(updated.getAddresses()).isNotEmpty();
        assertEquals("novo@gmail.com", updated.getEmail());
        assertEquals("nome novo", updated.getName());
    }
}

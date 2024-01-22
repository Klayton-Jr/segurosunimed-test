package com.example.api.service;

import com.example.api.domain.Address;
import com.example.api.domain.dto.CreateCustomerDTO;
import com.example.api.domain.dto.CustomerResponseDTO;
import com.example.api.domain.dto.UpdateCustomerDTO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.api.domain.Customer;
import com.example.api.repository.CustomerRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class CustomerService {

	private final CustomerRepository repository;
	private final ModelMapper mapper;
	private final AddressService addressService;

	public Page<CustomerResponseDTO> findAll(String name, String email, String gender, String city, String state, Pageable pageable) {
		Page<Customer> customerPage;
		if (name == null && email == null && gender == null && state == null && city == null) {
			customerPage = repository.findAllByOrderByNameAsc(pageable);
		} else {
			customerPage = repository.findByNameContainsAndEmailContainsAndGenderContainsAndAddresses_CityContainsAndAddresses_StateContains(
					returnEmptyIfNull(name),
					returnEmptyIfNull(email),
					returnEmptyIfNull(gender),
					returnEmptyIfNull(city),
					returnEmptyIfNull(state),
					pageable);
		}
		return customerPage.map(customer -> mapper.map(customer, CustomerResponseDTO.class));
	}

	public CustomerResponseDTO findById(Long id) {
		return mapper.map(findEntityById(id), CustomerResponseDTO.class);
	}

	private Customer findEntityById(Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
	}

	@Transactional
    public CustomerResponseDTO create(CreateCustomerDTO createCustomerDTO) {
		var newCustomerEntity = mapper.map(createCustomerDTO, Customer.class);

		var createdCustomerEntity = repository.save(newCustomerEntity);

		if (createCustomerDTO.getZipCode() != null) {
			addAddressForCustomer(createdCustomerEntity, createCustomerDTO.getZipCode());
		}

		return mapper.map(createdCustomerEntity, CustomerResponseDTO.class);
    }

	public void removeCustomer(Long id) {
		findEntityById(id);
		repository.deleteById(id);
	}

	@Transactional
	public CustomerResponseDTO patch(UpdateCustomerDTO updateCustomerDTO, Long id) {
		var customer = findEntityById(id);

		if (checkFieldToChange(customer.getName(), updateCustomerDTO.getName()))
			customer.setName(updateCustomerDTO.getName());
		if (checkFieldToChange(customer.getEmail(), updateCustomerDTO.getEmail()))
			customer.setEmail(updateCustomerDTO.getEmail());
		if (!returnEmptyIfNull(updateCustomerDTO.getZipCode()).isBlank())
			addAddressForCustomer(customer, updateCustomerDTO.getZipCode());

		return mapper.map(customer, CustomerResponseDTO.class);
	}

	@Transactional
	public CustomerResponseDTO put(UpdateCustomerDTO updateCustomerDTO, Long id) {
		var customer = findEntityById(id);

		customer.setName(updateCustomerDTO.getName());
		customer.setEmail(updateCustomerDTO.getEmail());
		addAddressForCustomer(customer, updateCustomerDTO.getZipCode());

		return mapper.map(customer, CustomerResponseDTO.class);
	}

	private void addAddressForCustomer(Customer customer, String zipCode) {
		var addressEntity = mapper.map(addressService.getAddressFromZipCode(zipCode), Address.class);
		addressEntity.setCustomer(customer);
		customer.getAddresses().add(addressEntity);
	}

	private boolean checkFieldToChange(String entityField, String dtoField) {
		return !entityField.equals(dtoField) && !returnEmptyIfNull(dtoField).isBlank();
	}

	private String returnEmptyIfNull(String field) {
		return field == null ? "" : field;
	}
}

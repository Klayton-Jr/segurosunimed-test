package com.example.api.web.rest;


import com.example.api.domain.dto.CreateCustomerDTO;
import com.example.api.domain.dto.CustomerResponseDTO;
import com.example.api.domain.dto.UpdateCustomerDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.api.service.CustomerService;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/customers")
@AllArgsConstructor
public class CustomerController {

	private final CustomerService service;

	@GetMapping
	@ApiOperation(
			value = "Listar todos clientes",
			responseContainer = "List",
			response = CustomerResponseDTO.class
	)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Lista de cliente retornada com sucesso")
	})
	public ResponseEntity<Page<CustomerResponseDTO>> findAll(@RequestParam(required = false) String name,
															 @RequestParam(required = false) String email,
															 @RequestParam(required = false) String gender,
															 @RequestParam(required = false) String city,
															 @RequestParam(required = false) String state,
															 @RequestParam(defaultValue = "0") int page,
															 @RequestParam(defaultValue = "20") int size) {
		return ResponseEntity.ok(service.findAll(name, email, gender, city, state, PageRequest.of(page, size)));
	}

	@GetMapping("/{id}")
	@ApiOperation(
			value = "Detalha dados cliente",
			response = CustomerResponseDTO.class
	)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Cliente retornado com sucesso"),
			@ApiResponse(code = 404, message = "Cliente não encontrado")
	})
	public ResponseEntity<CustomerResponseDTO> findById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findById(id));
	}

	@PostMapping
	@ApiOperation(
			value = "Criar um novo cliente",
			response = CustomerResponseDTO.class
	)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Cliente criado com sucesso"),
			@ApiResponse(code = 400, message = "Dados inválidos")
	})
	public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CreateCustomerDTO createCustomerDTO, UriComponentsBuilder uriComponentsBuilder) {
		var newCustomer = service.create(createCustomerDTO);
		var uri = uriComponentsBuilder.path("/customers/{id}").buildAndExpand(newCustomer.getId()).toUri();
		return ResponseEntity.created(uri).body(newCustomer);
	}

	@DeleteMapping("/{id}")
	@Transactional
	@ApiOperation(
			value = "Remove cliente"
	)
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "Cliente removido com sucesso"),
			@ApiResponse(code = 404, message = "Cliente não encontrado")
	})
	public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
		service.removeCustomer(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}")
	@ApiOperation(
			value = "Atualiza dados parciais de um cliente",
			response = CustomerResponseDTO.class
	)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Cliente atualizado com sucesso"),
			@ApiResponse(code = 400, message = "Dados inválidos"),
			@ApiResponse(code = 404, message = "Cliente não encontrado"),
	})
	public ResponseEntity<CustomerResponseDTO> updatePartialCustomer(@Valid @RequestBody UpdateCustomerDTO updateCustomerDTO,
															  @PathVariable Long id) {
		var updatedCustomer = service.patch(updateCustomerDTO, id);
		return ResponseEntity.ok().body(updatedCustomer);
	}

	@PutMapping("/{id}")
	@ApiOperation(
			value = "Atualiza todos os dados de um cliente",
			response = CustomerResponseDTO.class
	)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Cliente atualizado com sucesso"),
			@ApiResponse(code = 400, message = "Dados inválidos"),
			@ApiResponse(code = 404, message = "Cliente não encontrado"),
	})
	public ResponseEntity<CustomerResponseDTO> updateCompleteCustomer(@Valid @RequestBody UpdateCustomerDTO updateCustomerDTO,
																	 @PathVariable Long id) {
		var updatedCustomer = service.put(updateCustomerDTO, id);
		return ResponseEntity.ok().body(updatedCustomer);
	}

}

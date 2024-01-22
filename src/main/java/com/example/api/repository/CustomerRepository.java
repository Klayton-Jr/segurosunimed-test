package com.example.api.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.example.api.domain.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

	Page<Customer> findAllByOrderByNameAsc(Pageable pageable);
	Page<Customer> findByNameContainsAndEmailContainsAndGenderContainsAndAddresses_CityContainsAndAddresses_StateContains(
			String name, String email, String gender, String city, String state, Pageable pageable);
}

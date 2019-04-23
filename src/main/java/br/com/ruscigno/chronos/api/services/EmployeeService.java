package br.com.ruscigno.chronos.api.services;

import java.util.Optional;

import br.com.ruscigno.chronos.api.entities.Employee;

public interface EmployeeService {
	
	/**
	 * Persiste um funcionário na base de dados.
	 * 
	 * @param employee
	 * @return Employee
	 */
	Employee persistir(Employee employee);
	
	/**
	 * Busca e retorna um funcionário dado um CPF.
	 * 
	 * @param cpf
	 * @return Optional<Employee>
	 */
	Optional<Employee> buscarPorCpf(String cpf);
	
	/**
	 * Busca e retorna um funcionário dado um email.
	 * 
	 * @param email
	 * @return Optional<Employee>
	 */
	Optional<Employee> buscarPorEmail(String email);
	
	/**
	 * Busca e retorna um funcionário por ID.
	 * 
	 * @param id
	 * @return Optional<Employee>
	 */
	Optional<Employee> buscarPorId(Long id);

}

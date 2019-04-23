package br.com.ruscigno.chronos.api.services;

import java.util.Optional;

import br.com.ruscigno.chronos.api.entities.Company;

public interface CompanyService {

	/**
	 * Retorna uma empresa dado um CNPJ.
	 * 
	 * @param cnpj
	 * @return Optional<Company>
	 */
	Optional<Company> buscarPorCnpj(String cnpj);
	
	/**
	 * Cadastra uma nova empresa na base de dados.
	 * 
	 * @param company
	 * @return Company
	 */
	Company persistir(Company company);
	
}

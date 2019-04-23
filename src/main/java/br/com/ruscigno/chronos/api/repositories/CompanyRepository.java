package br.com.ruscigno.chronos.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ruscigno.chronos.api.entities.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
	
	@Transactional(readOnly = true)
	Company findByCnpj(String cnpj);

}

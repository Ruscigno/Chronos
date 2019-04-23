package br.com.ruscigno.chronos.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ruscigno.chronos.api.entities.Company;
import br.com.ruscigno.chronos.api.repositories.CompanyRepository;
import br.com.ruscigno.chronos.api.services.CompanyService;

@Service
public class CompanyServiceImpl implements CompanyService {

	private static final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

	@Autowired
	private CompanyRepository companyRepository;

	@Override
	public Optional<Company> buscarPorCnpj(String cnpj) {
		log.info("Buscando uma empresa para o CNPJ {}", cnpj);
		return Optional.ofNullable(companyRepository.findByCnpj(cnpj));
	}

	@Override
	public Company persistir(Company company) {
		log.info("Persistindo empresa: {}", company);
		return this.companyRepository.save(company);
	}

}

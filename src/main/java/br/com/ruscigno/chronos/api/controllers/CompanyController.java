package br.com.ruscigno.chronos.api.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ruscigno.chronos.api.dtos.CompanyDto;
import br.com.ruscigno.chronos.api.entities.Company;
import br.com.ruscigno.chronos.api.response.Response;
import br.com.ruscigno.chronos.api.services.CompanyService;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class CompanyController {

	private static final Logger log = LoggerFactory.getLogger(CompanyController.class);

	@Autowired
	private CompanyService companyService;

	public CompanyController() {
	}

	/**
	 * Retorna uma empresa dado um CNPJ.
	 * 
	 * @param cnpj
	 * @return ResponseEntity<Response<CompanyDto>>
	 */
	@GetMapping(value = "/cnpj/{cnpj}")
	public ResponseEntity<Response<CompanyDto>> buscarPorCnpj(@PathVariable("cnpj") String cnpj) {
		log.info("Buscando empresa por CNPJ: {}", cnpj);
		Response<CompanyDto> response = new Response<CompanyDto>();
		Optional<Company> company = companyService.buscarPorCnpj(cnpj);

		if (!company.isPresent()) {
			log.info("Company não encontrada para o CNPJ: {}", cnpj);
			response.getErrors().add("Company não encontrada para o CNPJ " + cnpj);
			return ResponseEntity.badRequest().body(response);
		}

		response.setData(this.converterEmpresaDto(company.get()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Popula um DTO com os dados de uma empresa.
	 * 
	 * @param company
	 * @return CompanyDto
	 */
	private CompanyDto converterEmpresaDto(Company company) {
		CompanyDto companyDto = new CompanyDto();
		companyDto.setId(company.getId());
		companyDto.setCnpj(company.getCnpj());
		companyDto.setRazaoSocial(company.getRazaoSocial());

		return companyDto;
	}

}

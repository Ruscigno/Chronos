package br.com.ruscigno.chronos.api.controllers;

import java.security.NoSuchAlgorithmException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ruscigno.chronos.api.dtos.CompanyRegisterDto;
import br.com.ruscigno.chronos.api.entities.Company;
import br.com.ruscigno.chronos.api.entities.Employee;
import br.com.ruscigno.chronos.api.enums.ProfileEnum;
import br.com.ruscigno.chronos.api.response.Response;
import br.com.ruscigno.chronos.api.services.CompanyService;
import br.com.ruscigno.chronos.api.services.EmployeeService;
import br.com.ruscigno.chronos.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")
public class CompanyRegisterController {

	private static final Logger log = LoggerFactory.getLogger(CompanyRegisterController.class);

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private CompanyService companyService;

	public CompanyRegisterController() {
	}

	/**
	 * Cadastra uma pessoa jurídica no sistema.
	 * 
	 * @param companyRegisterDto
	 * @param result
	 * @return ResponseEntity<Response<CompanyRegisterDto>>
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<CompanyRegisterDto>> cadastrar(@Valid @RequestBody CompanyRegisterDto companyRegisterDto,
			BindingResult result) throws NoSuchAlgorithmException {
		log.info("Cadastrando PJ: {}", companyRegisterDto.toString());
		Response<CompanyRegisterDto> response = new Response<CompanyRegisterDto>();

		validarDadosExistentes(companyRegisterDto, result);
		Company company = this.converterDtoParaEmpresa(companyRegisterDto);
		Employee employee = this.converterDtoParaFuncionario(companyRegisterDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro PJ: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		this.companyService.persistir(company);
		employee.setEmpresa(company);
		this.employeeService.persistir(employee);

		response.setData(this.converterCadastroPJDto(employee));
		return ResponseEntity.ok(response);
	}

	/**
	 * Verifica se a empresa ou funcionário já existem na base de dados.
	 * 
	 * @param companyRegisterDto
	 * @param result
	 */
	private void validarDadosExistentes(CompanyRegisterDto companyRegisterDto, BindingResult result) {
		this.companyService.buscarPorCnpj(companyRegisterDto.getCnpj())
				.ifPresent(emp -> result.addError(new ObjectError("empresa", "Company já existente.")));

		this.employeeService.buscarPorCpf(companyRegisterDto.getCpf())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente.")));

		this.employeeService.buscarPorEmail(companyRegisterDto.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente.")));
	}

	/**
	 * Converte os dados do DTO para empresa.
	 * 
	 * @param companyRegisterDto
	 * @return Company
	 */
	private Company converterDtoParaEmpresa(CompanyRegisterDto companyRegisterDto) {
		Company company = new Company();
		company.setCnpj(companyRegisterDto.getCnpj());
		company.setRazaoSocial(companyRegisterDto.getRazaoSocial());

		return company;
	}

	/**
	 * Converte os dados do DTO para funcionário.
	 * 
	 * @param companyRegisterDto
	 * @param result
	 * @return Employee
	 * @throws NoSuchAlgorithmException
	 */
	private Employee converterDtoParaFuncionario(CompanyRegisterDto companyRegisterDto, BindingResult result)
			throws NoSuchAlgorithmException {
		Employee employee = new Employee();
		employee.setNome(companyRegisterDto.getNome());
		employee.setEmail(companyRegisterDto.getEmail());
		employee.setCpf(companyRegisterDto.getCpf());
		employee.setPerfil(ProfileEnum.ROLE_ADMIN);
		employee.setSenha(PasswordUtils.gerarBCrypt(companyRegisterDto.getSenha()));

		return employee;
	}

	/**
	 * Popula o DTO de cadastro com os dados do funcionário e empresa.
	 * 
	 * @param employee
	 * @return CompanyRegisterDto
	 */
	private CompanyRegisterDto converterCadastroPJDto(Employee employee) {
		CompanyRegisterDto companyRegisterDto = new CompanyRegisterDto();
		companyRegisterDto.setId(employee.getId());
		companyRegisterDto.setNome(employee.getNome());
		companyRegisterDto.setEmail(employee.getEmail());
		companyRegisterDto.setCpf(employee.getCpf());
		companyRegisterDto.setRazaoSocial(employee.getEmpresa().getRazaoSocial());
		companyRegisterDto.setCnpj(employee.getEmpresa().getCnpj());

		return companyRegisterDto;
	}

}

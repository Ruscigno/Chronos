package br.com.ruscigno.chronos.api.controllers;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

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

import br.com.ruscigno.chronos.api.dtos.IndividualRegisterDto;
import br.com.ruscigno.chronos.api.entities.Company;
import br.com.ruscigno.chronos.api.entities.Employee;
import br.com.ruscigno.chronos.api.enums.ProfileEnum;
import br.com.ruscigno.chronos.api.response.Response;
import br.com.ruscigno.chronos.api.services.CompanyService;
import br.com.ruscigno.chronos.api.services.EmployeeService;
import br.com.ruscigno.chronos.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/cadastrar-pf")
@CrossOrigin(origins = "*")
public class IndividualRegisterController {

	private static final Logger log = LoggerFactory.getLogger(IndividualRegisterController.class);
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private EmployeeService employeeService;

	public IndividualRegisterController() {
	}

	/**
	 * Cadastra um funcionário pessoa física no sistema.
	 * 
	 * @param individualRegisterDto
	 * @param result
	 * @return ResponseEntity<Response<IndividualRegisterDto>>
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<IndividualRegisterDto>> cadastrar(@Valid @RequestBody IndividualRegisterDto individualRegisterDto,
			BindingResult result) throws NoSuchAlgorithmException {
		log.info("Cadastrando PF: {}", individualRegisterDto.toString());
		Response<IndividualRegisterDto> response = new Response<IndividualRegisterDto>();

		validarDadosExistentes(individualRegisterDto, result);
		Employee employee = this.converterDtoParaFuncionario(individualRegisterDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro PF: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Optional<Company> company = this.companyService.buscarPorCnpj(individualRegisterDto.getCnpj());
		company.ifPresent(emp -> employee.setEmpresa(emp));
		this.employeeService.persistir(employee);

		response.setData(this.converterCadastroPFDto(employee));
		return ResponseEntity.ok(response);
	}

	/**
	 * Verifica se a empresa está cadastrada e se o funcionário não existe na base de dados.
	 * 
	 * @param individualRegisterDto
	 * @param result
	 */
	private void validarDadosExistentes(IndividualRegisterDto individualRegisterDto, BindingResult result) {
		Optional<Company> company = this.companyService.buscarPorCnpj(individualRegisterDto.getCnpj());
		if (!company.isPresent()) {
			result.addError(new ObjectError("empresa", "Company não cadastrada."));
		}
		
		this.employeeService.buscarPorCpf(individualRegisterDto.getCpf())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente.")));

		this.employeeService.buscarPorEmail(individualRegisterDto.getEmail())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente.")));
	}

	/**
	 * Converte os dados do DTO para funcionário.
	 * 
	 * @param individualRegisterDto
	 * @param result
	 * @return Employee
	 * @throws NoSuchAlgorithmException
	 */
	private Employee converterDtoParaFuncionario(IndividualRegisterDto individualRegisterDto, BindingResult result)
			throws NoSuchAlgorithmException {
		Employee employee = new Employee();
		employee.setNome(individualRegisterDto.getNome());
		employee.setEmail(individualRegisterDto.getEmail());
		employee.setCpf(individualRegisterDto.getCpf());
		employee.setPerfil(ProfileEnum.ROLE_USUARIO);
		employee.setSenha(PasswordUtils.gerarBCrypt(individualRegisterDto.getSenha()));
		individualRegisterDto.getQtdHorasAlmoco()
				.ifPresent(qtdHorasAlmoco -> employee.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
		individualRegisterDto.getQtdHorasTrabalhoDia()
				.ifPresent(qtdHorasTrabDia -> employee.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabDia)));
		individualRegisterDto.getValorHora().ifPresent(valorHora -> employee.setValorHora(new BigDecimal(valorHora)));

		return employee;
	}

	/**
	 * Popula o DTO de cadastro com os dados do funcionário e empresa.
	 * 
	 * @param employee
	 * @return IndividualRegisterDto
	 */
	private IndividualRegisterDto converterCadastroPFDto(Employee employee) {
		IndividualRegisterDto individualRegisterDto = new IndividualRegisterDto();
		individualRegisterDto.setId(employee.getId());
		individualRegisterDto.setNome(employee.getNome());
		individualRegisterDto.setEmail(employee.getEmail());
		individualRegisterDto.setCpf(employee.getCpf());
		individualRegisterDto.setCnpj(employee.getEmpresa().getCnpj());
		employee.getQtdHorasAlmocoOpt().ifPresent(qtdHorasAlmoco -> individualRegisterDto
				.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
		employee.getQtdHorasTrabalhoDiaOpt().ifPresent(
				qtdHorasTrabDia -> individualRegisterDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabDia))));
		employee.getValorHoraOpt()
				.ifPresent(valorHora -> individualRegisterDto.setValorHora(Optional.of(valorHora.toString())));

		return individualRegisterDto;
	}

}

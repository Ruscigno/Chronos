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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ruscigno.chronos.api.dtos.EmployeeDto;
import br.com.ruscigno.chronos.api.entities.Employee;
import br.com.ruscigno.chronos.api.response.Response;
import br.com.ruscigno.chronos.api.services.EmployeeService;
import br.com.ruscigno.chronos.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class EmployeeController {

	private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

	@Autowired
	private EmployeeService employeeService;

	public EmployeeController() {
	}

	/**
	 * Atualiza os dados de um funcionário.
	 * 
	 * @param id
	 * @param employeeDto
	 * @param result
	 * @return ResponseEntity<Response<EmployeeDto>>
	 * @throws NoSuchAlgorithmException
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<EmployeeDto>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody EmployeeDto employeeDto, BindingResult result) throws NoSuchAlgorithmException {
		log.info("Atualizando funcionário: {}", employeeDto.toString());
		Response<EmployeeDto> response = new Response<EmployeeDto>();

		Optional<Employee> employee = this.employeeService.buscarPorId(id);
		if (!employee.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionário não encontrado."));
		}

		this.atualizarDadosFuncionario(employee.get(), employeeDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando funcionário: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		this.employeeService.persistir(employee.get());
		response.setData(this.converterFuncionarioDto(employee.get()));

		return ResponseEntity.ok(response);
	}

	/**
	 * Atualiza os dados do funcionário com base nos dados encontrados no DTO.
	 * 
	 * @param employee
	 * @param employeeDto
	 * @param result
	 * @throws NoSuchAlgorithmException
	 */
	private void atualizarDadosFuncionario(Employee employee, EmployeeDto employeeDto, BindingResult result)
			throws NoSuchAlgorithmException {
		employee.setNome(employeeDto.getNome());

		if (!employee.getEmail().equals(employeeDto.getEmail())) {
			this.employeeService.buscarPorEmail(employeeDto.getEmail())
					.ifPresent(func -> result.addError(new ObjectError("email", "Email já existente.")));
			employee.setEmail(employeeDto.getEmail());
		}

		employee.setQtdHorasAlmoco(null);
		employeeDto.getQtdHorasAlmoco()
				.ifPresent(qtdHorasAlmoco -> employee.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));

		employee.setQtdHorasTrabalhoDia(null);
		employeeDto.getQtdHorasTrabalhoDia()
				.ifPresent(qtdHorasTrabDia -> employee.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabDia)));

		employee.setValorHora(null);
		employeeDto.getValorHora().ifPresent(valorHora -> employee.setValorHora(new BigDecimal(valorHora)));

		if (employeeDto.getSenha().isPresent()) {
			employee.setSenha(PasswordUtils.gerarBCrypt(employeeDto.getSenha().get()));
		}
	}

	/**
	 * Retorna um DTO com os dados de um funcionário.
	 * 
	 * @param employee
	 * @return EmployeeDto
	 */
	private EmployeeDto converterFuncionarioDto(Employee employee) {
		EmployeeDto employeeDto = new EmployeeDto();
		employeeDto.setId(employee.getId());
		employeeDto.setEmail(employee.getEmail());
		employeeDto.setNome(employee.getNome());
		employee.getQtdHorasAlmocoOpt().ifPresent(
				qtdHorasAlmoco -> employeeDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
		employee.getQtdHorasTrabalhoDiaOpt().ifPresent(
				qtdHorasTrabDia -> employeeDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabDia))));
		employee.getValorHoraOpt()
				.ifPresent(valorHora -> employeeDto.setValorHora(Optional.of(valorHora.toString())));

		return employeeDto;
	}

}

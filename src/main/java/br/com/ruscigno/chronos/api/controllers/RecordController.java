package br.com.ruscigno.chronos.api.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.validation.Valid;

//import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ruscigno.chronos.api.dtos.EntryDto;
import br.com.ruscigno.chronos.api.entities.Employee;
import br.com.ruscigno.chronos.api.entities.Entry;
import br.com.ruscigno.chronos.api.enums.TypeEnum;
import br.com.ruscigno.chronos.api.response.Response;
import br.com.ruscigno.chronos.api.services.EmployeeService;
import br.com.ruscigno.chronos.api.services.EntryService;

@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin(origins = "*")
public class RecordController {

	private static final Logger log = LoggerFactory.getLogger(RecordController.class);
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private EntryService entryService;

	@Autowired
	private EmployeeService employeeService;
	
	@Value("${paginacao.qtd_por_pagina}")
	private int qtdPorPagina;

	public RecordController() {
	}

	/**
	 * Retorna a listagem de lançamentos de um funcionário.
	 * 
	 * @param funcionarioId
	 * @return ResponseEntity<Response<EntryDto>>
	 */
	@GetMapping(value = "/funcionario/{funcionarioId}")
	public ResponseEntity<Response<Page<EntryDto>>> listarPorFuncionarioId(
			@PathVariable("funcionarioId") Long funcionarioId,
			@RequestParam(value = "pag", defaultValue = "0") int pag,
			@RequestParam(value = "ord", defaultValue = "id") String ord,
			@RequestParam(value = "dir", defaultValue = "DESC") String dir) {
		log.info("Buscando lançamentos por ID do funcionário: {}, página: {}", funcionarioId, pag);
		Response<Page<EntryDto>> response = new Response<Page<EntryDto>>();

		PageRequest pageRequest = new PageRequest(pag, this.qtdPorPagina, Direction.valueOf(dir), ord);
		Page<Entry> entries = this.entryService.buscarPorFuncionarioId(funcionarioId, pageRequest);
		Page<EntryDto> lancamentosDto = entries.map(lancamento -> this.converterLancamentoDto(lancamento));

		response.setData(lancamentosDto);
		return ResponseEntity.ok(response);
	}

	/**
	 * Retorna um lançamento por ID.
	 * 
	 * @param id
	 * @return ResponseEntity<Response<EntryDto>>
	 */
	@GetMapping(value = "/{id}")
	public ResponseEntity<Response<EntryDto>> listarPorId(@PathVariable("id") Long id) {
		log.info("Buscando lançamento por ID: {}", id);
		Response<EntryDto> response = new Response<EntryDto>();
		Optional<Entry> entry = this.entryService.buscarPorId(id);

		if (!entry.isPresent()) {
			log.info("Lançamento não encontrado para o ID: {}", id);
			response.getErrors().add("Lançamento não encontrado para o id " + id);
			return ResponseEntity.badRequest().body(response);
		}

		response.setData(this.converterLancamentoDto(entry.get()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Adiciona um novo lançamento.
	 * 
	 * @param lancamento
	 * @param result
	 * @return ResponseEntity<Response<EntryDto>>
	 * @throws ParseException 
	 */
	@PostMapping
	public ResponseEntity<Response<EntryDto>> adicionar(@Valid @RequestBody EntryDto entryDto,
			BindingResult result) throws ParseException {
		log.info("Adicionando lançamento: {}", entryDto.toString());
		Response<EntryDto> response = new Response<EntryDto>();
		validarFuncionario(entryDto, result);
		Entry entry = this.converterDtoParaLancamento(entryDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando lançamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		entry = this.entryService.persistir(entry);
		response.setData(this.converterLancamentoDto(entry));
		return ResponseEntity.ok(response);
	}

	/**
	 * Atualiza os dados de um lançamento.
	 * 
	 * @param id
	 * @param entryDto
	 * @return ResponseEntity<Response<Entry>>
	 * @throws ParseException 
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<EntryDto>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody EntryDto entryDto, BindingResult result) throws ParseException {
		log.info("Atualizando lançamento: {}", entryDto.toString());
		Response<EntryDto> response = new Response<EntryDto>();
		validarFuncionario(entryDto, result);
		entryDto.setId(Optional.of(id));
		Entry entry = this.converterDtoParaLancamento(entryDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando lançamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		entry = this.entryService.persistir(entry);
		response.setData(this.converterLancamentoDto(entry));
		return ResponseEntity.ok(response);
	}

	/**
	 * Remove um lançamento por ID.
	 * 
	 * @param id
	 * @return ResponseEntity<Response<Entry>>
	 */
	@DeleteMapping(value = "/{id}")
	//@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<String>> remover(@PathVariable("id") Long id) {
		log.info("Removendo lançamento: {}", id);
		Response<String> response = new Response<String>();
		Optional<Entry> entry = this.entryService.buscarPorId(id);

		if (!entry.isPresent()) {
			log.info("Erro ao remover devido ao lançamento ID: {} ser inválido.", id);
			response.getErrors().add("Erro ao remover lançamento. Registro não encontrado para o id " + id);
			return ResponseEntity.badRequest().body(response);
		}

		this.entryService.remover(id);
		return ResponseEntity.ok(new Response<String>());
	}

	/**
	 * Valida um funcionário, verificando se ele é existente e válido no
	 * sistema.
	 * 
	 * @param entryDto
	 * @param result
	 */
	private void validarFuncionario(EntryDto entryDto, BindingResult result) {
		if (entryDto.getFuncionarioId() == null) {
			result.addError(new ObjectError("funcionario", "Funcionário não informado."));
			return;
		}

		log.info("Validando funcionário id {}: ", entryDto.getFuncionarioId());
		Optional<Employee> employee = this.employeeService.buscarPorId(entryDto.getFuncionarioId());
		if (!employee.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionário não encontrado. ID inexistente."));
		}
	}

	/**
	 * Converte uma entidade lançamento para seu respectivo DTO.
	 * 
	 * @param entry
	 * @return EntryDto
	 */
	private EntryDto converterLancamentoDto(Entry entry) {
		EntryDto entryDto = new EntryDto();
		entryDto.setId(Optional.of(entry.getId()));
		entryDto.setData(this.dateFormat.format(entry.getData()));
		entryDto.setTipo(entry.getTipo().toString());
		entryDto.setDescricao(entry.getDescricao());
		entryDto.setLocalizacao(entry.getLocalizacao());
		entryDto.setFuncionarioId(entry.getFuncionario().getId());

		return entryDto;
	}

	/**
	 * Converte um EntryDto para uma entidade Entry.
	 * 
	 * @param entryDto
	 * @param result
	 * @return Entry
	 * @throws ParseException 
	 */
	private Entry converterDtoParaLancamento(EntryDto entryDto, BindingResult result) throws ParseException {
		Entry entry = new Entry();

		if (entryDto.getId().isPresent()) {
			Optional<Entry> lanc = this.entryService.buscarPorId(entryDto.getId().get());
			if (lanc.isPresent()) {
				entry = lanc.get();
			} else {
				result.addError(new ObjectError("lancamento", "Lançamento não encontrado."));
			}
		} else {
			entry.setFuncionario(new Employee());
			entry.getFuncionario().setId(entryDto.getFuncionarioId());
		}

		entry.setDescricao(entryDto.getDescricao());
		entry.setLocalizacao(entryDto.getLocalizacao());
		entry.setData(this.dateFormat.parse(entryDto.getData()));

//		if (EnumUtils.isValidEnum(TypeEnum.class, lancamentoDto.getTipo())) {
			entry.setTipo(TypeEnum.valueOf(entryDto.getTipo()));
//		} else {
//			result.addError(new ObjectError("tipo", "Tipo inválido."));
//		}

		return entry;
	}

}

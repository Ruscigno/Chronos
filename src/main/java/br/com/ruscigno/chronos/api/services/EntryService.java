package br.com.ruscigno.chronos.api.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import br.com.ruscigno.chronos.api.entities.Entry;

public interface EntryService {

	/**
	 * Retorna uma lista paginada de lançamentos de um determinado funcionário.
	 * 
	 * @param funcionarioId
	 * @param pageRequest
	 * @return Page<Entry>
	 */
	Page<Entry> buscarPorFuncionarioId(Long funcionarioId, PageRequest pageRequest);
	
	/**
	 * Retorna um lançamento por ID.
	 * 
	 * @param id
	 * @return Optional<Entry>
	 */
	Optional<Entry> buscarPorId(Long id);
	
	/**
	 * Persiste um lançamento na base de dados.
	 * 
	 * @param entry
	 * @return Entry
	 */
	Entry persistir(Entry entry);
	
	/**
	 * Remove um lançamento da base de dados.
	 * 
	 * @param id
	 */
	void remover(Long id);
	
}

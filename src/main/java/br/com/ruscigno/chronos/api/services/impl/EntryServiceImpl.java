package br.com.ruscigno.chronos.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import br.com.ruscigno.chronos.api.entities.Entry;
import br.com.ruscigno.chronos.api.repositories.EntryRepository;
import br.com.ruscigno.chronos.api.services.EntryService;

@Service
public class EntryServiceImpl implements EntryService {

	private static final Logger log = LoggerFactory.getLogger(EntryServiceImpl.class);

	@Autowired
	private EntryRepository entryRepository;

	public Page<Entry> buscarPorFuncionarioId(Long funcionarioId, PageRequest pageRequest) {
		log.info("Buscando lançamentos para o funcionário ID {}", funcionarioId);
		return this.entryRepository.findByFuncionarioId(funcionarioId, pageRequest);
	}
	
	@Cacheable("lancamentoPorId")
	public Optional<Entry> buscarPorId(Long id) {
		log.info("Buscando um lançamento pelo ID {}", id);
		return this.entryRepository.findById(id);
	}
	
	@CachePut("lancamentoPorId")
	public Entry persistir(Entry entry) {
		log.info("Persistindo o lançamento: {}", entry);
		return this.entryRepository.save(entry);
	}
	
	public void remover(Long id) {
		log.info("Removendo o lançamento ID {}", id);
		this.entryRepository.findById(id).ifPresent(
			lancamento -> this.entryRepository.delete(lancamento));
	}

}

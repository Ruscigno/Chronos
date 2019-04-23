package br.com.ruscigno.chronos.api.repositories;

import java.util.List;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.com.ruscigno.chronos.api.entities.Entry;

@Transactional(readOnly = true)
@NamedQueries({
		@NamedQuery(name = "EntryRepository.findByFuncionarioId", 
				query = "SELECT lanc FROM Entry lanc WHERE lanc.funcionario.id = :funcionarioId") })
public interface EntryRepository extends JpaRepository<Entry, Long> {

	List<Entry> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId);

	Page<Entry> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId, Pageable pageable);
}

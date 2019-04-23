package br.com.ruscigno.chronos.api.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.ruscigno.chronos.api.entities.Entry;
import br.com.ruscigno.chronos.api.repositories.EntryRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EntryServiceTest {

	@MockBean
	private EntryRepository entryRepository;

	@Autowired
	private EntryService entryService;

	@Before
	public void setUp() throws Exception {
		BDDMockito
				.given(this.entryRepository.findByFuncionarioId(Mockito.anyLong(), Mockito.any(PageRequest.class)))
				.willReturn(new PageImpl<Entry>(new ArrayList<Entry>()));
//		BDDMockito.given(this.lancamentoRepository.findOne(Mockito.anyLong())).willReturn(new Entry());
		BDDMockito.given(this.entryRepository.save(Mockito.any(Entry.class))).willReturn(new Entry());
	}

	@Test
	public void testBuscarLancamentoPorFuncionarioId() {
		Page<Entry> entry = this.entryService.buscarPorFuncionarioId(1L, new PageRequest(0, 10));

		assertNotNull(entry);
	}

//	@Test
//	public void testBuscarLancamentoPorId() {
//		Optional<Entry> lancamento = this.lancamentoService.buscarPorId(1L);
//
//		assertTrue(lancamento.isPresent());
//	}

	@Test
	public void testPersistirLancamento() {
		Entry entry = this.entryService.persistir(new Entry());

		assertNotNull(entry);
	}

}

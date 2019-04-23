package br.com.ruscigno.chronos.api.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.ruscigno.chronos.api.dtos.EntryDto;
import br.com.ruscigno.chronos.api.entities.Employee;
import br.com.ruscigno.chronos.api.entities.Entry;
import br.com.ruscigno.chronos.api.enums.TypeEnum;
import br.com.ruscigno.chronos.api.services.EmployeeService;
import br.com.ruscigno.chronos.api.services.EntryService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EntryControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private EntryService entryService;
	
	@MockBean
	private EmployeeService employeeService;
	
	private static final String URL_BASE = "/api/lancamentos/";
	private static final Long ID_FUNCIONARIO = 1L;
	private static final Long ID_LANCAMENTO = 1L;
	private static final String TIPO = TypeEnum.INICIO_TRABALHO.name();
	private static final Date DATA = new Date();
	
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Test
//	@WithMockUser
	public void testCadastrarLancamento() throws Exception {
		Entry entry = obterDadosLancamento();
		BDDMockito.given(this.employeeService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(new Employee()));
		BDDMockito.given(this.entryService.persistir(Mockito.any(Entry.class))).willReturn(entry);

		mvc.perform(MockMvcRequestBuilders.post(URL_BASE)
				.content(this.obterJsonRequisicaoPost())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(ID_LANCAMENTO))
				.andExpect(jsonPath("$.data.tipo").value(TIPO))
				.andExpect(jsonPath("$.data.data").value(this.dateFormat.format(DATA)))
				.andExpect(jsonPath("$.data.funcionarioId").value(ID_FUNCIONARIO))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
//	@WithMockUser
	public void testCadastrarLancamentoFuncionarioIdInvalido() throws Exception {
		BDDMockito.given(this.employeeService.buscarPorId(Mockito.anyLong())).willReturn(Optional.empty());

		mvc.perform(MockMvcRequestBuilders.post(URL_BASE)
				.content(this.obterJsonRequisicaoPost())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").value("Funcionário não encontrado. ID inexistente."))
				.andExpect(jsonPath("$.data").isEmpty());
	}
	
	@Test
//	@WithMockUser(username = "admin@admin.com", roles = {"ADMIN"})
	public void testRemoverLancamento() throws Exception {
		BDDMockito.given(this.entryService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(new Entry()));

		mvc.perform(MockMvcRequestBuilders.delete(URL_BASE + ID_LANCAMENTO)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
//	@Test
//	@WithMockUser
//	public void testRemoverLancamentoAcessoNegado() throws Exception {
//		BDDMockito.given(this.lancamentoService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(new Entry()));
//
//		mvc.perform(MockMvcRequestBuilders.delete(URL_BASE + ID_LANCAMENTO)
//				.accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isForbidden());
//	}

	private String obterJsonRequisicaoPost() throws JsonProcessingException {
		EntryDto entryDto = new EntryDto();
		entryDto.setId(null);
		entryDto.setData(this.dateFormat.format(DATA));
		entryDto.setTipo(TIPO);
		entryDto.setFuncionarioId(ID_FUNCIONARIO);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(entryDto);
	}

	private Entry obterDadosLancamento() {
		Entry entry = new Entry();
		entry.setId(ID_LANCAMENTO);
		entry.setData(DATA);
		entry.setTipo(TypeEnum.valueOf(TIPO));
		entry.setFuncionario(new Employee());
		entry.getFuncionario().setId(ID_FUNCIONARIO);
		return entry;
	}	

}
package br.com.ruscigno.chronos.api.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.ruscigno.chronos.api.dtos.CompanyRegisterDto;
import br.com.ruscigno.chronos.api.entities.Company;
import br.com.ruscigno.chronos.api.services.CompanyService;
import br.com.ruscigno.chronos.api.services.EmployeeService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CompanyRegisterControllerTest {

	private static final String URL_BASE = "/api/cadastrar-pj";
	private static final Long ID = 1L;
	private static final String NOME = "Teste Company";
	private static final String EMAIL = "test@test.com";
	private static final String SENHA = "654321";
	private static final String CPF = "99126579006";
	private static final String RAZAO_SOCIAL = "Teste Company";
	private static final String CNPJ = "55638989000155";
	@Autowired
	private MockMvc mvc;

	@MockBean
	private EmployeeService employeeService;

	@MockBean
	private CompanyService companyService;

	@Test
	@WithMockUser
	public void cadastrarTest() throws Exception {
		BDDMockito.given(this.companyService.persistir(Mockito.any(Company.class))).willReturn(getCompanyData());
		
		mvc.perform(MockMvcRequestBuilders.post(URL_BASE)
			.content(this.obterJsonRequisicaoPost())
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.razaoSocial").value(RAZAO_SOCIAL))
			.andExpect(jsonPath("$.data.cnpj").value(CNPJ))
			.andExpect(jsonPath("$.errors").isEmpty());
	}

	private String obterJsonRequisicaoPost() throws JsonProcessingException {
		CompanyRegisterDto companyDto = new CompanyRegisterDto();
		companyDto.setId(ID);
		companyDto.setNome(NOME);
		companyDto.setEmail(EMAIL);
		companyDto.setSenha(SENHA);
		companyDto.setCpf(CPF);
		companyDto.setRazaoSocial(RAZAO_SOCIAL);
		companyDto.setCnpj(CNPJ);
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(companyDto);
	}
	
	private Company getCompanyData() {
		Company company = new Company();
		company.setId(ID);
		company.setRazaoSocial(RAZAO_SOCIAL);
		company.setCnpj(CNPJ);
		return company;
	}

}

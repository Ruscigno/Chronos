package br.com.ruscigno.chronos.api.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.ruscigno.chronos.api.entities.Company;
import br.com.ruscigno.chronos.api.repositories.CompanyRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CompanyServiceTest {

	@MockBean
	private CompanyRepository companyRepository;

	@Autowired
	private CompanyService companyService;

	private static final String CNPJ = "51463645000100";

	@Before
	public void setUp() throws Exception {
		BDDMockito.given(this.companyRepository.findByCnpj(Mockito.anyString())).willReturn(new Company());
		BDDMockito.given(this.companyRepository.save(Mockito.any(Company.class))).willReturn(new Company());
	}

	@Test
	public void testBuscarEmpresaPorCnpj() {
		Optional<Company> company = this.companyService.buscarPorCnpj(CNPJ);

		assertTrue(company.isPresent());
	}
	
	@Test
	public void testPersistirEmpresa() {
		Company company = this.companyService.persistir(new Company());

		assertNotNull(company);
	}

}

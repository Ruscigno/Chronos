package br.com.ruscigno.chronos.api.repositories;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.ruscigno.chronos.api.entities.Company;
import br.com.ruscigno.chronos.api.entities.Employee;
import br.com.ruscigno.chronos.api.entities.Entry;
import br.com.ruscigno.chronos.api.enums.ProfileEnum;
import br.com.ruscigno.chronos.api.enums.TypeEnum;
import br.com.ruscigno.chronos.api.utils.PasswordUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EntryRepositoryTest {
	
	@Autowired
	private EntryRepository entryRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	private Long funcionarioId;

	@Before
	public void setUp() throws Exception {
		Company company = this.companyRepository.save(obterDadosEmpresa());
		
		Employee employee = this.employeeRepository.save(obterDadosFuncionario(company));
		this.funcionarioId = employee.getId();
		
		this.entryRepository.save(obterDadosLancamentos(employee));
		this.entryRepository.save(obterDadosLancamentos(employee));
	}

	@After
	public void tearDown() throws Exception {
		this.companyRepository.deleteAll();
	}

	@Test
	public void testBuscarLancamentosPorFuncionarioId() {
		List<Entry> entries = this.entryRepository.findByFuncionarioId(funcionarioId);
		
		assertEquals(2, entries.size());
	}
	
	@Test
	public void testBuscarLancamentosPorFuncionarioIdPaginado() {
		PageRequest page = new PageRequest(0, 10);
		Page<Entry> entries = this.entryRepository.findByFuncionarioId(funcionarioId, page);
		
		assertEquals(2, entries.getTotalElements());
	}
	
	private Entry obterDadosLancamentos(Employee employee) {
		Entry lancameto = new Entry();
		lancameto.setData(new Date());
		lancameto.setTipo(TypeEnum.INICIO_ALMOCO);
		lancameto.setFuncionario(employee);
		return lancameto;
	}

	private Employee obterDadosFuncionario(Company company) throws NoSuchAlgorithmException {
		Employee employee = new Employee();
		employee.setNome("Fulano de Tal");
		employee.setPerfil(ProfileEnum.ROLE_USUARIO);
		employee.setSenha(PasswordUtils.gerarBCrypt("123456"));
		employee.setCpf("24291173474");
		employee.setEmail("email@email.com");
		employee.setEmpresa(company);
		return employee;
	}

	private Company obterDadosEmpresa() {
		Company company = new Company();
		company.setRazaoSocial("Company de exemplo");
		company.setCnpj("51463645000100");
		return company;
	}

}

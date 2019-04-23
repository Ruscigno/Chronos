package br.com.ruscigno.chronos.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.ruscigno.chronos.api.entities.Company;
import br.com.ruscigno.chronos.api.entities.Employee;
import br.com.ruscigno.chronos.api.enums.ProfileEnum;
import br.com.ruscigno.chronos.api.utils.PasswordUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmployeeRepositoryTest {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private CompanyRepository companyRepository;

	private static final String EMAIL = "email@email.com";
	private static final String CPF = "24291173474";

	@Before
	public void setUp() throws Exception {
		Company company = this.companyRepository.save(obterDadosEmpresa());
		this.employeeRepository.save(obterDadosFuncionario(company));
	}

	@After
	public final void tearDown() {
		this.companyRepository.deleteAll();
	}

	@Test
	public void testBuscarFuncionarioPorEmail() {
		Employee employee = this.employeeRepository.findByEmail(EMAIL);

		assertEquals(EMAIL, employee.getEmail());
	}

	@Test
	public void testBuscarFuncionarioPorCpf() {
		Employee employee = this.employeeRepository.findByCpf(CPF);

		assertEquals(CPF, employee.getCpf());
	}

	@Test
	public void testBuscarFuncionarioPorEmailECpf() {
		Employee employee = this.employeeRepository.findByCpfOrEmail(CPF, EMAIL);

		assertNotNull(employee);
	}

	@Test
	public void testBuscarFuncionarioPorEmailOuCpfParaEmailInvalido() {
		Employee employee = this.employeeRepository.findByCpfOrEmail(CPF, "email@invalido.com");

		assertNotNull(employee);
	}

	@Test
	public void testBuscarFuncionarioPorEmailECpfParaCpfInvalido() {
		Employee employee = this.employeeRepository.findByCpfOrEmail("12345678901", EMAIL);

		assertNotNull(employee);
	}

	private Employee obterDadosFuncionario(Company company) throws NoSuchAlgorithmException {
		Employee employee = new Employee();
		employee.setNome("Fulano de Tal");
		employee.setPerfil(ProfileEnum.ROLE_USUARIO);
		employee.setSenha(PasswordUtils.gerarBCrypt("123456"));
		employee.setCpf(CPF);
		employee.setEmail(EMAIL);
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

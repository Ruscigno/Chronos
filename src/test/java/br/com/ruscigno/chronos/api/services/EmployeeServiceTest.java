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

import br.com.ruscigno.chronos.api.entities.Employee;
import br.com.ruscigno.chronos.api.repositories.EmployeeRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmployeeServiceTest {

	@MockBean
	private EmployeeRepository employeeRepository;

	@Autowired
	private EmployeeService employeeService;

	@Before
	public void setUp() throws Exception {
		BDDMockito.given(this.employeeRepository.save(Mockito.any(Employee.class))).willReturn(new Employee());
		BDDMockito.given(this.employeeRepository.findByEmail(Mockito.anyString())).willReturn(new Employee());
		BDDMockito.given(this.employeeRepository.findByCpf(Mockito.anyString())).willReturn(new Employee());
		BDDMockito.given(this.employeeRepository.findById(Mockito.anyLong())).willReturn(Optional.of(new Employee()));;
	}

	@Test
	public void testPersistirFuncionario() {
		Employee employee = this.employeeService.persistir(new Employee());

		assertNotNull(employee);
	}

	@Test
	public void testBuscarFuncionarioPorId() {
		Optional<Employee> funcionario = this.employeeService.buscarPorId(1L);

		assertTrue(funcionario.isPresent());
	}

	@Test
	public void testBuscarFuncionarioPorEmail() {
		Optional<Employee> employee = this.employeeService.buscarPorEmail("email@email.com");

		assertTrue(employee.isPresent());
	}

	@Test
	public void testBuscarFuncionarioPorCpf() {
		Optional<Employee> employee = this.employeeService.buscarPorCpf("24291173474");

		assertTrue(employee.isPresent());
	}

}

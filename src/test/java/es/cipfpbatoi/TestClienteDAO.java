package es.cipfpbatoi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import es.cipfpbatoi.dao.ClienteDAO;
import es.cipfpbatoi.dao.ConexionBD;
import es.cipfpbatoi.modelo.Cliente;

@TestMethodOrder(OrderAnnotation.class)
class TestClienteDAO {
	static ClienteDAO capaDao;
	Cliente registroVacio = new Cliente();
	Cliente registroExiste1 = new Cliente(1, "Matt Design", "C/ Pintor Sorolla, 3");
	Cliente registroExiste2 = new Cliente(4, "Luis Llull", "C/ Salvador Dalí, 3");
	Cliente registroNoExiste = new Cliente(100, null, null);
	Cliente registroNuevo = new Cliente("insert nombre test", "insert direccion test");
	Cliente registroNuevoError = new Cliente("insert nombre test 111111111111111111111111111111111111111111",
			"insert direccion test");
	Cliente registroModificarBorrar = new Cliente(5, "update nombre test", "update direccion test");
	Cliente registroModificarBorrarError = new Cliente(5,
			"update nombre test 111111111111111111111111111111111111111111", "update direccion test");
	static int numRegistrosEsperado = 5;
	static int autoIncrement = 5;
	final static String TABLA = "clientes";
	final static String BD = "empresa_ad_test";

	@BeforeAll
	static void setUpBeforeClass() {
		try {
			capaDao = new ClienteDAO();

			ConexionBD.getConexion().createStatement()
					.executeUpdate("delete from " + BD + "." + TABLA + " where id >= " + numRegistrosEsperado);

			if (ConexionBD.getConexion().getMetaData().getDatabaseProductName() == "MariaDB") {
				ConexionBD.getConexion().createStatement()
						.executeUpdate("ALTER TABLE " + BD + "." + TABLA + " AUTO_INCREMENT = " + autoIncrement);
			} else { // PostgreSQL
				System.out.println("antes");
				ConexionBD.getConexion().createStatement()
						.executeUpdate("ALTER SEQUENCE " + BD + "." + TABLA + "_id_seq RESTART WITH " + autoIncrement);
			}

			ConexionBD.getConexion().createStatement().executeUpdate(
					"insert into " + BD + "." + TABLA + "(nombre, direccion) values ('nombre test', 'direccion test')");

		} catch (SQLException e) {
			fail("El test falla al preparar el test (instanciando dao: posiblemente falla la conexión a la BD)");
		}
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		capaDao.cerrar();
	}

	@Test
	@Order(1)
	void testFind() {
		Cliente registroObtenido = capaDao.find(registroExiste1.getId());
		Cliente registroEsperado = registroExiste1;
		assertEquals(registroEsperado, registroObtenido);

		registroObtenido = capaDao.find(registroExiste2.getId());
		registroEsperado = registroExiste2;
		assertEquals(registroEsperado, registroObtenido);

		registroObtenido = capaDao.find(registroNoExiste.getId());
		assertNull(registroObtenido);
	}

	@Test
	@Order(1)
	void testFindAll() {
		int numRegistrosObtenido = capaDao.findAll().size();
		assertEquals(numRegistrosEsperado, numRegistrosObtenido);
	}

	@Test
	@Order(2)
	void testInsert() {
		Boolean respuestaObtenida = capaDao.insert(registroNuevo);
		assertTrue(respuestaObtenida);
		assertNotEquals(0, registroNuevo.getId());
		assertEquals(6, registroNuevo.getId());
		respuestaObtenida = capaDao.insert(registroNuevoError);
		assertFalse(respuestaObtenida);
	}

	@Test
	@Order(3)
	void testUpdate() {
		boolean respuestaObtenida = capaDao.update(registroModificarBorrar);
		assertTrue(respuestaObtenida);
		respuestaObtenida = capaDao.update(registroNoExiste);
		assertFalse(respuestaObtenida);
		respuestaObtenida = capaDao.update(registroModificarBorrarError);
		assertFalse(respuestaObtenida);
	}

	@Test
	@Order(4)
	void testDelete() {
		boolean respuestaObtenida = capaDao.delete(registroModificarBorrar);
		assertTrue(respuestaObtenida);
		respuestaObtenida = capaDao.delete(registroNoExiste.getId());
		assertFalse(respuestaObtenida);
	}

	@Test
	@Order(1)
	void testSize() {
		int respuestaObtenida = capaDao.size();
		assertEquals(numRegistrosEsperado, respuestaObtenida);
	}

	@Test
	@Order(6)
	void testExists() {
		boolean respuestaObtenida = capaDao.exists(registroExiste1.getId());
		assertTrue(respuestaObtenida);
		respuestaObtenida = capaDao.exists(registroNoExiste.getId());
		assertFalse(respuestaObtenida);
	}

	@Test
	@Order(1)
	void testFindExample() {
		int numRegistrosObtenido = capaDao.findByExample(registroVacio).size();
		assertEquals(numRegistrosEsperado, numRegistrosObtenido);
		
		numRegistrosObtenido = capaDao.findByExample(registroExiste1).size();
		assertEquals(1, numRegistrosObtenido);

		Cliente registro = new Cliente("ana", null);
		numRegistrosObtenido = capaDao.findByExample(registro).size();
		assertEquals(1, numRegistrosObtenido);

		registro = new Cliente(null, "C/");
		numRegistrosObtenido = capaDao.findByExample(registro).size();
		assertEquals(3, numRegistrosObtenido);

		registro = new Cliente("NADA", "NADA");
		numRegistrosObtenido = capaDao.findByExample(registro).size();
		assertEquals(0, numRegistrosObtenido);
	}

}

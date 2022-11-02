package es.cipfpbatoi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import es.cipfpbatoi.dao.ConexionBD;
import es.cipfpbatoi.dao.GrupoDAO;
import es.cipfpbatoi.modelo.Grupo;

@TestMethodOrder(OrderAnnotation.class)
class TestGrupoDAO {
	static GrupoDAO capaDao;
	Grupo registroVacio = new Grupo();
	Grupo registroExiste1 = new Grupo(1, "Hardware");
	Grupo registroExiste2 = new Grupo(3, "Otros");
	Grupo registroNoExiste = new Grupo(100, "no existe");
	Grupo registroNuevo = new Grupo("insert test");
	Grupo registroNuevoError = new Grupo("insert test 12345");
	Grupo registroModificarBorrar = new Grupo(4, "update test");
	Grupo registroModificarBorrarError = new Grupo(4, "update test 12345");
	static int numRegistrosEsperado = 4;
	static int autoIncrement = 4;
	final static String TABLA = "grupos";
	final static String BD = "empresa_ad_test";

	@BeforeAll
	static void setUpBeforeClass() {
		try {
			capaDao = new GrupoDAO();

			ConexionBD.getConexion().createStatement()
					.executeUpdate("delete from " + BD + "." + TABLA + " where id >= " + numRegistrosEsperado);

			if (ConexionBD.getConexion().getMetaData().getDatabaseProductName().equals("MariaDB")) {
				ConexionBD.getConexion().createStatement()
						.executeUpdate("ALTER TABLE " + BD + "." + TABLA + " AUTO_INCREMENT = " + autoIncrement);
			} else { // PostgreSQL
				System.out.println("antes");
				ConexionBD.getConexion().createStatement()
						.executeUpdate("ALTER SEQUENCE " + BD + "." + TABLA + "_id_seq RESTART WITH " + autoIncrement);
				System.out.println("despues");
			}

			ConexionBD.getConexion().createStatement()
					.executeUpdate("insert into " + BD + ".grupos(descripcion) values ('descrip test')");

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
	void testfind() {
		Grupo registroObtenido = capaDao.find(registroExiste1.getId());
		Grupo registroEsperado = registroExiste1;
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
		assertEquals(5, registroNuevo.getId());
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
	void testSave() {		
		Boolean respuestaObtenida = capaDao.save(registroModificarBorrar);
		assertTrue(respuestaObtenida);
		respuestaObtenida = capaDao.save(registroNoExiste);
		assertTrue(respuestaObtenida);
		assertNotEquals(0, registroNoExiste.getId());
		assertEquals(6, registroNoExiste.getId());
		respuestaObtenida = capaDao.save(registroModificarBorrarError);
		assertFalse(respuestaObtenida);
	}

	@Test
	@Order(5)
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

}

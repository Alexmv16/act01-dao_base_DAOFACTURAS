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

import es.cipfpbatoi.dao.ArticuloDAO;
import es.cipfpbatoi.dao.ConexionBD;
import es.cipfpbatoi.modelo.Articulo;
import es.cipfpbatoi.modelo.Grupo;

@TestMethodOrder(OrderAnnotation.class)
class TestArticuloDAO {
	static ArticuloDAO capaDao;
	Articulo registroVacio = new Articulo();
	Articulo registroExiste1 = new Articulo(1, "Monitor 20", 178f, "mon20", new Grupo(1, "Hardware"));
	Articulo registroExiste2 = new Articulo(5, "Papel A4-500", 4f, "PA4500", new Grupo(2, "Suministros"));
	Articulo registroNoExiste = new Articulo(100, "No existe", 0f, "ne", new Grupo(1, "Hardware"));
	Articulo registroNuevo = new Articulo("insert test", 100f, "instest", new Grupo(1, "Hardware"));
	Articulo registroNuevoError = new Articulo("insert test", 100f, "instest", new Grupo(100, "xxxx"));
	Articulo registroModificarBorrar = new Articulo(9, "update test", 100f, "updtest", new Grupo(1, "Hardware"));
	Articulo registroModificarBorrarError = new Articulo(9, "update test", 100f, "updtest", new Grupo(100, "xxxx"));
	static int numRegistrosEsperado = 9;
	static int autoIncrement = 9;
	final static String TABLA = "articulos";
	final static String BD = "empresa_ad_test";

	@BeforeAll
	static void setUpBeforeClass() {
		try {
			capaDao = new ArticuloDAO();

			ConexionBD.getConexion().createStatement()
					.executeUpdate("delete from " + BD + "." + TABLA + " where id >= " + numRegistrosEsperado);

			if (ConexionBD.getConexion().getMetaData().getDatabaseProductName().equals("MariaDB")) {
				ConexionBD.getConexion().createStatement()
						.executeUpdate("ALTER TABLE " + BD + "." + TABLA + " AUTO_INCREMENT = " + autoIncrement);
			} else { // PostgreSQL
				ConexionBD.getConexion().createStatement()
						.executeUpdate("ALTER SEQUENCE " + BD + "." + TABLA + "_id_seq RESTART WITH " + autoIncrement);
			}

			ConexionBD.getConexion().createStatement().executeUpdate("insert into " + BD + "." + TABLA
					+ "(nombre, precio, codigo, grupo) values ('nombre test', 100, 'nomtest', 1)");

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
		Articulo registroObtenido = capaDao.find(registroExiste1.getId());
		Articulo registroEsperado = registroExiste1;
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
		assertEquals(10, registroNuevo.getId());
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
		assertEquals(12, registroNoExiste.getId());
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

	@Test
	@Order(1)
	void testFindExample() {
		int numRegistrosObtenido = capaDao.findByExample(registroVacio).size();
		assertEquals(numRegistrosEsperado, numRegistrosObtenido);

		numRegistrosObtenido = capaDao.findByExample(registroExiste1).size();
		assertEquals(1, numRegistrosObtenido);

		Articulo registro = new Articulo("Monitor", 0, null, null);
		numRegistrosObtenido = capaDao.findByExample(registro).size();
		assertEquals(3, numRegistrosObtenido);

		registro = new Articulo("Monitor", 200f, null, null);
		numRegistrosObtenido = capaDao.findByExample(registro).size();
		assertEquals(2, numRegistrosObtenido);

		registro = new Articulo(null, 60, null, null);
		numRegistrosObtenido = capaDao.findByExample(registro).size();
		assertEquals(4, numRegistrosObtenido);

		registro = new Articulo(null, 60, null, new Grupo(1, null));
		numRegistrosObtenido = capaDao.findByExample(registro).size();
		assertEquals(2, numRegistrosObtenido);

		registro = new Articulo(null, 0, null, new Grupo(1, null));
		numRegistrosObtenido = capaDao.findByExample(registro).size();
		assertEquals(7, numRegistrosObtenido);
	}

	@Test
	@Order(1)
	void testFindByGrupo() {
		int numRegistrosObtenido = capaDao.findByGrupo(new Grupo(1000, null)).size();
		assertEquals(0, numRegistrosObtenido);

		numRegistrosObtenido = capaDao.findByGrupo(new Grupo(1, null)).size();
		assertEquals(7, numRegistrosObtenido);

		numRegistrosObtenido = capaDao.findByGrupo(new Grupo(2, null)).size();
		assertEquals(1, numRegistrosObtenido);
	}
	
}

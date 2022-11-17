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
import es.cipfpbatoi.dao.LineaFacturaDAO;
import es.cipfpbatoi.modelo.Articulo;
import es.cipfpbatoi.modelo.Grupo;
import es.cipfpbatoi.modelo.LineaFactura;

@TestMethodOrder(OrderAnnotation.class)
class TestLineaFacturaDAO {
	static LineaFacturaDAO capaDao;
	Articulo art1 = new Articulo(1, "Monitor 20", 178f, "mon20", new Grupo(1, "Hardware"));
	LineaFactura registroVacio = new LineaFactura();
	LineaFactura registroExiste1 = new LineaFactura(1, 35, art1, 3, 534);
	LineaFactura registroExiste2 = new LineaFactura(1, 1538, art1, 1, 178);
	LineaFactura registroNoExiste = new LineaFactura(100, 1538, art1, 1, 178);
	LineaFactura registroNoExisteError = new LineaFactura(100, 6000, art1, 1, 178);
	LineaFactura registroNuevo1 = new LineaFactura(1, art1, 1);
	LineaFactura registroNuevo2 = new LineaFactura(2, art1, 3);
	LineaFactura registroModificarBorrar = new LineaFactura(3, 1, art1, 6);
	static int numRegistrosEsperadoFactura = 3;
	// static int autoIncrement = x;
	final static String TABLA = "lineas_factura";
	final static String BD = "empresa_ad_test";

	@BeforeAll
	static void setUpBeforeClass() {
		try {

			capaDao = new LineaFacturaDAO();
			ConexionBD.getConexion().createStatement()
					.executeUpdate("delete from " + BD + "." + TABLA + " where linea >=3 and factura = 1");
			ConexionBD.getConexion().createStatement()
					.executeUpdate("delete from " + BD + "." + TABLA + " where linea >4 and factura = 2");

			ConexionBD.getConexion().createStatement().executeUpdate("insert into " + BD + "." + TABLA
					+ "(linea, factura, articulo, cantidad, importe) values (3, 1, 1, 5, 5*178)");

		} catch (SQLException e) {
			fail("El test falla al preparar el test (instanciando dao: posiblemente falla la conexión a la BD)");
		}
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		ConexionBD.getConexion().createStatement()
				.executeUpdate("delete from " + BD + "." + TABLA + " where linea >=3 and factura = 1");
		ConexionBD.getConexion().createStatement()
				.executeUpdate("delete from " + BD + "." + TABLA + " where linea >4 and factura = 2");
		capaDao.cerrar();
	}

//	@BeforeEach
//	static void setUp() {
//		try {
//			ConexionBD.getConexion().createStatement().executeUpdate("delete from empresa_ad.clientes where id > 5");
//		} catch (SQLException e) {
//			fail("El test falla en la preparación antes de cada test (preparando tabla clientes)");
//		}
//	}

	@Test
	@Order(1)
	void testfind() {
		LineaFactura registroObtenido = capaDao.find(registroExiste1.getLinea(), registroExiste1.getFactura());
		LineaFactura registroEsperado = registroExiste1;
		assertEquals(registroEsperado, registroObtenido);

		registroObtenido = capaDao.find(registroExiste2.getLinea(), registroExiste2.getFactura());
		registroEsperado = registroExiste2;
		assertEquals(registroEsperado, registroObtenido);

		registroObtenido = capaDao.find(registroNoExiste.getLinea(), registroNoExiste.getFactura());
		assertNull(registroObtenido);
	}

	@Test
	@Order(2)
	void testInsert() {
		boolean respuestaObtenida = capaDao.insert(registroNuevo1);
		assertTrue(respuestaObtenida);
		assertEquals(4, registroNuevo1.getLinea());
		assertEquals(178f, registroNuevo1.getImporte());
		respuestaObtenida = capaDao.insert(registroNoExisteError);
		assertFalse(respuestaObtenida);
	}

	@Test
	@Order(3)
	void testUpdate() {
		boolean respuestaObtenida = capaDao.update(registroModificarBorrar);
		assertTrue(respuestaObtenida);
		assertEquals(6 * 178f, registroModificarBorrar.getImporte());

		respuestaObtenida = capaDao.update(registroNoExisteError);
		assertFalse(respuestaObtenida);
	}

	@Test
	@Order(4)
	void testSave() {
		boolean respuestaObtenida = capaDao.save(registroNuevo2);
		assertTrue(respuestaObtenida);
		assertEquals(5, registroNuevo2.getLinea());
		assertEquals(3 * 178f, registroNuevo2.getImporte());

		registroModificarBorrar.setCantidad(7);
		respuestaObtenida = capaDao.save(registroModificarBorrar);
		assertTrue(respuestaObtenida);
		assertEquals(7 * 178f, registroModificarBorrar.getImporte());

		respuestaObtenida = capaDao.save(registroNoExisteError);
		assertFalse(respuestaObtenida);
	}

	@Test
	@Order(4)
	void testDelete() {
		boolean respuestaObtenida = capaDao.delete(registroModificarBorrar);
		assertTrue(respuestaObtenida);
		respuestaObtenida = capaDao.delete(registroNoExiste.getLinea(), registroNoExiste.getFactura());
		assertFalse(respuestaObtenida);
	}

	@Test
	@Order(1)
	void testSize() {
		int respuestaObtenida = capaDao.size();
		assertEquals(14001, respuestaObtenida);
	}

	@Test
	@Order(6)
	void testExists() {
		boolean respuestaObtenida = capaDao.exists(registroExiste1.getLinea(), registroExiste1.getFactura());
		assertTrue(respuestaObtenida);
		respuestaObtenida = capaDao.exists(registroNoExiste.getLinea(), registroNoExiste.getFactura());
		assertFalse(respuestaObtenida);
	}

	@Test
	@Order(2)
	void testFindByFactura() {
		int numRegistrosObtenido = capaDao.findByFactura(registroExiste1.getFactura()).size();
		assertEquals(numRegistrosEsperadoFactura, numRegistrosObtenido);

		numRegistrosObtenido = capaDao.findByFactura(1000).size();
		assertEquals(numRegistrosEsperadoFactura, numRegistrosObtenido);

		numRegistrosObtenido = capaDao.findByFactura(5000).size();
		assertEquals(0, numRegistrosObtenido);

		numRegistrosObtenido = capaDao.findByFactura(6000).size();
		assertEquals(0, numRegistrosObtenido);
	}

}

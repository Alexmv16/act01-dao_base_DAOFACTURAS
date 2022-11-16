package es.cipfpbatoi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import es.cipfpbatoi.dao.ConexionBD;
import es.cipfpbatoi.dao.FacturaDAO;
import es.cipfpbatoi.modelo.Cliente;
import es.cipfpbatoi.modelo.Factura;
import es.cipfpbatoi.modelo.Vendedor;

@TestMethodOrder(OrderAnnotation.class)
class TestFacturaDAO {
	static FacturaDAO capaDao;
	Factura registroVacio = new Factura();
	Cliente cli2 = new Cliente(2, "Diana Perez", "C/ Brito del Pino, 1120");
	Vendedor ven2 = new Vendedor(2, "Juan Fernandez", LocalDate.of(2014, 3, 1), 11500f);

	Factura registroExiste1 = new Factura(2, LocalDate.of(2008, 3, 18), cli2, ven2, "Contado");
	Factura registroExiste2 = new Factura(17, LocalDate.of(2012, 10, 11), cli2, ven2, "transferencia");
	Factura registroNoExiste = new Factura(10000, LocalDate.of(0, 1, 1), cli2, ven2, "Contado");

	Factura registroNuevo = new Factura(LocalDate.of(2021, 12, 1), cli2, ven2, "tarjeta");
	Factura registroNuevoError = new Factura(LocalDate.of(2021, 12, 1), new Cliente(100, null, null), ven2, "tarjeta");
	Factura registroModificarBorrar = new Factura(5000, LocalDate.of(2021, 12, 2), cli2, ven2, "Contado");
	Factura registroModificarBorrarError = new Factura(5000, LocalDate.of(2021, 12, 2), new Cliente(100, null, null),
			ven2, "Contado");

	Factura registroPrimeroPag1 = new Factura(1, null, null, null, null);
	Factura registroUltimoPag1 = new Factura(100, null, null, null, null);
	Factura registroPrimeroPag1Cli2 = new Factura(2, null, null, null, null);
	Factura registroUltimoPag1Cli2 = new Factura(291, null, null, null, null);
	Factura registroPrimeroPag1Vend2 = new Factura(2, null, null, null, null);
	Factura registroUltimoPag1Vend2 = new Factura(195, null, null, null, null);

	Factura registroPrimeroPag5 = new Factura(401, null, null, null, null);
	Factura registroUltimoPag5 = new Factura(500, null, null, null, null);
	Factura registroPrimeroPag5Cli2 = new Factura(1154, null, null, null, null);
	Factura registroUltimoPag5Cli2 = new Factura(1474, null, null, null, null);
	Factura registroPrimeroPag5Vend2 = new Factura(789, null, null, null, null);
	Factura registroUltimoPag5Vend2 = new Factura(987, null, null, null, null);

	static int numRegistrosEsperado = 5000;
	static int autoIncrement = 5000;
	final static String TABLA = "facturas";
	final static String BD = "empresa_ad_test";
	final int PAGESIZE = 100;

	@BeforeAll
	static void setUpBeforeClass() {
		try {
			capaDao = new FacturaDAO();

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
					+ "(fecha, cliente, vendedor, formapago) values ('2021-11-30', 4, 1, 'Contado')");

		} catch (SQLException e) {
			fail("El test falla al preparar el test (instanciando dao: posiblemente falla la conexión a la BD)");
		}
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
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
		Factura registroObtenido = capaDao.find(registroExiste1.getId());
		Factura registroEsperado = registroExiste1;
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
		List<Factura> lista = capaDao.findAll();
		int numRegistrosObtenido = lista.size();
		assertEquals(PAGESIZE, numRegistrosObtenido);
		assertEquals(registroPrimeroPag1.getId(), lista.get(0).getId());
		assertEquals(registroUltimoPag1.getId(), lista.get(lista.size() - 1).getId());
	}

	@Test
	@Order(1)
	void testFindAllPage() {
		List<Factura> lista = capaDao.findAll(5);
		int numRegistrosObtenido = lista.size();
		assertEquals(PAGESIZE, numRegistrosObtenido);
		assertEquals(registroPrimeroPag5.getId(), lista.get(0).getId());
		assertEquals(registroUltimoPag5.getId(), lista.get(lista.size() - 1).getId());

		numRegistrosObtenido = capaDao.findAll(1000).size(); // pagina que no existe
		assertEquals(0, numRegistrosObtenido);
	}

	@Test
	@Order(1)
	void testFindByCliente() {
		List<Factura> lista = capaDao.findByCliente(cli2);
		int numRegistrosObtenido = lista.size();
		assertEquals(PAGESIZE, numRegistrosObtenido);
		assertEquals(registroPrimeroPag1Cli2.getId(), lista.get(0).getId());
		assertEquals(registroUltimoPag1Cli2.getId(), lista.get(lista.size() - 1).getId());

		numRegistrosObtenido = capaDao.findByCliente(new Cliente(100, null, null)).size();
		assertEquals(0, numRegistrosObtenido);
	}

	@Test
	@Order(1)
	void testFindByClientePage() {
		List<Factura> lista = capaDao.findByCliente(cli2, 5);
		int numRegistrosObtenido = lista.size();
		assertEquals(PAGESIZE, numRegistrosObtenido);
		assertEquals(registroPrimeroPag5Cli2.getId(), lista.get(0).getId());
		assertEquals(registroUltimoPag5Cli2.getId(), lista.get(lista.size() - 1).getId());

		numRegistrosObtenido = capaDao.findByCliente(cli2, 1000).size(); // pagina que no existe
		assertEquals(0, numRegistrosObtenido);

		numRegistrosObtenido = capaDao.findByCliente(new Cliente(100, null, null), 5).size();
		assertEquals(0, numRegistrosObtenido);
	}

	@Test
	@Order(1)
	void testFindByVendedor() {
		List<Factura> lista = capaDao.findByVendedor(ven2);
		int numRegistrosObtenido = lista.size();
		assertEquals(PAGESIZE, numRegistrosObtenido);
		assertEquals(registroPrimeroPag1Vend2.getId(), lista.get(0).getId());
		assertEquals(registroUltimoPag1Vend2.getId(), lista.get(lista.size() - 1).getId());

		numRegistrosObtenido = capaDao.findByVendedor(new Vendedor(100, null, null, 0)).size();
		assertEquals(0, numRegistrosObtenido);
	}

	@Test
	@Order(1)
	void testFindByVendedorPage() {
		List<Factura> lista = capaDao.findByVendedor(ven2, 5);
		int numRegistrosObtenido = lista.size();
		assertEquals(PAGESIZE, numRegistrosObtenido);
		assertEquals(registroPrimeroPag5Vend2.getId(), lista.get(0).getId());
		assertEquals(registroUltimoPag5Vend2.getId(), lista.get(lista.size() - 1).getId());

		numRegistrosObtenido = capaDao.findByVendedor(ven2, 1000).size(); // pagina que no existe
		assertEquals(0, numRegistrosObtenido);

		numRegistrosObtenido = capaDao.findByVendedor(new Vendedor(100, null, null, 0), 5).size();
		assertEquals(0, numRegistrosObtenido);
	}

	@Test
	@Order(2)
	void testInsert() {
		Boolean respuestaObtenida = capaDao.insert(registroNuevo);
		assertTrue(respuestaObtenida);
		assertNotEquals(0, registroNuevo.getId());
		assertEquals(numRegistrosEsperado + 1, registroNuevo.getId());
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
		assertEquals(numRegistrosEsperado + 3, registroNoExiste.getId());
		respuestaObtenida = capaDao.save(registroModificarBorrarError);
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
	@Order(7)
	void testGetNextLine() {
		int respuestaObtenida = capaDao.getNextLine(registroExiste1.getId());
		assertEquals(5, respuestaObtenida);

		respuestaObtenida = capaDao.getNextLine(1);
		assertEquals(3, respuestaObtenida);
		respuestaObtenida = capaDao.getNextLine(numRegistrosEsperado + 1); // factura sin lineas
		assertEquals(1, respuestaObtenida);

		respuestaObtenida = capaDao.getNextLine(registroNoExiste.getId());
		assertEquals(-1, respuestaObtenida);
	}

	@Test
	@Order(8)
	void testGetImporteTotalExists() {
		double respuestaObtenida = capaDao.getImporteTotal(registroExiste1.getId());
		assertEquals(112, respuestaObtenida);
		respuestaObtenida = capaDao.getImporteTotal(registroExiste2.getId());
		assertEquals(4308, respuestaObtenida);

		respuestaObtenida = capaDao.getImporteTotal(registroNoExiste.getId());
		assertEquals(-1, respuestaObtenida);
	}

	@Test
	@Order(9)
	void testFindByExample() {
		assertNull(capaDao.findByExample(registroExiste1));
	}

}

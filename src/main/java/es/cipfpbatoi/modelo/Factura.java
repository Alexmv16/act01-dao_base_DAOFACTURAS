package es.cipfpbatoi.modelo;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Factura {
	private int id;
	private LocalDate fecha;
	private Cliente cliente;
	private Vendedor vendedor;
	private String formaPago;
	private List<LineaFactura> lineas;

	public Factura() {
		this.lineas = null;
	}

	public Factura(LocalDate fecha, Cliente cliente, Vendedor vendedor, String formaPago) {
		this.fecha = fecha;
		this.cliente = cliente;
		this.vendedor = vendedor;
		this.formaPago = formaPago;
		this.lineas = null;
	}

	public Factura(LocalDate fecha, Cliente cliente, Vendedor vendedor, String formaPago, List<LineaFactura> lineas) {
		this.fecha = fecha;
		this.cliente = cliente;
		this.vendedor = vendedor;
		this.formaPago = formaPago;
		this.lineas = lineas;
	}

	public Factura(int id, LocalDate fecha, Cliente cliente, Vendedor vendedor, String formaPago) {
		this.id = id;
		this.fecha = fecha;
		this.cliente = cliente;
		this.vendedor = vendedor;
		this.formaPago = formaPago;
		this.lineas = null;
	}

	public Factura(int id, LocalDate fecha, Cliente cliente, Vendedor vendedor, String formaPago,
			List<LineaFactura> lineas) {
		this.id = id;
		this.fecha = fecha;
		this.cliente = cliente;
		this.vendedor = vendedor;
		this.formaPago = formaPago;
		this.lineas = lineas;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Vendedor getVendedor() {
		return vendedor;
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}

	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	public List<LineaFactura> getLineas() {
		return lineas;
	}

	public void setLineas(List<LineaFactura> lineas) {
		this.lineas = lineas;
	}

	@Override
	public String toString() {
		return "Factura [id=" + id + ", fecha=" + fecha + ", cliente=" + cliente.getId() + " " + cliente.getNombre()
				+ ", vendedor=" + vendedor.getId() + " " + vendedor.getNombre() + ", formaPago=" + formaPago + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(cliente, fecha, formaPago, id, lineas, vendedor);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Factura other = (Factura) obj;
		return Objects.equals(cliente, other.cliente) && Objects.equals(fecha, other.fecha)
				&& Objects.equals(formaPago, other.formaPago) && id == other.id && Objects.equals(lineas, other.lineas)
				&& Objects.equals(vendedor, other.vendedor);
	}

}

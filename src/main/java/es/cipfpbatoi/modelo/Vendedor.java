package es.cipfpbatoi.modelo;

import java.time.LocalDate;
import java.util.Objects;

public class Vendedor {
	private int id;
	private String nombre;
	private LocalDate fecha_ingreso;
	private float salario;

	public Vendedor() {
	}

	public Vendedor(String nombre, LocalDate fecha_ingreso, float salario) {
		this.nombre = nombre;
		this.fecha_ingreso = fecha_ingreso;
		this.salario = salario;
	}

	public Vendedor(int id, String nombre, LocalDate fecha_ingreso, float salario) {
		this.id = id;
		this.nombre = nombre;
		this.fecha_ingreso = fecha_ingreso;
		this.salario = salario;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public LocalDate getFecha_ingreso() {
		return fecha_ingreso;
	}

	public void setFecha_ingreso(LocalDate fecha_ingreso) {
		this.fecha_ingreso = fecha_ingreso;
	}

	public float getSalario() {
		return salario;
	}

	public void setSalario(float salario) {
		this.salario = salario;
	}

	@Override
	public String toString() {
		return "Vendedor [id=" + id + ", nombre=" + nombre + ", fecha_ingreso=" + fecha_ingreso + ", salario=" + salario
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(fecha_ingreso, id, nombre, salario);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vendedor other = (Vendedor) obj;
		return Objects.equals(fecha_ingreso, other.fecha_ingreso) && id == other.id
				&& Objects.equals(nombre, other.nombre)
				&& Float.floatToIntBits(salario) == Float.floatToIntBits(other.salario);
	}

	
	
}

package definicion;

import java.util.Objects;

/**
 * Clase que modela un empleado de la vida real.
 * 
 * @author Alberto Lebrero Pizarro - 2º DAM - CEU
 * @version 1.0
 */
public class Empleado {
	private String codigo;
	private String nombre;
	private float salario;
	private Empleo trabajo;
	
	public Empleado() {}
	
	public Empleado(String codigo, String nombre, float salario, Empleo trabajo) {
		this.codigo = codigo;
		this.nombre = nombre;
		this.salario = salario;
		this.trabajo = trabajo;
	}
	
	public String getCodigo() {
		return codigo;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public float getSalario() {
		return salario;
	}
	
	public Empleo getTrabajo() {
		return trabajo;
	}

	public void setSalario(float nuevoSalario) {
		this.salario = nuevoSalario;
	}

	@Override
	public int hashCode() {
		return Objects.hash(codigo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Empleado other = (Empleado) obj;
		return Objects.equals(codigo, other.codigo);
	}
	
}

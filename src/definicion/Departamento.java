package definicion;
import java.util.LinkedList;
import java.util.List;

/**
 * Clase que modela un departamento de la vida real.
 * 
 * @author Alberto Lebrero Pizarro - 2º DAM - CEU
 * @version 1.0
 */
public class Departamento {
	private String codigo;
	private String nombre;
	private List<Empleado> empleados;
	
	public Departamento() {}
	
	public Departamento(String codigo, String nombre) {
		this.codigo = codigo;
		this.nombre = nombre;
		this.empleados = new LinkedList<>();
	}
	
	public String getCodigo() {
		return codigo;
	}
	
	public String getNombre() {
		return nombre;
	}

	public List<Empleado> getEmpleados() {
		return empleados;
	}
	
	public void annadirEmpleado(Empleado empleadoAAnnadir) {
		empleados.add(empleadoAAnnadir);
	}
	
	public boolean borrarEmpleado(Empleado empleadoABorrar) {
		return empleados.remove(empleadoABorrar);
	}
}

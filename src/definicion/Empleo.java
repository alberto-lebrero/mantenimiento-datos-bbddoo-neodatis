package definicion;

/**
 * Clase que modela un empleo de la vida real.
 * 
 * @author Alberto Lebrero Pizarro - 2º DAM - CEU
 * @version 1.0
 */
public class Empleo {
	private String codigo;
	private String nombre;
	
	public Empleo() {}
	
	public Empleo(String codigo, String nombre) {
		this.codigo = codigo;
		this.nombre = nombre;
	}
	
	public String getCodigo() {
		return codigo;
	}

	public String getNombre() {
		return nombre;
	}
}

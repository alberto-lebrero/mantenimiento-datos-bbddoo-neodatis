package aplicacion;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;

import definicion.Departamento;
import definicion.Empleado;

/**
 * Clase que modela el mantenimiento de los datos de una base de datos de Neodatis ya creada.
 * 
 * @author Alberto Lebrero Pizarro - 2º DAM - CEU
 * @version 1.3
 */
public class MantenimientoDatos {
	private Scanner teclado;
	
	/**
	 * Constructor por defecto que crea una instancia de la entrada estándar (Scanner) 
	 * para poder solicitar datos por pantalla.
	 */
	public MantenimientoDatos() {
		this.teclado = new Scanner(System.in);
	}
	
	
	/**
	 * Visualiza los datos de los empleados de un departamento en específico.
	 * En caso de que ese departamento no exista se mostrará un mensaje por pantalla indicándolo.
	 * 
	 * @param odb Objeto de la base de datos de Neodatis sobre la cual se realizará la consulta.
	 */
	public void consultarDepartamento(ODB odb) {
	    int codigoSolicitado = solicitarEntero(teclado, "Introduce el código del departamento a consultar: ");
	    
	    IQuery consultaDepartamento = new CriteriaQuery(Departamento.class, Where.equal("codigo", String.valueOf(codigoSolicitado)));
	    Objects<Departamento> resultadoConsultaDepartamento = odb.getObjects(consultaDepartamento);
	    
	    if(resultadoConsultaDepartamento.hasNext()) {
	        Departamento departamentoEncontrado = resultadoConsultaDepartamento.next();
	        
	        System.out.println("-".repeat(40));
	        System.out.println("Departamento " + departamentoEncontrado.getCodigo() + ": " + departamentoEncontrado.getNombre()
	                                + " (" + departamentoEncontrado.getEmpleados().size() + " empleados)");
	        
	        for(Empleado empleado : departamentoEncontrado.getEmpleados()) {
	            System.out.println("-".repeat(20));
	            System.out.println("Empleado " + empleado.getCodigo() + ": " + empleado.getNombre());
	            System.out.println("Empleo: " + empleado.getTrabajo().getNombre());
	            System.out.println("Salario: " + empleado.getSalario() + "€");
	        }
	        
	        System.out.println("-".repeat(40));
	    } else {
	        System.out.println("-".repeat(25));
	        System.out.println("Departamento inexistente.");
	        System.out.println("-".repeat(25));
	    }
	}
	
	/**
	 * Modifica el salario de un empleado solicitando previamente su código por la entrada estándar.
	 * 
	 * @param odb Objeto de la base de datos de Neodatis sobre la cual se realizará la modificación del salario del empleado.
	 */
	public void modificarSalarioEmpleado(ODB odb) {
	    int codigoSolicitado = solicitarEntero(teclado, "Introduce el código del empleado a modificar: ");
	    
	    IQuery consultaEmpleado = new CriteriaQuery(Empleado.class, Where.equal("codigo", String.valueOf(codigoSolicitado)));
	    Objects<Empleado> resultadoConsultaEmpleado = odb.getObjects(consultaEmpleado);
	    
	    if (resultadoConsultaEmpleado.hasNext()) {
	        Empleado empleadoAModificar = resultadoConsultaEmpleado.next();
	        System.out.println("-".repeat(40));
	        System.out.println("Empleado " + empleadoAModificar.getCodigo() + ": " + empleadoAModificar.getNombre());
	        System.out.println("Empleo: " + empleadoAModificar.getTrabajo().getNombre());
	        System.out.println("Salario actual: " + empleadoAModificar.getSalario() + "€");
	        System.out.println("-".repeat(40));
	        
	        String confirmacion = solicitarCadena(teclado, "¿Desea modificar su salario? (s/n): ");
	        
	        if(confirmacion.equalsIgnoreCase("s")) {
	        	float nuevoSalario = solicitarEntero(teclado, "Introduce el nuevo salario: ");
		        
	        	empleadoAModificar.setSalario(nuevoSalario);
		        odb.store(empleadoAModificar);
		        
		        System.out.println("-".repeat(25));
		        System.out.println("Salario modificado.");
		        System.out.println("-".repeat(25));
	        }else {
		        System.out.println("-".repeat(25));
	        	System.out.println("Operación cancelada.");
		        System.out.println("-".repeat(25));
	        }
	    } else {
	        System.out.println("-".repeat(25));
	        System.out.println("El empleado con código " + codigoSolicitado + "no existe en la base de datos.");
	        System.out.println("-".repeat(25));
	    }
	}
	
	
	public void borrarEmpleado(ODB odb) {
	    int codigoSolicitado = solicitarEntero(teclado, "Introduce el código del empleado a eliminar: ");

	    //Busco el empleado en la base de datos Neodatis
	    IQuery consultaEmpleado = new CriteriaQuery(Empleado.class, Where.equal("codigo", String.valueOf(codigoSolicitado)));
	    Objects<Empleado> empleados = odb.getObjects(consultaEmpleado);

	    boolean empleadoExiste = empleados.hasNext();
	    Empleado empleadoAEliminar = empleadoExiste ? empleados.next() : null;

	    if (!empleadoExiste) {
	        System.out.println("Empleado con código " + codigoSolicitado + " no existe en la base de datos.");
	    } else {
	        System.out.println("Empleado encontrado: " + empleadoAEliminar.getNombre());
	        String confirmacion = solicitarCadena(teclado, "¿Deseas eliminar este empleado? (S/N): ");

	        if (confirmacion.equalsIgnoreCase("S")) {
	            //Busco el departamento en la base de datos y elimino al empleado de la lista del departamento
	            IQuery consultaDepartamento = new CriteriaQuery(Departamento.class);
	            Objects<Departamento> listaDepartamentos = odb.getObjects(consultaDepartamento);

	            while (listaDepartamentos.hasNext()) {
	                Departamento departamentoDondeEstaElEmpleado = listaDepartamentos.next();
	                
	                if (departamentoDondeEstaElEmpleado.getEmpleados().contains(empleadoAEliminar)) {
	                	departamentoDondeEstaElEmpleado.borrarEmpleado(empleadoAEliminar);
	                    odb.store(departamentoDondeEstaElEmpleado); // Guardo los cambios en el departamento
	                    System.out.println("Empleado eliminado del departamento: " + departamentoDondeEstaElEmpleado.getNombre());
	                }
	            }
	            
	            //Elimino al empleado de la base de datos Neodatis
	            odb.delete(empleadoAEliminar);
	            odb.commit();
	            System.out.println("Empleado eliminado con éxito.");
	        } else {
	            System.out.println("Operación cancelada.");
	        }
	    }
	}
	
	
	/**
	 * Solicita al usuario ingresar una cadena de carcateres (String).
	 * 
	 * @param teclado Objeto Scanner utilizado para leer el número entero escrito por el usuario.
	 * @param mensaje Mensaje de texto que se mostrará al usuario solicitando el dato de tipo String.
	 * @return Un String con la cadena proporcionada por el usuario.
	 * @throws NoSuchElementException Si no se encontró ninguna cadena tras la solicitud.
	 */
	public static String solicitarCadena(Scanner teclado, String mensaje) throws NoSuchElementException {
		String cadenaIngresada;
		
        do {
        	System.out.print(mensaje);
        	cadenaIngresada = teclado.nextLine().trim();
			
        	if(cadenaIngresada.isEmpty()) {
        		System.out.println("\tLa cadena no puede estar vacía.");
        	}
        	
        }while((cadenaIngresada.isEmpty()));
        		
        return cadenaIngresada;
    }
	
	
	/**
	 * Solicita al usuario ingresar un número entero positivo de tipo primitivo.
	 * 
	 * @param teclado Objeto Scanner utilizado para leer el número entero escrito por el usuario.
	 * @param mensaje Mensaje de texto que se mostrará al usuario solicitando el dato de tipo primitivo int.
	 * @return Devuelve el valor entero si se cumplen las condiciones.
	 * @throws InputMismatchException Si el usuario ingresa cualquier dato distinto al solicitado.
	 */
    private static int solicitarEntero(Scanner teclado, String mensaje) {
        int numero;
        do {
            System.out.print(mensaje);
            while (!teclado.hasNextInt()) {
                System.out.println("Debe ingresar un número entero positivo.");
                teclado.next();
            }
            numero = teclado.nextInt();
        } while (numero < 0);
        teclado.nextLine();
        return numero;
    }
}

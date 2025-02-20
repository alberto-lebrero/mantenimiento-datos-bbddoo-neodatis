package aplicacion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;

import definicion.Departamento;
import definicion.Empleado;
import definicion.Empleo;

/**
 * Clase que comprueba el correcto funcionamiento de la aplicación.
 * 
 * @author Alberto Lebrero Pizarro - 2º DAM - CEU
 * @version 1.3
 */
public class ProgramaPrincipal {
	private static String NOMBRE_FICHERO_BBDDOO = "bbddoo.p6.neodatis";

	public static void main(String[] args) {
		System.out.println("PRÁCTICA 6: MANTENIMIENTO DE DATOS EN BASES DE DATOS ORIENTADAS A OBJETOS (BB.DD.OO.)");

		boolean salirPrograma = false;
		Scanner teclado = new Scanner(System.in);

		while(!salirPrograma) {
			String nombreDeUsuario = MantenimientoDatos.solicitarCadena(teclado, "Introduce un usuario: ");
			String contrasennaDeUsuario = MantenimientoDatos.solicitarCadena(teclado, "Introduce una contraseña: ");

			try {
				Class.forName("oracle.jdbc.OracleDriver");
				
				//Obtengo la conexión a la BBDD de Oracle
				try(Connection conexion = DriverManager.getConnection
	                    ("jdbc:oracle:thin:"+nombreDeUsuario+"/"+contrasennaDeUsuario+"@//localhost:1521/XE")) {
					System.out.println("-----------------------------------------------------");
					System.out.println("Conectado con usuario " + nombreDeUsuario + " y contraseña " + contrasennaDeUsuario);
					System.out.println("-----------------------------------------------------");
					
					ODB odb = ODBFactory.open(NOMBRE_FICHERO_BBDDOO); 

					//Cargo los empleos, departamentos y empleados de la BBDD Oracle a la BBDDOO Neodatis.
					cargarEmpleos(conexion, odb);
					cargarDepartamentos(conexion, odb);
					cargarEmpleados(conexion, odb);
					
					
					boolean salirMenuPrincipal = false;
					while(!salirMenuPrincipal) {
						menuPrincipal();
						MantenimientoDatos mantenimiento = new MantenimientoDatos();
						
						int opcionEscogida = escogerOpcion("Escoge una opción: ", 1, 4);
						
						switch(opcionEscogida) {
							case 1:
								mantenimiento.consultarDepartamento(odb);
								break;
							case 2:
								mantenimiento.modificarSalarioEmpleado(odb);
								break;
							case 3:
								mantenimiento.borrarEmpleado(odb);
								break;
							case 4:
								salirPrograma = true;
								salirMenuPrincipal = true;
								teclado.close();
								conexion.close();
								
								if(odb != null) {
									odb.close();
								}
								System.out.println("Hasta pronto!");
								break;
						}
					}
					
				}
				
			} catch (ClassNotFoundException cNFE) {
				System.out.println("Error al cargar el driver JDBC de Oracle.\n");
			} catch (SQLException sqlE) {
				sqlE.printStackTrace();
				System.out.println("Usuario y contraseña erróneos.\n");

			}
		}
		
	}
	
	private static void menuPrincipal() {
		System.out.println("******************************************************************");
		System.out.println("* 1. Visualizar los datos de los empleados de un departamento.   *");
		System.out.println("* 2. Modificar el salario de un empleado.                        *");
		System.out.println("* 3. Borrar a un empleado de la BBDD.                            *");
		System.out.println("* 4. Salir.                                                      *");
		System.out.println("******************************************************************");
	}
	
	
	/**
	 * Carga todos los empleos de la tabla JOBS de la base de datos de Oracle a la BBDDOO Neodatis.
	 * 
	 * @param conexion Conexión a la base de datos Oracle sobre la cual sacaré la información de los empleos.
	 * @param odb Objeto de la base de datos de Neodatis sobre la cual se alamcenarán los objetos de los empleos.
	 * @throws SQLException Si ocurre algún error de acceso a la base de datos Oracle tras realizar la consulta.
	 */
	private static void cargarEmpleos(Connection conexion, ODB odb) throws SQLException {
		Statement sentenciaEmpleos = conexion.createStatement();
		ResultSet tablaTemporalEmpleos = sentenciaEmpleos.executeQuery("SELECT JOB_ID, JOB_TITLE FROM JOBS");
		
		while(tablaTemporalEmpleos.next()) {
			Empleo empleoACargarEnNeodatis = new Empleo(tablaTemporalEmpleos.getString("JOB_ID"), tablaTemporalEmpleos.getString("JOB_TITLE"));
			odb.store(empleoACargarEnNeodatis);
		}
		
		System.out.println("-- Empleos insertados --");
		
		//Libero los recursos utilizados en el orden de creación
		tablaTemporalEmpleos.close();
		sentenciaEmpleos.close();
	}
	
	/**
	 * Carga todos los departamentos de la tabla DEPARTMENTS de la base de datos de Oracle a la BBDDOO Neodatis.
	 * 
	 * @param conexion Conexión a la base de datos Oracle sobre la cual sacaré la información de los departamentos.
	 * @param odb Objeto de la base de datos de Neodatis sobre la cual se alamcenarán los objetos de los departamentos.
	 * @throws SQLException Si ocurre algún error de acceso a la base de datos Oracle tras realizar la consulta.
	 */
	private static void cargarDepartamentos(Connection conexion, ODB odb) throws SQLException {
		Statement sentenciaDepartamentos = conexion.createStatement();
		ResultSet tablaTemporalDepartamentos = sentenciaDepartamentos.executeQuery("SELECT DEPARTMENT_ID, DEPARTMENT_NAME FROM DEPARTMENTS");
		
		while(tablaTemporalDepartamentos.next()) {
			Departamento departamentoACargarEnNeodatis = new Departamento(tablaTemporalDepartamentos.getString("DEPARTMENT_ID"), 
																			tablaTemporalDepartamentos.getString("DEPARTMENT_NAME"));
			odb.store(departamentoACargarEnNeodatis);
		}
		
		System.out.println("-- Departamentos insertados --");

		//Libero los recursos utilizados en el orden de creación
		tablaTemporalDepartamentos.close();
		sentenciaDepartamentos.close();
	}
	
	
	/**
	 * Carga todos los empleados de la tabla EMPLOYEES de la base de datos de Oracle a la BBDDOO Neodatis.
	 * 
	 * @param conexion Conexión a la base de datos Oracle sobre la cual sacaré la información de los empleados.
	 * @param odb Objeto de la base de datos de Neodatis sobre la cual se alamcenarán los objetos de los empleados.
	 * @throws SQLException Si ocurre algún error de acceso a la base de datos Oracle tras realizar la consulta.
	 */
	public static void cargarEmpleados(Connection conexion, ODB odb) throws SQLException {
        Statement sentenciaEmpleados = conexion.createStatement();
		ResultSet tablaTemporalEmpleados = sentenciaEmpleados.executeQuery("SELECT EMPLOYEE_ID, FIRST_NAME||' '|| LAST_NAME AS NOMBRE, "
																		 + "SALARY, JOB_ID, DEPARTMENT_ID "
																		 + "FROM EMPLOYEES");

        while (tablaTemporalEmpleados.next()) {
        	String idEmpleado = tablaTemporalEmpleados.getString("EMPLOYEE_ID");
			String nombreEmpleado = tablaTemporalEmpleados.getString("NOMBRE");
			float salarioEmpleado = tablaTemporalEmpleados.getFloat("SALARY");
			String idEmpleo = tablaTemporalEmpleados.getString("JOB_ID");
			String idDepartamento = tablaTemporalEmpleados.getString("DEPARTMENT_ID");

            //Buco el empleo (JOB) en la BBDDOO Neodatis
            IQuery consultaEmpleos = new CriteriaQuery(Empleo.class, Where.equal("codigo", idEmpleo));
            Objects<Empleo> listaEmpleos = odb.getObjects(consultaEmpleos);
            Empleo empleoACargar = listaEmpleos.hasNext() ? listaEmpleos.next() : null;
            
            if (empleoACargar != null) {
            	//Busco el departamento en la BBDDOO Neodatis
                IQuery consultaDepartamento = new CriteriaQuery(Departamento.class, Where.equal("codigo", String.valueOf(idDepartamento)));
                Objects<Departamento> listaDepartamentos = odb.getObjects(consultaDepartamento);
                Departamento departamentoAActualizar = listaDepartamentos.hasNext() ? listaDepartamentos.next() : null;
                
                if (departamentoAActualizar != null) {
                	 //Creo el objeto Empleado y lo añado a la lista de departamentos
                    Empleado empleadoACargar = new Empleado(idEmpleado, nombreEmpleado, salarioEmpleado, empleoACargar);
                    departamentoAActualizar.annadirEmpleado(empleadoACargar);
                    
                    //Guardo al empleado y actualizo la lista de empleados en el departamento en la BBDDOO Neodatis
                    odb.store(empleadoACargar);
                    odb.store(departamentoAActualizar);
                }
            }
        }
		System.out.println("-- Empleados insertados --");

		//Libero los recursos utilizados en el orden de creación
        tablaTemporalEmpleados.close();
        sentenciaEmpleados.close();
	}
	
	
	/**
	 * Escoge la opción el que el usuario selecciona por teclado y realiza la 
	 * operación pertinente. Si el usuario escoge una opción no válida mostrará 
	 * un mensaje de error por pantalla.
	 * 
	 * @param mensajeParaSolicitarDato Mensaje que se imprimirá por pantalla para solicitar el dato al usuario.
	 * @param minValor Mínimo valor a ser seleccionado.
	 * @param maxValor Máximo valor a ser seleccionado.
	 * 
	 * @throws InputMismatchException Si el token recuperado no coincide con el patrón del tipo 
	 * 		   esperado (int).
	 * @throws InputMismatchException
	 */
	private static int escogerOpcion(String mensajeParaSolicitarDato, int minValor, int maxValor) throws InputMismatchException {
		int opcionEscogida = 0;
		@SuppressWarnings("resource")
		Scanner teclado = new Scanner(System.in);;
		
		do {
			System.out.print(mensajeParaSolicitarDato);
			opcionEscogida = teclado.nextInt();
			
			//Limpio el buffer de entrada
			teclado.nextLine();
			
			if((opcionEscogida < minValor) || (opcionEscogida > maxValor)) {
				System.out.println("Opción incorrecta. Por favor, seleccione una opción válida.\n");
			}
		} while((opcionEscogida < minValor) || (opcionEscogida > maxValor));
				
		return opcionEscogida;
	}

}

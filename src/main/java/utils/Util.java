package utils;

import connections.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import models.*;


public class Util {

	/**
	 * Este método se utiliza para realizar la lógica del LoginServlet. Comprueba si el usuario existe, en tal caso si
	 * la contraseña coincide con el correo y finalmente guarda algunos datos fundamentales.
	 * @author Ricardo
	 * @param req
	 * @param session
	 * @return Booleano si ha conseguido acceder.
	 */
    public static boolean loginProcess(HttpServletRequest req, HttpSession session) {
        boolean landing = false;
        Conector conector = new Conector();
        Connection con = null;
		String error = "";

        try {
            con = conector.getMySqlConnection();
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM credentials WHERE email = ?");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String hashedPassFromDB = resultSet.getString("pass");
                if(BCrypt.checkpw(password, hashedPassFromDB)) {
                    User user = createUser(con, resultSet);
                    session.setAttribute("user", user);
                    landing = true;
                }else
		    		error = "Contraseña incorrecta";
            } else
				error = "El usuario no existe";
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
        }

		if (!error.isEmpty())
			session.setAttribute("error",error);
		return landing;
    }

	/**
	 * Método utilizado para crear el susuario que hace login.
	 * @author Ricardo
	 * @param con
	 * @param resultSet
	 * @return Devuelve el usuario.
	 * @throws SQLException
	 */
    public static User createUser(Connection con, ResultSet resultSet) throws SQLException {
		User user = new User();

		if (con != null) {
			user.setId(resultSet.getInt("id"));
			PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM user_obj WHERE id = ?");
			preparedStatement.setInt(1, user.getId());
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				user.setUserType(resultSet.getString("user_type"));
				user.setName(resultSet.getString("user_name"));
				user.setId_school(resultSet.getInt("school_id"));
				user.setId_course((Integer) resultSet.getObject("course_id"));
			}
		}

        return user;
    }

	/**
	 * Inserta un registro nuevo en la tabla de Credentials.
	 * @author Ricardo
	 * @param con
	 * @param userEmail	email que se recoge en el registro.
	 * @param userPass	contraseña que se recoge en el registro.
	 */
    public static void insertCredentials(Connection con, String userEmail, String userPass) {
		// No cerramos la conexión porque este metodo se utiliza dentro de otro que si la cierra
		if (con != null) {
			try (PreparedStatement ps = con.prepareStatement("call insertUserCredentials(?,?)")) {
				ps.setString(1, userEmail);
				ps.setString(2, userPass);
				int linesModified = ps.executeUpdate();
				if (linesModified > 0)
					System.out.println("Query ejecutada con éxito!");
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
    }

	/**
	 * Inserta un usuario nuevo en la BD.
	 * @author Ricardo
	 * @param con
	 * @param name	nombre recogido en el registro.
	 * @param lastName	apellidos recogidos en el registro.
	 * @param birthDate	fecha de nacimiento recogida en el registro.
	 * @param dnie	DNI o NIE recogido en el registro.
	 * @param email email recogido en el registro.
	 * @param pass	contraseña recogida en el registro.
	 * @param schoolId	ID del colegio seleccionado en el registro.
	 * @param courseId	ID del módulo seleccionado en el registro.
	 */
    public static void insertUserInDb(Connection con, String name, String lastName, String birthDate, String dnie, String email, String pass, String schoolId, String courseId) {
		// No cerramos la conexión porque este metodo se utiliza dentro de otro que si la cierra
		if (con != null) {
			try {
				PreparedStatement ps = con.prepareStatement("call insertUser(?,?,?,?,?,?,?,?,?);");
				ps.setString(1, name);
				ps.setString(2, lastName);
				ps.setString(3, birthDate);
				ps.setString(4, dnie);
				ps.setString(5, "01");
				ps.setString(6, email);
				ps.setString(7, pass);
				// Preguntarnos si los ids la mejor forma de tratarlos sería con Strings y no ints.
				ps.setInt(8, Integer.parseInt(schoolId));
				ps.setInt(9, Integer.parseInt(courseId));
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    }

	/**
	 * Método utilizado para ver si un correo existe en la BD.
	 * @author Ricardo
	 * @param con
	 * @param email email que se quiere comprobar.
	 * @return devuelve un boolean en función de si existe o no el email.
	 */
    public static boolean checkIfEmailExist(Connection con, String email) {
        boolean res = false;

		// No cerramos la conexión porque este metodo se utiliza dentro de otro que si la cierra
		if (con != null) {
			try {
				PreparedStatement ps = con.prepareStatement("select * from credentials where email=?;");
				ps.setString(1, email);
				try {
					ResultSet rs = ps.executeQuery();
					res = rs.next();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

        return res;
    }

	/**
	 * Método para comprobar si un DNI o NIE existe en la BD.
	 * @author Ricardo
	 * @param con
	 * @param dnie
	 * @return	Devuelve un booleano en función de si existe el DNIE o no.
	 */
    public static boolean checkIfDnieExist(Connection con, String dnie) {
        boolean res = false;

		if (con != null) {
			try {
				PreparedStatement ps = con.prepareStatement("select * from user_obj where dnie=?;");
				ps.setString(1, dnie);
				try {
					ResultSet rs = ps.executeQuery();
					res = rs.next();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

        return res;
    }

	/**
	 *	Método utilizado para guardar la información necesaria en la tarjeta del usuario en el index.
	 * @author Ricardo
	 * @param activeUser
	 * @param centroUsuario
	 * @param crs
	 * @return	String con la información de dicho usuario.
	 */
	public static String getContentTarjetaIndex(User activeUser, String centroUsuario, Course crs) {
		String result = "";

		switch (activeUser.getUserType()) {
			case "01" -> {result = "Alumno de " + crs.getNameCourse();}
			case "02" -> {result = "Profesor " + centroUsuario;}
			case "03" -> {result = "Empleado/a de Accenture";}
		}

		return result;
	}

	/**
	 * Método utilizado en el datosPersonales.jsp devuelve los datos del usuario y los utiliza para imprimirlos.
	 * @author Ricardo
	 * @param id
	 * @return	List con los datos del alumno ordenados.
	 */
	public static List<String> getUserInfo(int id) {
		List<String> usuario = new ArrayList<>();
		Connection con = null;

		try {
			con = new Conector().getMySqlConnection();
			if (con != null) {
				PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM user_obj WHERE id = ?");
				preparedStatement.setInt(1, id);
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					usuario.add(resultSet.getString("user_name"));
					usuario.add(resultSet.getString("user_surname"));
					usuario.add(resultSet.getString("dnie"));
					usuario.add(resultSet.getString("birthDate"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return usuario;
	}
	
	/**
	 * Método que formatea la fecha tipo aaaa-dd-mm a dd/mm/aaaa
	 * Sólo funciona si la fecha es tipo aaaa-dd-mm
	 * @author Óscar
	 * @param date - fecha a cambiar
	 * @return fecha cambiada
	 */
	public static String dateFormat(String date) {
		String fecha = "";
		String año = date.substring(0,4);
		String mes = date.substring(5, 7);
		String dia = date.substring(8,10);

		fecha = dia + "/" + mes + "/" + año;

		return fecha;
	}

	/**
	 * Método utilizado para poder definir la imagen correspondiente a cada centro
	 * @author Ricardo
	 * @param id ID del centro del usuario
	 * @return String con el link de la ruta de la imagen
	 */
    public static String defineImageIndex(Integer id){
    	String imagen = "";

    	switch(id) {
			case 1 -> {imagen = "./images/logos/LOGOTIPO-CESUR.png";}
			case 2 -> {imagen = "./images/logos/LOGOTIPO-IES-PABLO-PICASSO.png";}
			case 3 -> {imagen = "./images/logos/LOGOTIPO-IES-BELEN.png";}
			case 4 -> {imagen = "./images/logos/LOGOTIPO-ALAN-TURING.png";}
			case 5 -> {imagen = "./images/logos/LOGOTIPO-IES-SAN-JOSE.png";}
			default -> {imagen = "./images/logos/LOGOTIPO-ACCENTURE.png";}
		}

    	return imagen;
    }

	/**
	 * Método utilizado para definir la ruta al JSP de las noticias de cada centro
	 * @author Ricardo
	 * @param id
	 * @return	String con la ruta
	 */
	public static String defineID(int id){
    	String nombre="";

    	switch(id){
			case 1->{nombre="./jsp/noticiasCesur.jsp";}
			case 2->{nombre="./jsp/noticiasPabloPicasso.jsp";}
			case 3->{nombre="./jsp/noticiasBelen.jsp";}
			case 4->{nombre="./jsp/noticiasAlanTuring.jsp";}
			case 5->{nombre="./jsp/noticiasSanJose.jsp";}
    	}

    	return nombre;
    }

	/**
	 * Método utilizado para crear un objeto School a partir de un ID
	 * @author Ricardo
	 * @param idSchool
	 * @return School con los datos correspondientes al ID del parámetro
	 */

	// Este método me gustaría cambiarle el nombre a createSchool
	public static School getInfoSchool(int idSchool){
		int idSchoolConstr=idSchool;
		String nombreSchool="";
		String tlfSchool= "";
		String email= "";
		String scheduleSchool= "";
		String locSchool="";
		Conector conector = new Conector();
		Connection con = null;

		try {
			con = conector.getMySqlConnection();
			if (con != null) {
				String sql = "SELECT * FROM school where id="+idSchool;
				Statement sentencia = con.createStatement();

				try (ResultSet rs = sentencia.executeQuery(sql)) {
					while (rs.next()) {
						idSchoolConstr=rs.getInt(1);
						nombreSchool=rs.getString(2);
						tlfSchool= rs.getString(3);
						email= rs.getString(4);
						scheduleSchool= rs.getString(5);
						locSchool=rs.getString(6);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
        }

		return new School(idSchoolConstr, nombreSchool, tlfSchool, email, scheduleSchool, locSchool);
	}

	/**
	 * Método utilizado para crear un Course a partir de un ID
	 * @author Ricardo
	 * @param idCourse
	 * @return Course correspondiente al ID introducido por parámetro
	 */
	public static Course getCourseInfo(int idCourse){
		Course course = new Course();
		Conector conector = new Conector();
		Connection con = null;

		try {
			con = conector.getMySqlConnection();
			if (con != null) {
				PreparedStatement sentencia = con.prepareStatement("SELECT * FROM course where id="+idCourse);
				ResultSet rs = sentencia.executeQuery();
				if (rs.next()) {
					course.setId_course(idCourse);
					course.setNameCourse(rs.getString(2));
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
		}

		return course;
	}

	/**
	 * Método utilizado para definir la ruta del mapa de un centro a partir de su ID
	 * @author Ricardo
	 * @param idSchool
	 * @return	String con la ruta del mapa del centro de Google Maps del correspondiente ID
	 */
	public static String defineMap(int idSchool) {
    	String mapLink = "";
    	switch(idSchool) {
			case 1 -> {mapLink="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d10757.374130614628!2d-4.372041717464043!3d36.71808277803187!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0xd7259120bfc4db3%3A0xec0ecedd8dc61902!2sCESUR%20M%C3%A1laga%20Este%20Formaci%C3%B3n%20Profesional!5e0!3m2!1ses!2ses!4v1715334512514!5m2!1ses!2ses";}
			case 2 -> {mapLink="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d6395.717928363966!2d-4.455162806420868!3d36.725948300000006!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0xd72f70c3d574e37%3A0x67343146876c734b!2sIES%20Pablo%20Picasso!5e0!3m2!1ses!2ses!4v1715335018709!5m2!1ses!2ses";}
			case 3 -> {mapLink="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3199.124902411609!2d-4.459761523439527!3d36.69553637227712!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0xd72f9dee2ea3131%3A0xe00a7d745fb8b2e3!2sIES%20Bel%C3%A9n!5e0!3m2!1ses!2ses!4v1715335056310!5m2!1ses!2ses";}
			case 4 -> {mapLink="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3197.2394272377824!2d-4.554430616275409!3d36.740823696739334!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0xd72f10963ce0f3d%3A0x310ae7d4bb2e8f7b!2sCPIFP%20Alan%20Turing!5e0!3m2!1ses!2ses!4v1715335096355!5m2!1ses!2ses";}
			case 5 -> {mapLink="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d60854.997797710945!2d-4.459410649332534!3d36.715431468654785!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0xd72f711c56e8bed%3A0x6de2361e88593aeb!2sColegio%20Diocesano%20San%20Jos%C3%A9%20Obrero!5e0!3m2!1ses!2ses!4v1715335137121!5m2!1ses!2ses";}
    	}
    	return mapLink;
    }

	/**
	 * Método utilizado para enseñar todos los centros en el registro
	 * @author Ricardo
	 * @return	List con todos los School
	 */
	public static List<School> getAllSchools() {
		List<School> schools = new ArrayList<>();
		Connection con = null;

		try {
			con = new Conector().getMySqlConnection();
			try {
				PreparedStatement ps = con.prepareStatement("select * from school");
				ResultSet result = ps.executeQuery();
				while (result.next()) {
					int id = result.getInt("id");
					String name = result.getString("school_name");
					String telephone = result.getString("tel");
					String email = result.getString("email");
					String secretarySchedule = result.getString("secretarySchedule");
					String location = result.getString("loc");
					schools.add(new School(id, name, telephone, email, secretarySchedule, location));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return schools;
	}

	/**
	 * Método utilizado para mostrar los cursos en el registro
	 * @author Ricardo
	 * @return	List con los Courses
	 */
	public static List<Course> getAllCourses() {
		List<Course> courses = new ArrayList<>();
		Connection con = null;

		try {
			con = new Conector().getMySqlConnection();
			try {
				PreparedStatement ps = con.prepareStatement("select id, course_name from course;");
				ResultSet result = ps.executeQuery();
				while (result.next()) {
					int id = result.getInt("id");
					String name = result.getString("course_name");
					courses.add(new Course(id, name));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return courses;
	}
}

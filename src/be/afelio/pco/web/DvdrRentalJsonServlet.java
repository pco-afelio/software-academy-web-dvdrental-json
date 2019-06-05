package be.afelio.pco.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import be.afelio.software_academy.pco.jdbc.exercises.dvdrental.DvdRentalJdbcRepository;
import be.afelio.software_academy.pco.jdbc.exercises.dvdrental.beans.Actor;
import be.afelio.software_academy.pco.jdbc.exercises.dvdrental.beans.Film;
import be.afelio.software_academy.pco.jdbc.exercises.dvdrental.impl.DvdRentalJdbcRepositoryImpl;

/*
 	<servlet>
 		<servlet-name>JSON</servlet-name>
 		<servlet-class>be.afelio.pco.web.DvdrRentalJsonServlet</servlet-class>
 	</servlet>
 	<servlet-mapping>
 		<servlet-name>JSON</servlet-name>
 		<url-pattern>/json</url-pattern>
 	</servlet-mapping>
 */
@WebServlet("/json/*")
public class DvdrRentalJsonServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected DvdRentalJdbcRepository repository;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("DvdrRentalJsonServlet.init()");
		try {
			Class.forName("org.postgresql.Driver");
			// where is "database.properties"  ?
			String path = getServletContext().getRealPath("/WEB-INF/database.properties");
			System.out.println("Path = " + path);
			
			// load configuration properties from file
			Properties properties = new Properties();
			try (
					InputStream in = new FileInputStream(path);
			) {
				properties.load(in);
			}
			String url = properties.getProperty("database.url");
			String user = properties.getProperty("database.user");
			String password = properties.getProperty("database.password");
			
			// instanciate repository
			repository = new DvdRentalJdbcRepositoryImpl(url, user, password);
		} catch(Exception e) {
			throw new ServletException(e);
		}
	}

	// GET /dvdrental/json/customer/Mary/Smith
	// => { "email": "mary.smith@sakilacustomer.org" }
	// ou
	// GET /dvdrental/json/film/Grosse Wonderful
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		System.out.println("DvdrRentalJsonServlet.doGet() => " + pathInfo);
		
		if (pathInfo.startsWith("/customer/")) {
			String[] parts = pathInfo.split("/");
			// System.out.println(java.util.Arrays.toString(parts));
			String firstname = parts[2];
			String name = parts[3];
			String email = repository.findOneCustomerEmailByCustomerFirstNameAndName(firstname, name);
			
			// generate JSON
			// { "email": "mary.smith@sakilacustomer.org" }
			String json = String.format("{ \"email\": \"%s\" }", email);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
			
		} else if (pathInfo.startsWith("/film/")) {
			int index = pathInfo.lastIndexOf("/");
			String title = pathInfo.substring(index + 1);
			Film film = repository.findOneFilmByTitle(title);
			
			// generate JSON with jackson library
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(film);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		} else {
			response.setStatus(404);
		}
	}

	// POST /dvdrental/json/actor
	// in: 		{ "firstname": "Betty", "name": "Boop" }
	// out: 	{ "id": 123,  "firstName": "Betty", "lastName": "Boop" }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("DvdrRentalJsonServlet.doPost()");
		// get Object from json string
		ObjectMapper mapper= new ObjectMapper();
		CreateActorParameters parameters 
			= mapper.readValue(request.getInputStream(), CreateActorParameters.class);
		System.out.println(parameters);
		
		repository.createAndStoreNewActor(parameters.firstname, parameters.name);
		Actor actor = null;
		List<Actor> list = repository.findAllActorsByActorFirstNameIgnoreCase(parameters.firstname);
		for (int i = 0; actor == null && i < list.size(); i++) {
			Actor candidate = list.get(i);
			if (candidate.getLastName().equalsIgnoreCase(parameters.name)) {
				actor = candidate;
			}
		}
		
		// generate JSON 
		String json = mapper.writeValueAsString(actor);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	// PUT /dvdrental/json/film
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("DvdrRentalJsonServlet.doPut()");
		
		// get Java object from JSON
		ObjectMapper mapper = new ObjectMapper();
		CreateFilmParameters p = mapper.readValue(request.getInputStream(), CreateFilmParameters.class);
		
		Film film = repository.createAndStoreNewFilm(p.getTitle(), p.getDescription(), 
				p.getReleaseYear(), p.getLanguageName(), p.getLength(), p.getActorIds());
		
		// generate JSON 
		String json = mapper.writeValueAsString(film);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	// DELETE /dvdrental/json/category/Comic Book movie
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("DvdrRentalJsonServlet.doDelete()");
		int index = request.getPathInfo().lastIndexOf("/");
		String name = request.getPathInfo().substring(index+1);
		
		boolean deleted = repository.deleteCategoryByName(name);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write( String.format("{\"deleted\": %s}", deleted) );
		
	}

}

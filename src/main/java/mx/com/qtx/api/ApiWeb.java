package mx.com.qtx.api;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import mx.com.qtx.entidades.Saludo;

@RestController
public class ApiWeb {
	
	private static Logger log = LoggerFactory.getLogger(ApiWeb.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private Environment env;
	
	private String urlProv = null;
	
	@PostConstruct
	public void inicializarUrlCte() {
		this.urlProv = "http://" + env.getProperty("mx.com.qtx.prov01") 
		              + ":" + env.getProperty("mx.com.qtx.prov01.port");
	}
	
	@GetMapping(path = "/testServicio", produces = MediaType.APPLICATION_JSON_VALUE)
	public Saludo probarServicio() {
		String Url =  this.urlProv + "/saludo/json/{nombre}";
		
		Saludo saludo =this.restTemplate.getForObject(Url, Saludo.class, "Jimena");
		
		log.debug("objeto recuperado de " + Url + " es " + saludo);
		return saludo;
	}

	@GetMapping(path = "/testArreglo", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Saludo> probarGetArreglo() {
		
		String Url =  this.urlProv + "/saludos";
		
		Saludo[] saludos =this.restTemplate.getForObject(Url, Saludo[].class);
		
		List<Saludo> listSaludos = Arrays.asList(saludos);
		log.debug("objeto recuperado de " + Url + " es " + listSaludos);
		return listSaludos;
	}
	
	@GetMapping(path = "/testArregloErr", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Saludo> probarGetArregloErr() {
		
		List<Saludo> listSaludos = null;
		String Url =  this.urlProv + "/saludos";
		try {
			Saludo[] saludos =this.restTemplate.getForObject(Url, Saludo[].class);
			listSaludos = Arrays.asList(saludos);		
			log.debug("objeto recuperado de " + Url + " es " + listSaludos);
		}
		catch(RestClientResponseException rcrex) {
			Saludo saludoErr = new Saludo("error", rcrex.getResponseBodyAsString());
			listSaludos.add(saludoErr);
		}		
		return listSaludos;
	}
	
	@ExceptionHandler
	public String manejarError(Exception ex) {
		String error = ex.getClass().getName() + ":" + ex.getMessage();
		return error;		
	}
}

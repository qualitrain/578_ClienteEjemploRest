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

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import mx.com.qtx.entidades.Saludo;

@RestController
public class ApiWeb {
	
	private static Logger log = LoggerFactory.getLogger(ApiWeb.class);
	
//	@Autowired
//	private EurekaClient eurekaCte;	
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private Environment env;
	
	private String getUrlProv(){
		String idServicioProv = env.getProperty("mx.com.qtx.prov01");
		String urlProv = "http://" + idServicioProv;
		return urlProv;
	}
	
	@GetMapping(path = "/testServicio", produces = MediaType.APPLICATION_JSON_VALUE)
	public Saludo probarServicio() {
		String Url =  this.getUrlProv() + "/saludo/json/{nombre}";
		
		Saludo saludo =this.restTemplate.getForObject(Url, Saludo.class, "Jimena");
		
		log.debug("objeto recuperado de " + Url + " es " + saludo);
		return saludo;
	}

	@GetMapping(path = "/testArreglo", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Saludo> probarGetArreglo() {
		
		String Url =  this.getUrlProv() + "/saludos";
		
		Saludo[] saludos =this.restTemplate.getForObject(Url, Saludo[].class);
		
		List<Saludo> listSaludos = Arrays.asList(saludos);
		log.debug("objeto recuperado de " + Url + " es " + listSaludos);
		return listSaludos;
	}
	
	@GetMapping(path = "/testArregloErr", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Saludo> probarGetArregloErr() {
		
		List<Saludo> listSaludos = null;
		String Url =  this.getUrlProv() + "/saludos";
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
	
	@GetMapping(path = "/testCircuitBreaker", produces = MediaType.APPLICATION_JSON_VALUE)
	@CircuitBreaker(name="MiCircuitBreaker", fallbackMethod = "fallback")
	public List<Saludo> probarGetArregloInestable() {
		log.trace("probarGetArregloInestable()");
		
		String Url =  this.getUrlProv() + "/saludos";
		
		Saludo[] saludos =this.restTemplate.getForObject(Url, Saludo[].class);
		
		List<Saludo> listSaludos = Arrays.asList(saludos);
		log.debug("objeto recuperado de " + Url + " es " + listSaludos);
		return listSaludos;
	}
	
	public List<Saludo> fallback(CallNotPermittedException e) {
		log.trace("fallback()");
		return List.of(new Saludo("Lo siento","servicio lento",""));
	}
	
	@ExceptionHandler
	public String manejarInvocacionFallida(CallNotPermittedException cnpEx) {
		String error = cnpEx.getClass().getName() + ":" + cnpEx.getMessage();
		error += " causado por " + cnpEx.getCausingCircuitBreakerName();
		return error;		
		
	}
	
	@ExceptionHandler
	public String manejarError(Exception ex) {
		String error = ex.getClass().getName() + ":" + ex.getMessage();
		if(ex.getCause() != null) {
			error += " causado por " + ex.getCause().getClass().getName() + ":" + ex.getCause().getMessage();
		}
		return error;		
	}
}

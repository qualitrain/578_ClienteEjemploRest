package mx.com.qtx.entidades;

public class Saludo {
	private String saludo;
	private String nombre;
	private String logMonitoreo;
	
	public Saludo() {
		super();
	}

	public Saludo(String saludo, String nombre, String logMonitoreo) {
		super();
		this.saludo = saludo;
		this.nombre = nombre;
		this.logMonitoreo = logMonitoreo;
	}

	public Saludo(String saludo, String nombre) {
		super();
		this.saludo = saludo;
		this.nombre = nombre;
	}

	public String getSaludo() {
		return saludo;
	}

	public void setSaludo(String saludo) {
		this.saludo = saludo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getLogMonitoreo() {
		return logMonitoreo;
	}

	public void setLogMonitoreo(String logMonitoreo) {
		this.logMonitoreo = logMonitoreo;
	}

	@Override
	public String toString() {
		return "Saludo [saludo=" + saludo + ", nombre=" + nombre + ", logMonitoreo=" + logMonitoreo + "]";
	}

}

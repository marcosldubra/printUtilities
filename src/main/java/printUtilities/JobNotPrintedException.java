package printUtilities;

@SuppressWarnings("serial")
public class JobNotPrintedException extends Exception {

	public JobNotPrintedException() {
		super ("No ha sido posible imprimir el trabajo. " + "causa de la excepción pasada por parámetro");
	}
}

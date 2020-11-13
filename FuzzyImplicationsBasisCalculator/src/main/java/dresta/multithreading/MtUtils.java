package dresta.multithreading;

public class MtUtils {

	public static void mtPrintln(String x) {
		if(!Thread.currentThread().isInterrupted()) {
			System.out.println(x);
		}
	}
	
}

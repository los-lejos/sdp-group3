package dice;

import java.util.concurrent.LinkedBlockingQueue;

public class Log extends Thread {

	/*
	 * Statically-accessible
	 */
	private static Log log;
	
	public static void init() {
		log = new Log();
		log.start();
	}
	
	public static void close() {
		log.setIsRunning(false);
	}
	
	public static void logException(Exception e) {
		log.log("Exception: " + e.getMessage() + "\n");
	}
	
	public static void logError(String msg) {
		log.log(msg + "\n");
	}
	
	public static void logWarning(String msg) {
		log.log("Warning: " + msg + "\n");
	}
	
	public static void logInfo(String msg) {
		log.log(msg + "\n");
	}
	
	public static void logPrint(String msg) {
		log.log(msg);
	}
	
	/*
	 * Background thread methods
	 */
	
	private boolean isRunning = true;
	
	private LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>();
	
	public void log(String msg) {
		messageQueue.offer(msg);
	}
	
	public void setIsRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	@Override
	public void run() {
		try {
			while(this.isRunning) {
				String msg = messageQueue.take();
				System.out.print(msg);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

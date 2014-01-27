package dice.vision;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Ingvaras Merkys (based on code by sdp-group6, 2013)
 * 
 *         SocketVisionReader: Captures input from vision system.
 * 
 *         Usage: - Create an instance. - Add the listener.
 * 
 *         Contains an inner class SocketThread that will update listeners with
 *         world information.
 * 
 *         NOTE: Reader implements AbstractVisionReader
 */
public class SocketVisionReader extends Reader {

	public static final int PORT = 28541;
	public static final String ENTITY_BIT = "E";
	public static final String PITCH_SIZE_BIT = "P";
	public static final String GOAL_POS_BIT = "G";

	public SocketVisionReader() {
		new SocketThread().start();
	}

	class SocketThread extends Thread {

		@Override
		public void run() {

			try {
				ServerSocket server = new ServerSocket(PORT);

				while (true) {
					Socket socket = server.accept();

					System.out.println("Client connected.");

					Scanner scanner = new Scanner(new BufferedInputStream(
							socket.getInputStream()));

					while (scanner.hasNextLine()) {
						try {
							parse(scanner.nextLine());
						} catch (java.util.NoSuchElementException e) {
							System.out.println("No input from camera!");
						}
					}
					System.out.println("Client disconnected");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void parse(String line) {

		// Ignore Comments
		if (line.charAt(0) != '#') {

			String[] tokens = line.split(" ");

			if (tokens[0].equals(ENTITY_BIT)) {

				double x1 = Double.parseDouble(tokens[1]); // yellow x
				double y1 = Double.parseDouble(tokens[2]); // yellow y
				double d1 = Double.parseDouble(tokens[3]); // yellow angle

				double x2 = Double.parseDouble(tokens[4]); // blue x
				double y2 = Double.parseDouble(tokens[5]); // blue y
				double d2 = Double.parseDouble(tokens[6]); // blue angle

				double x3 = Double.parseDouble(tokens[7]); // ball x
				double y3 = Double.parseDouble(tokens[8]); // ball y
				// +timestamp
				propagate(x1, y1, d1, x2, y2, d2, x3, y3,
						Long.parseLong(tokens[9]));

			} else if (tokens[0].equals(PITCH_SIZE_BIT)) {

				propagatePitchSize(Double.parseDouble(tokens[1]),
						Double.parseDouble(tokens[2]));

			} else if (tokens[0].equals(GOAL_POS_BIT)) {
				// WHAT IS GOAL???
				propagateGoals(Double.parseDouble(tokens[1]),
						Double.parseDouble(tokens[2]),
						Double.parseDouble(tokens[3]),
						Double.parseDouble(tokens[4]));

			} else {

			}

		}

	}

	public static void main(String[] args) {
		SocketVisionReader svr = new SocketVisionReader();
	}

}
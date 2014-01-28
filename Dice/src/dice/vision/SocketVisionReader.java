package dice.vision;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import dice.state.RobotState;
import dice.state.WorldState;
import dice.state.Ball;

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

	private static final int PORT = 28541;
	private static final String ENTITY_BIT = "E";
	private static final String PITCH_SIZE_BIT = "P";
	private static final String GOAL_POS_BIT = "G";
	private WorldState world;

	private SocketThread thread;

	public SocketVisionReader() {
		thread = new SocketThread();
		thread.start();
	}

	public void stop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
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

				// parse robots from left to right on the screen

				// leftmost defender
				double x1 = Double.parseDouble(tokens[1]); // x
				double y1 = Double.parseDouble(tokens[2]); // y
				double d1 = Double.parseDouble(tokens[3]); // angle

                // left attacker
				double x2 = Double.parseDouble(tokens[4]); // x
				double y2 = Double.parseDouble(tokens[5]); // y
				double d2 = Double.parseDouble(tokens[6]); // angle

				// right attacker
				double x3 = Double.parseDouble(tokens[7]); // x
				double y3 = Double.parseDouble(tokens[8]); // y
				double d3 = Double.parseDouble(tokens[9]); // angle

                // rightmost defender
				double x4 = Double.parseDouble(tokens[10]); // x
				double y4 = Double.parseDouble(tokens[11]); // y
				double d4 = Double.parseDouble(tokens[12]); // angle

				// ball
				double xBall = Double.parseDouble(tokens[13]); // x
				double yBall = Double.parseDouble(tokens[14]); // y


				// +timestamp
				propagate(x1, y1, d1, x2, y2, d2, x3, y3,
						Long.parseLong(tokens[9]));

				// if this is the first time we have heard about the
				// world, initialize the world state
				if (world == null) {
					// first yellow
					RobotState opponentDefender = new RobotState(x1, y1);
					RobotState ourAttacker = new RobotState(x2, y2);
					RobotState opponentAttacker = new RobotState(x3, y3);
					RobotState ourDefender = new RobotState(x4, y4);
					Ball ball = new Ball(xBall, yBall);
					world = new WorldState(opponentDefender, ourAttacker,
					                       opponentAttacker, ourDefender,
					                       ball); // stuff
                }


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

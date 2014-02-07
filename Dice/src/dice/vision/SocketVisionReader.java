package dice.vision;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import dice.Log;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StrategyEvaluator;

/**
 * @author Ingvaras Merkys (based on code by sdp-group6, 2013)
 * @author Craig Wilkinson
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
public class SocketVisionReader {

	private static final int PORT = 28541;
	private static final String ENTITY_BIT = "E";
	private static final String PITCH_SIZE_BIT = "P";
	private static final String GOAL_POS_BIT = "G";
	
	private StrategyEvaluator strategy;
	private WorldState world;

	private SocketThread thread;

	public SocketVisionReader(WorldState world, StrategyEvaluator strategy) {
		this.strategy = strategy;
		this.world = world;
		thread = new SocketThread();
		thread.start();
	}

	public void stop() {
        thread.setIsRunning(false);
    }

	class SocketThread extends Thread {
		
		private boolean isRunning = true;
		
		public void setIsRunning(boolean isRunning) {
			this.isRunning = isRunning;
		}

		@Override
		public void run() {

			try {
				ServerSocket server = new ServerSocket(PORT);

				while (isRunning) {
					Log.logInfo("Waiting for vision to connect to socket");
					Socket socket = server.accept();

					Log.logInfo("Client connected.");

					Scanner scanner = new Scanner(new BufferedInputStream(
							socket.getInputStream()));

					while (scanner.hasNextLine() && isRunning) {
						try {
							parse(scanner.nextLine());
						} catch (java.util.NoSuchElementException e) {
							Log.logError("No input from camera!");
						}
					}
					
					Log.logInfo("Client disconnected");
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
                Vector2 firstPos = new Vector2(x1, y1);
				double d1 = Double.parseDouble(tokens[3]); // angle

                // left attacker
				double x2 = Double.parseDouble(tokens[4]); // x
				double y2 = Double.parseDouble(tokens[5]); // y
                Vector2 secondPos = new Vector2(x2, y2);
				double d2 = Double.parseDouble(tokens[6]); // angle

				// right attacker
				double x3 = Double.parseDouble(tokens[7]); // x
				double y3 = Double.parseDouble(tokens[8]); // y
                Vector2 thirdPos = new Vector2(x3, y3);
				double d3 = Double.parseDouble(tokens[9]); // angle

                // rightmost defender
				double x4 = Double.parseDouble(tokens[10]); // x
				double y4 = Double.parseDouble(tokens[11]); // y
                Vector2 fourthPos = new Vector2(x4, y4);
				double d4 = Double.parseDouble(tokens[12]); // angle

				// ball
				double xBall = Double.parseDouble(tokens[13]); // x
				double yBall = Double.parseDouble(tokens[14]); // y
                Vector2 ballPos = new Vector2(xBall, yBall);

                this.world.updateState(firstPos, d1, secondPos, d2, thirdPos, d3, fourthPos, d4, ballPos);
                this.strategy.onNewState(this.world);
			} else if (tokens[0].equals(PITCH_SIZE_BIT)) {
				double pitchWidth = Double.parseDouble(tokens[1]);
				double pitchHeight = Double.parseDouble(tokens[2]);
				
				//TODO vision sends different data from this method, so need to sync
				//world.calibratePitch(origin, first, second, third, end);
			}
			// We probably need this, but don't think vision is sending this right now
			//else if (tokens[0].equals(GOAL_POS_BIT)) {
			//	propagateGoals(Double.parseDouble(tokens[1]),
			//			Double.parseDouble(tokens[2]),
			//			Double.parseDouble(tokens[3]),
			//			Double.parseDouble(tokens[4]));
			//}

		}

	}
}

package dice.vision;

import java.util.ArrayList;
import java.util.List;

import dice.vision.Listener;

/**
 * @author Ingvaras Merkys (based on code by sdp-group6, 2013)
 */
public class Reader {

	private final List<Listener> listeners = new ArrayList<Listener>();

	/**
	 * @param listener
	 */
	public final void addListener(Listener listener) {
		listeners.add(listener);
	}

	protected final void propagate(double yPosX, double yPosY, double yRad,
			double bPosX, double bPosY, double bRad, double ballPosX,
			double ballPosY, long timestamp) {

		for (Listener listener : listeners) {
			listener.update(yPosX, yPosY, yRad, bPosX, bPosY, bRad, ballPosX,
					ballPosY, timestamp);
		}
	}

	protected final void propagatePitchSize(double width, double height) {

		for (Listener listener : listeners) {
			listener.updatePitchSize(width, height);
		}
	}

	/**
	 * Inform listeners of the position of the goals.
	 * 
	 * @param xMin
	 *            Left-hand side of the pitch.
	 * @param xMax
	 *            Right-hand side of the pitch.
	 * @param yMin
	 *            Bottom of the goals.
	 * @param yMax
	 *            Top of the goals.
	 */
	protected final void propagateGoals(double xMin, double xMax, double yMin,
			double yMax) {

		for (Listener listener : listeners) {
			listener.updateGoals(xMin, xMax, yMin, yMax);
		}
	}

}

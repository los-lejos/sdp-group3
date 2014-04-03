package robot;

import java.util.Arrays;

import lejos.nxt.ColorSensor;

public abstract class BallSensorController {
		
	private static final int BALL_SENSOR_READING_MAX = 20;
	
	private final ColorSensor ballSensor;

	private int[] readings = new int[BALL_SENSOR_READING_MAX];
	private int readingIndex = 0;
	private int numberOfReadings = 0;
	
	public BallSensorController(ColorSensor ballSensor) {
		this.ballSensor = ballSensor;
	}
	
	public abstract boolean isDetectingBallInKicker();
	public abstract boolean isBallNearby();
	
	public void takeReading() {
		readings[readingIndex] = ballSensor.getColor().getRed();
		System.out.println(readings[readingIndex]);
		readingIndex++;
		
		if(readingIndex >= BALL_SENSOR_READING_MAX) {
			readingIndex = 0;
		}
		
		numberOfReadings++;
	}
	
	public void resetMeasurements() {
		this.readingIndex = 0;
		this.numberOfReadings = 0;
	}
	
	public boolean hasEnoughReadings() {
		// Wait for a good number of readings so that any initial noise is overwritten
		return this.numberOfReadings >= BALL_SENSOR_READING_MAX * 1.5;
	}
	
	protected int getRecentReadingMedian() {
		// Could be a lot more clever with this, but if it doesn't impact perf this is simple
		int[] sortedReadings = Arrays.copyOf(this.readings, BALL_SENSOR_READING_MAX);
		Arrays.sort(sortedReadings);
		
		// Don't worry about there not being enough data to read here
		// the array will be populated very quickly upon turning on the robot
		// so it shouldn't be an issue
		int lower = BALL_SENSOR_READING_MAX / 2;
		int higher = BALL_SENSOR_READING_MAX / 2 + 1;
		return (readings[lower] + readings[higher]) / 2;
	}
	
	protected int getRecentReadingMin() {
		// Could be a lot more clever with this, but if it doesn't impact perf this is simple
		int[] sortedReadings = Arrays.copyOf(this.readings, BALL_SENSOR_READING_MAX);
		Arrays.sort(sortedReadings);
		
		return sortedReadings[0];
	}
	
	protected int getRecentReadingMax() {
		// Could be a lot more clever with this, but if it doesn't impact perf this is simple
		int[] sortedReadings = Arrays.copyOf(this.readings, BALL_SENSOR_READING_MAX);
		Arrays.sort(sortedReadings);
		
		return sortedReadings[sortedReadings.length - 1];
	}
}

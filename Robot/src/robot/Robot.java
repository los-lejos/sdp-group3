package robot;

import lejos.nxt.LightSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

public abstract class Robot {
	
	private static final int LIGHT_SENSOR_CUTOFF = 40;
	private static final int FRONT_SENSOR_CUTOFF = 12;
	
	int TIRE_DIAMETER_MM;
	int TRACK_WIDTH_MM;
	NXTRegulatedMotor LEFT_MOTOR;
	NXTRegulatedMotor RIGHT_MOTOR;
	LightSensor LEFT_LIGHT_SENSOR;
	LightSensor RIGHT_LIGHT_SENSOR;
    UltrasonicSensor FRONT_SENSOR;
    DifferentialPilot PILOT;
    double robotMoveSpeed;
    
    public boolean rightSensorOnBoundary() {
    	return RIGHT_LIGHT_SENSOR.getLightValue() >= LIGHT_SENSOR_CUTOFF;
    }
    
    public boolean leftSensorOnBoundary() {
    	return LEFT_LIGHT_SENSOR.getLightValue() >= LIGHT_SENSOR_CUTOFF;
    }
    
    public boolean objectAtFrontSensor() {
    	return FRONT_SENSOR.getDistance() <= FRONT_SENSOR_CUTOFF;
    }
    
    public void setMoveSpeed(double speed) {
    	robotMoveSpeed = speed;
    }
    
    public DifferentialPilot getPilot() {
    	return PILOT;
    }

}

import lejos.nxt.*;

public class Main {
	public static void main (String[] args) {
		// Test for kicking the ball
		UltrasonicSensor ultra = new UltrasonicSensor(SensorPort.S1);

		while(Button.readButtons() == 0) {
			System.out.println(ultra.getDistance());
			
			if(ultra.getDistance() >= 10) {
				Motor.A.setSpeed(400);
				Motor.A.rotate(40);
				
				while(Motor.A.isMoving());
				
				Motor.A.setSpeed(800);
				Motor.A.rotate(-40);

			}
		}

		Button.ESCAPE.waitForPressAndRelease();
	}
}

//public class Main {
//	
//	public enum State {
//		FORWARD, TURNING_RIGHT, TURNING_LEFT
//	}
//	
//    public static final NXTRegulatedMotor leftMotor = Motor.A;
//    public static final NXTRegulatedMotor rightMotor = Motor.C;
//    
//    public static long lastSensor = 0;
//    public static State currentState = State.FORWARD;
//    
//    private static final int LightCutoff = 40;
//    private static final int RobotMoveSpeed = 720;
//    private static final int RobotTurnSpeed = 400;
//    private static final long ReverseTime = 2000;
//    
//	
//	public static void main (String[] args) {
//		LightSensor leftLight = new LightSensor(SensorPort.S1);
//        LightSensor rightLight = new LightSensor(SensorPort.S4);
//        
//		//System.out.println("Press a button for a great time!");
//        //Button.waitForAnyPress();
//
//        // start moving to begin with
//        rightMotor.setSpeed(RobotMoveSpeed);
//        leftMotor.setSpeed(RobotMoveSpeed);
//        leftMotor.backward();
//        rightMotor.backward();
//
//        // begin main loop
//        boolean running = true;
//        while (running) {
//
//            // System.out.println(leftLight.getLightValue());
//            
//            if (currentState == State.FORWARD) {
//
//                if (leftLight.getLightValue() >= LightCutoff) {
//                    System.out.println("Turning right.");
//                    currentState = State.TURNING_RIGHT;
//                    rightMotor.setSpeed(RobotTurnSpeed);
//                    leftMotor.setSpeed(RobotTurnSpeed);
//                    rightMotor.forward();
//                } else if (rightLight.getLightValue() >= LightCutoff) {
//                    System.out.println("Turning left.");
//                    currentState = State.TURNING_LEFT;
//                    rightMotor.setSpeed(RobotTurnSpeed);
//                    leftMotor.setSpeed(RobotTurnSpeed);
//                    leftMotor.forward();
//                }
//
//            } else if (currentState == State.TURNING_RIGHT) {
//                
//                if (leftLight.getLightValue() < LightCutoff &&
//                    rightLight.getLightValue() < LightCutoff) {
//                    System.out.println("Going forward.");
//                    currentState = State.FORWARD;
//                    rightMotor.setSpeed(RobotMoveSpeed);
//                    leftMotor.setSpeed(RobotMoveSpeed);
//                    rightMotor.backward();
//                }
//
//            } else if (currentState == State.TURNING_LEFT) {
//
//                if (rightLight.getLightValue() < LightCutoff &&
//                	leftLight.getLightValue() < LightCutoff) {
//                    System.out.println("Going forward.");
//                    currentState = State.FORWARD;
//                    rightMotor.setSpeed(RobotMoveSpeed);
//                    leftMotor.setSpeed(RobotMoveSpeed);
//                    leftMotor.backward();
//                }
//
//            } else
//                System.out.println("Impossible state!");
//
//
//            if (Button.readButtons() != 0)
//                running = false;
//
//        }
//
//
//		
//        Button.waitForAnyPress();
//	}
//
//}

//import java.io.*;
//import lejos.nxt.*;
//import lejos.nxt.comm.*;
//
//public class BTReceive {
//  public static void main(String [] args) throws Exception {
//    String connected = "Connected";
//    String waiting = "Waiting...";
//    String closing = "Closing...";
//
//    while (true) {
//      LCD.drawString(waiting,0,0);
//      NXTConnection connection = Bluetooth.waitForConnection(); 
//      LCD.clear();
//      LCD.drawString(connected,0,0);
//
//      DataInputStream dis = connection.openDataInputStream();
//      DataOutputStream dos = connection.openDataOutputStream();
//
//      for(int i=0;i<100;i++) {
//        int n = dis.readInt();
//        LCD.drawInt(n,7,0,1);
//        dos.writeInt(-n);
//        dos.flush();
//      }
//      dis.close();
//      dos.close();
//
//      LCD.clear();
//      LCD.drawString(closing,0,0);
//
//      connection.close();
//      LCD.clear();
//    }
//  }
//}

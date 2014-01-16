package uk.ac.ed.inf.sdp;
//import lejos.nxt.*;
//
//public class Main {
//	public static void main (String[] args) {
////		LightSensor light = new LightSensor(SensorPort.S1);
////		light.calibrateLow();
////		
//		Motor.A.setSpeed(720);
//		Motor.C.setSpeed(720);
//	  	Motor.A.forward();
//	  	Motor.C.forward();
//	  	
//	  	UltrasonicSensor ultra = new UltrasonicSensor(SensorPort.S1);
//		ultra.continuous();
//
//		while(Button.readButtons() == 0 && 
//				ultra.getDistance() < 15);
//				//light.getLightValue() < 30);
//		
//		Motor.A.stop();
//		Motor.C.stop();
//		
////		while(Button.readButtons() == 0) {
////			System.out.println();
////		} // > 15 looks good for ball
//		
//		Button.waitForAnyPress();
//	}
//}

import lejos.nxt.*;

public class Main {
	public static void main (String[] args) {
		LightSensor light = new LightSensor(SensorPort.S1);
		light.calibrateLow();
		
		Motor.A.setSpeed(360);
		Motor.C.setSpeed(360);
	  	Motor.A.forward();
		Motor.C.forward();

		int turns = 0;

		while(Button.readButtons() == 0) {

            // drive until there is some white
            while(Button.readButtons() == 0 && 
                    light.getLightValue() < 30);
            
            Motor.A.backward();

            // turn until there is no white
            while(Button.readButtons() == 0 && 
                    light.getLightValue() > 30);

            Motor.A.forward();

            turns++;
        }
            
        Button.waitForAnyPress();
	}
}

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

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

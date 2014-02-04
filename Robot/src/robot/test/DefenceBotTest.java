package robot.test;
import robot.navigation.HolonomicPilot;
import lejos.nxt.*;

public class DefenceBotTest {
      public static void main (String[] args) throws InterruptedException {
 
    	  HolonomicPilot pilot = new HolonomicPilot(48, Motor.A, Motor.B);
    	  
    	  pilot.setTravelSpeed(200, 200);
		  		  
		  pilot.forward();
		  Thread.sleep(5000);
		  pilot.stop();
		  
		  pilot.right();
		  Thread.sleep(5000);
		  pilot.stop();   
		  
		  pilot.backward();
		  Thread.sleep(5000);
		  pilot.stop();
		  
		  pilot.left();
		  Thread.sleep(5000);
		  pilot.stop();
	
		  
		  pilot.travel(0, 360, true);
		  System.out.println(Motor.A.getTachoCount());
		  System.out.println(Motor.B.getTachoCount());
		  System.out.println();
    	  
		  pilot.travel(250, 45, true);
		  System.out.println(Motor.A.getTachoCount());
		  System.out.println(Motor.B.getTachoCount());
		  System.out.println();
		  
    	  Button.waitForAnyPress();

      }
}                                                             
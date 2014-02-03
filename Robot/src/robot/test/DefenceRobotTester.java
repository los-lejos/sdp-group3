package robot.test;
import robot.navigation.HolonomicPilot;
import lejos.nxt.*;

public class DefenceRobotTester {
      public static void main (String[] args) {
    	  
    	  HolonomicPilot pilot = new HolonomicPilot(48, Motor.A, Motor.B);
    	  
    	  pilot.setTravelSpeed(100, 100);
		  int xAxis = 0;
		  int yAxis = 100;
		  
		  while(xAxis < 20){    			  
			  pilot.forward();
			  xAxis++;
			  System.out.print(xAxis);
		  }
		  pilot.stop();
		  
		  while(yAxis < 120){    			  
			  pilot.right();
			  yAxis++;
			  System.out.print(yAxis);
		  }
		  pilot.stop();   
		  
		  while(xAxis > 0){    			  
			  pilot.backward();
			  xAxis--;
			  System.out.print(xAxis);
		  }
		  pilot.stop();
		  
		  while(yAxis > 100){    			  
			  pilot.backward();
			  yAxis--;
			  System.out.print(yAxis);
		  }
		  pilot.stop();
		  
		  pilot.travel(200, 45, true);
		  
		  for (int wait = 0; wait < 20; wait++){
			  System.out.print(wait);
		  }
    	  
    	  Button.waitForAnyPress();
      }
}                                                             
package dice.misc;
import dice.misc.RotationCalibration;

public class Main {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		String cmd = "";
		for (String str : args) {
			cmd += str + " ";
		}
		RotationCalibration program = new RotationCalibration();
		program.init(cmd);
		program.run();
		program.cleanup();
	}

}

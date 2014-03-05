package dice.misc;

import java.io.IOException;
import java.util.Arrays;

import shared.RobotInstructions;

import dice.Log;
import dice.communication.BluetoothRobotCommunicator;
import dice.communication.RobotCommunicationCallback;
import dice.communication.RobotCommunicator;
import dice.communication.RobotEventListener;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.WorldState;
import dice.state.WorldState.BallPossession;
import dice.strategy.StrategyEvaluator;
import dice.strategy.StrategyEvaluator.StrategyType;
import dice.vision.SocketVisionReader;

/*
 * @author Ingvaras Merkys
 *
 */

public class RotationCalibration {
	
	private RobotCommunicator attackerComms;
	private StrategyEvaluator strategy;
	private WorldState worldState;
	private SocketVisionReader visionReader;
	private RobotEventListener attackerEventListener = new RobotEventListener() {
		@Override
		public void onBallCaught() {}
		@Override
		public void onBallReleased() {}
	};
	private GameObject attackerRobot;

	public void init(String cmd) {
		Log.init();
		try {
			Process p = Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			Log.logError("Exception occured.");
			e.printStackTrace();
		}
		worldState = WorldState.init();
        worldState.setSide(WorldState.Side.LEFT);
		this.attackerComms = new BluetoothRobotCommunicator();
		strategy = new StrategyEvaluator();
		strategy.setType(StrategyType.NONE);
		strategy.setCommunicator(RobotType.ATTACKER, attackerComms);
		this.visionReader = new SocketVisionReader(worldState, strategy);
		this.attackerRobot = worldState.getOurAttacker();
	}
	
	public void cleanup() {
		if(this.attackerComms != null) {
			this.attackerComms.close();
		}
		this.visionReader.stop();
	}
	
	public void setWidthAndTurn(int i) throws InterruptedException {
		double angleToTurn = 180;
		UsefulCallback callback = new UsefulCallback();
		byte byte1 = (byte) (i / 10);
		byte byte2 = (byte) (i % 10);
		byte byte3 = (byte) 0;
		RobotInstruction instrSet = new RobotInstruction(RobotInstructions.SET_TRACK_WIDTH, byte1, byte2, byte3);
		instrSet.setCallback(callback);
		this.attackerComms.sendInstruction(instrSet);
		Thread.sleep(1500);                   
		RobotInstruction instrRot = RobotInstruction.createMoveTo(angleToTurn, 0);
		callback.cont = false;
		instrRot.setCallback(callback);
		this.attackerComms.sendInstruction(instrRot);;
		Thread.sleep(1500);
	}

	public void run() throws InterruptedException {
		this.attackerComms.init(RobotType.ATTACKER, this.attackerEventListener);
		worldState.setSide(WorldState.Side.LEFT);
		Log.logInfo("Ready");
		double angleDiffPrev = Math.toDegrees(attackerRobot.getRotation());
		double angleDiff = angleDiffPrev;
		double anglePrev = angleDiffPrev;
		double angleReal = angleDiffPrev;
		int minTrackWidth = 110;
		int maxTrackWidth = 200;
		for (int i = minTrackWidth; i < maxTrackWidth; i+=10) {
			setWidthAndTurn(i);
			anglePrev = angleReal;
			angleReal = Math.toDegrees(attackerRobot.getRotation());
			angleDiffPrev = angleDiff;
			angleDiff = angleReal - anglePrev;
			angleDiff = angleDiff < 0 ? Math.abs(angleDiff) + 180 : angleDiff;
			if (angleDiff > angleDiffPrev) {
				for (int j = i-10; j < i; j++) {
					setWidthAndTurn(j);
					anglePrev = angleReal;
					angleReal = Math.toDegrees(attackerRobot.getRotation());
					angleDiff = angleReal - anglePrev;
					angleDiff = angleDiff < 0 ? Math.abs(angleDiff) + 180 : angleDiff;
					if (angleDiff < 2) {
						System.out.println("SetTrackWidth" + j);
						break;
					}
				}
			}
			Thread.sleep(1500);
		}
	}
}

package tests.strategy;

import dice.communication.RobotCommunicator;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.WorldState;
import dice.strategy.StrategyEvaluator;
import dice.strategy.StrategyEvaluator.StrategyType;

public class StrategyTests {
	public static void main(String[] args) {
		test();
	}
	
	public static void test() {
		GameObject opponentDefender = new GameObject(0, 0, 0.0);
		GameObject ourAttacker = new GameObject(0, 0, 0.0);
		GameObject opponentAttacker = new GameObject(0, 0, 0.0);
		GameObject ourDefender = new GameObject(0, 0, 0.0);
		GameObject ball = new GameObject(0, 0, 0.0);
		WorldState state = new WorldState(opponentDefender, opponentAttacker, ourDefender, ourAttacker, ball);
		
		RobotCommunicator attackerComms = new MockRobotCommunicator();
		attackerComms.init(RobotType.ATTACKER, null);
		
		RobotCommunicator defenderComms = new MockRobotCommunicator();
		defenderComms.init(RobotType.DEFENDER, null);		
		
		StrategyEvaluator strat = new StrategyEvaluator();
		strat.setType(StrategyType.MATCH);
		strat.setCommunicator(RobotType.ATTACKER, attackerComms);
		strat.setCommunicator(RobotType.DEFENDER, defenderComms);
		strat.onNewState(state);
	}
}

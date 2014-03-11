package tests.strategy;

import dice.communication.RobotCommunicator;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.WorldState;
import dice.state.WorldState.Side;
import dice.strategy.StrategyEvaluator;
import dice.strategy.StrategyEvaluator.StrategyType;

public class StrategyTests {
	public static void main(String[] args) {
		test();
	}
	
	public static void test() {
		GameObject opponentDefender = new GameObject();
		GameObject ourAttacker = new GameObject();
		GameObject opponentAttacker = new GameObject();
		GameObject ourDefender = new GameObject();
		GameObject ball = new GameObject();
		WorldState state = new WorldState(opponentDefender, opponentAttacker, ourDefender, ourAttacker, ball, Side.LEFT);
		
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

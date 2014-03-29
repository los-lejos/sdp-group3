package dice.strategy;

import dice.communication.RobotType;
import dice.state.WorldState;
import dice.strategy.action.attacker.BlockAction;
import dice.strategy.action.attacker.RecievePassAction;
import dice.strategy.action.attacker.ShootAction;
import dice.strategy.action.shared.CorrectionAction;
import dice.strategy.action.shared.ToBallAction;

public class AttackerStrategyState extends RobotStrategyState {
	
	private RecievePassAction recievePass;
	private ShootAction shoot;
	private ToBallAction toBall;
	private CorrectionAction correction;
	private CorrectionAction passCorrect;
	private BlockAction block;

	public AttackerStrategyState() {
		super(RobotType.ATTACKER);
		
		this.recievePass = new RecievePassAction(RobotType.ATTACKER);
		this.shoot = new ShootAction(RobotType.ATTACKER);
		this.toBall = new ToBallAction(RobotType.ATTACKER);
		this.passCorrect = new CorrectionAction(RobotType.ATTACKER, true);
		this.correction = new CorrectionAction(RobotType.ATTACKER);
		this.block = new BlockAction(RobotType.ATTACKER);
	}

	@Override
	public StrategyAction getBestAction(WorldState state) {
		if(recievePass.isPossible(state)) {
			if(passCorrect.isPossible(state)) {
				return passCorrect;
			}
			
			return recievePass;
		}
		
		if(shoot.isPossible(state)) {
			// Correct before we shoot
			if(correction.isPossible(state)) {
				return correction;
			} else {
				return shoot;
			}
		}
		
		if(toBall.isPossible(state)) {
			return toBall;
		}
		
		if(block.isPossible(state)) {
			// Correct before we block
			if(correction.isPossible(state)) {
				return correction;
			} else {
				return block;
			}
		}
		
		return null;
	}

}

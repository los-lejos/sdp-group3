package dice.strategy;

import dice.communication.RobotType;
import dice.state.WorldState;
import dice.strategy.action.defender.PassAction;
import dice.strategy.action.defender.SaveAction;
import dice.strategy.action.shared.CorrectionAction;
import dice.strategy.action.shared.ToBallAction;

public class DefenderStrategyState extends RobotStrategyState {
	
	private PassAction pass;
	private ToBallAction toBall;
	private CorrectionAction correction;
	private SaveAction save;

	public DefenderStrategyState() {
		super(RobotType.DEFENDER);
		
		this.pass = new PassAction(RobotType.DEFENDER);
		this.toBall = new ToBallAction(RobotType.DEFENDER);
		this.correction = new CorrectionAction(RobotType.DEFENDER, CorrectionAction.Side.OPP);
		this.save = new SaveAction(RobotType.DEFENDER);
	}

	@Override
	public StrategyAction getBestAction(WorldState state) {
		if(pass.isPossible(state)) {
			if(correction.isPossible(state)) {
				return correction;
			}
			
			return pass;
		}
		
		if(toBall.isPossible(state)) {
			return toBall;
		}
		
		if(save.isPossible(state)) {
			if(correction.isPossible(state)) {
				return correction;
			}
			
			return save;
		}

		return null;
	}

}

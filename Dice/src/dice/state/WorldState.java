package dice.state;

/** The class that will contain all information about the
 * current state of the game.
 * @author Craig Wilkinson
 */
public class WorldState {
    private GameObject opponentDefender;
    private GameObject opponentAttacker;
    private GameObject ourDefender;
    private GameObject ourAttacker;
    private GameObject ball;

    // populate the world. First all robots and the
    // ball must be created
    public WorldState(GameObject opponentDefender, GameObject opponentAttacker, GameObject ourDefender, GameObject ourAttacker, GameObject ball) {
        
        this.opponentDefender = opponentDefender;
        this.opponentAttacker = opponentAttacker;
        this.ourDefender = ourDefender;
        this.ourAttacker = ourAttacker;
        this.ball = ball;
    }

    public void update(GameObject opponentDefender, GameObject opponentAttacker, GameObject ourDefender, GameObject ourAttacker, GameObject ball) {
        
        this.opponentDefender = opponentDefender;
        this.opponentAttacker = opponentAttacker;
        this.ourDefender = ourDefender;
        this.ourAttacker = ourAttacker;
        this.ball = ball;
    }

    public GameObject getOpponentDefender() {
    	return opponentDefender;
    }

    public GameObject getOpponentAttacker() {
    	return opponentAttacker;
    }

    public GameObject getOurDefender() {
    	return ourDefender;
    }

    public GameObject getOurAttacker() {
    	return ourAttacker;
    }

}

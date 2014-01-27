package dice.state;

public class Ball extends GameObject {
	private byte currentQuartile;

    public Ball(float xPos, float yPos) {
    	super(xPos, yPos);
    }

    public void setQuartile(byte quartile) 
                            throws OutOfRangeException {
    	if (quartile >= 0 && quartile <= 4)
            this.currentQuartile = quartile;
        else
        	throw new OutOfRangeException("Ball cannot be in " +
        	    "quartile " + String.valueOf(quartile));
    }

    public byte getCurrentQuartile() {
        return currentQuartile;
    }
}

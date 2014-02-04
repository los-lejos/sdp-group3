package dice.state;

public class Goal {
	private Position topPost;
	private Position bottomPost;

	public Goal(Position top, Position bottom) {
		topPost = top;
		bottomPost = bottom;
    }

    public double getHeight() {
    	return bottomPost.Y - topPost.Y;
    }
    
    public Position getTopPost() {
    	return topPost;
    }

    public Position getBottomPost() {
    	return bottomPost;
    }

}

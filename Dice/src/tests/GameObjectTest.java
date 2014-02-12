package tests;

import dice.Log;
import dice.state.GameObject;
import java.lang.Math;

public class GameObjectTest {
	public static void main(String[] args) {
	    testRotations();
    }

    public static void testRotations() {
        // create a couple of objects
        GameObject test1 = new GameObject();
        GameObject test2 = new GameObject();

        Log.logError(String.valueOf(test2.getRotationRelativeTo(test1)));
    }
}

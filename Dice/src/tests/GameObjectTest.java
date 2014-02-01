package tests;

import dice.state.GameObject;
import java.lang.Math;

public class GameObjectTest {
	public static void main(String[] args) {
	    testRotations();
    }

    public static void testRotations() {
        // create a couple of objects
        GameObject test1 = new GameObject(0, 0, Math.PI/4.0);
        GameObject test2 = new GameObject(10, 10, 0);

        System.out.println(String.valueOf(test2.getRotationRelativeTo(test1)));
    }
}

package dice.state;

import java.lang.Math;

public class AngleTest {
	public static void main(String[] args) {
		GameObject obj1 = new GameObject();
		obj1.setPos(0,0,0);
		obj1.setRotation(Math.PI / 2.0);
		GameObject obj2 = new GameObject();
		obj2.setPos(-1,1,0);

		System.out.println(String.valueOf(obj1.getRotationRelativeTo(obj2)));
	}
}

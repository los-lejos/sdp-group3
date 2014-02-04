# Robot

## Instructions

Right now the robot accepts two instructions, MOVE_TO and KICK_TOWARD.

### MOVE_TO

This has two parameters, the **angle** in which we want to move, and the **distance** to travel.

* The **angle** parameter should be in range 0-359, with 0 being straight ahead. Angles are relative to current heading of robot (so eg. 90 = turn right).
* The **distance** parameter should be given in cm.

We can also use MOVE_TO to rotate by suppling distance == 0.

### KICK_TOWARD

### Byte representation

* **angle** should be given by the first two parameters. Parameter 1 = the hundreds value of angle. Parameter 2 = the tens and units values. For example, an angle of 234 is represented by the parameters 2 and 34.
* **distance** should be given by the third parameter. Since this value is not read during KICK_TOWARD we can just use zero, or whatever convention we decide upon.

## To Do

* Units for distance? Will probably take distances in cm, so need to convert to robot travel units (whatever these are).
* Test: What happens with negative distances? Would be nice to use these for reversing.
## Running vision system

The vision system is in the `vision/` directory.

To run the vision system, execute `vision/vision.py`. It accepts several parameters:

`-h, --help`: Show this help message and exit

`-p PITCH, --pitch=PITCH`: PITCH should be 0 for main pitch, 1 for the other pitch

`-f FILE, --file=FILE`: Use FILE as input instead of capturing from Camera

`-s, --stdout`: Send output to stdout instead of using a socket

`-r, --reset`: Don't restore the last run's saved pitch size

`-t, --thresholds`: Don't restore the last run's saved thresholds and blur values

`-b, --blur`: Display blurred stream

`-n, --normalize`: Normalize at startup (recommended with -t)

So if you're only playing with the vision system (the simulator is not running), you should use i.e.:

`python vision/vision.py -str -p 0`

Another example, to keep all the previous values (pitch size, thresholds, blur) and load stream from the image file:

`python vision/vision.py -p 0 -f vision/pitch.jpg`

## Using vision system

You can change the active layer by pressing **Y** (for Yellow), **B** (for Blue) or **R** (for Red).

You can regulate the thresholds with the trackbars. H, S and V stands for **H**ue (ranges from 0 to 179, represents the colour), **S**aturation (ranges from 0 to 255), and **V**alue (ranges from 0 to 255, represents the brightness).

Press **T** to see the area which is detected with the current values of the thresholds. Press **T** again to go back to normal mode.

Press **N** to normalize the brightness of the frame (in case yellow T is not detected properly).

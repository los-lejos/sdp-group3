To run vision system you first need to set up environment variables.

Open terminal and type:
> gedit /afs/inf.ed.ac.uk/user/s10/YOUR_STUDENT_NUMBER/.bashrc

Add the following lines: 

export PYTHONPATH=$PYTHONPATH:/afs/inf.ed.ac.uk/user/s11/sXXXXXXX/Desktop/sdp-group3/Dice/vision/lib/lib/python2.6/site-packages
export PYTHONPATH=$PYTHONPATH:/afs/inf.ed.ac.uk/user/s11/sXXXXXXX/Desktop/sdp-group3/Dice/vision/lib/sdl_font/lib
export PYTHONPATH=$PYTHONPATH:/afs/inf.ed.ac.uk/user/s11/sXXXXXXX/Desktop/sdp-group3/Dice/vision/lib/opencv2.3/lib
export PYTHONPATH=$PYTHONPATH:/afs/inf.ed.ac.uk/user/s11/sXXXXXXX/Desktop/sdp-group3/Dice/vision/lib/opencv2.3/lib/python2.6/site-packages

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/afs/inf.ed.ac.uk/user/s11/sXXXXXXX/Desktop/sdp-group3/Dice/vision/lib/sdl_font/lib
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/afs/inf.ed.ac.uk/user/s11/sXXXXXXX/Desktop/sdp-group3/Dice/vision/lib/lib/python2.6/site-packages
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/afs/inf.ed.ac.uk/user/s11/sXXXXXXX/Desktop/sdp-group3/Dice/vision/lib/opencv2.3/lib

Save it.

Then run:
> exec bash

Important! Restart terminal for this to truly take effect.

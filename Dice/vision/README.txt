Usage: vision.py [options]

Options:
  -h, --help            show this help message and exit
  -p PITCH, --pitch=PITCH
                        PITCH should be 0 for main pitch, 1 for the other
                        pitch
  -s                    Send output to stdout instead of using a socket
  --reset-pitch         Don't restore the last run's saved pitch size
  --reset-thresh        Don't restore the last run's saved thresholds
  --scale=SCALE         Scale down the image in preprocessing stage
  -c COLOUR_ORDER, --colour-order=COLOUR_ORDER
                        The way different colour robots are put from left to
                        right (e. g. "--colour-order=yybb" for sequence
                        yellow-yellow-blue-blue)
  -n                    Turns off threshold layers. Use when thresholds are
                        well adjusted.
  -f FILE, --file=FILE  File input, can be a video or image.

To run vision system you first need to set up environment variables.

Open terminal and type:
> gedit /afs/inf.ed.ac.uk/user/s11/YOUR_STUDENT_NUMBER/.bashrc

Add the following lines: 

export PYTHONPATH=$PYTHONPATH:/afs/inf.ed.ac.uk/user/s11/sXXXXXXX/<path-to-project>/Dice/vision/lib/lib/python2.6/site-packages
export PYTHONPATH=$PYTHONPATH:/afs/inf.ed.ac.uk/user/s11/sXXXXXXX/<path-to-project>/Dice/vision/lib/sdl_font/lib
export PYTHONPATH=$PYTHONPATH:/afs/inf.ed.ac.uk/user/s11/sXXXXXXX/<path-to-project>/Dice/vision/lib/opencv2.3/lib
export PYTHONPATH=$PYTHONPATH:/afs/inf.ed.ac.uk/user/s11/sXXXXXXX/<path-to-project>/Dice/vision/lib/opencv2.3/lib/python2.6/site-packages

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/afs/inf.ed.ac.uk/user/s11/sXXXXXXX/<path-to-project>/Dice/vision/lib/sdl_font/lib
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/afs/inf.ed.ac.uk/user/s11/sXXXXXXX/<path-to-project>/Dice/vision/lib/lib/python2.6/site-packages
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/afs/inf.ed.ac.uk/user/s11/sXXXXXXX/<path-to-project>/Dice/vision/lib/opencv2.3/lib

Save it.

Then run:
> exec bash

Important! Restart terminal for this to truly take effect.

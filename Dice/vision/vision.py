from __future__ import print_function
import sys
import os
import time
import math
import socket

from optparse import OptionParser

from SimpleCV import Image, Camera, VirtualCamera
from preprocess import Preprocessor
from features import Features
from threshold import Threshold
from filter import Filter
from display import Gui, ThresholdGui

HOST = 'localhost' 
PORT = 28541

# Distinct between field size line or entity line
ENTITY_BIT = 'E';
BALL_BIT = 'O';
BLUE_BIT = 'B';
YELLOW_BIT = 'Y';
PITCH_SIZE_BIT  = 'P';

class Vision:
    
    def __init__(self, pitchnum, stdout, sourcefile, resetPitchSize, resetThresholds, displayBlur, normalizeAtStartup, noDribbling):
               
        self.running = True
        self.connected = False
   
        self.stdout = stdout 

        if sourcefile is None:  
            self.cap = Camera()
        else:
            filetype = 'video'
            if sourcefile.endswith(('jpg', 'png')):
                filetype = 'image'

            self.cap = VirtualCamera(sourcefile, filetype)
        
        calibrationPath = os.path.join('calibration', 'pitch{0}'.format(pitchnum))
        self.cap.loadCalibration(os.path.join(sys.path[0], calibrationPath))

        self.preprocessor = Preprocessor(pitchnum, resetPitchSize)
        if self.preprocessor.hasPitchSize:
            self.gui = Gui(self.preprocessor.pitch_size)
        else:
            self.gui = Gui()
        self.threshold = Threshold(pitchnum, resetThresholds, displayBlur, normalizeAtStartup)
        self.thresholdGui = ThresholdGui(self.threshold, self.gui)
        self.features = Features(self.gui, self.threshold)
        self.filter = Filter(noDribbling)
        
        eventHandler = self.gui.getEventHandler()
        eventHandler.addListener('q', self.quit)

        while self.running:
            try:
                if not self.stdout:
                    self.connect()
                else:
                    self.connected = True

                if self.preprocessor.hasPitchSize:
                    self.outputPitchSize()
                    self.gui.setShowMouse(False)
                else:
                    eventHandler.setClickListener(self.setNextPitchCorner)

                while self.running:
                    self.doStuff()

            except socket.error:
                self.connected = False
                # If the rest of the system is not up yet/gets quit,
                # just wait for it to come available.
                time.sleep(1)
                print("Connection error, sleeping 1s...")

                # Strange things seem to happen to X sometimes if the
                # display isn't updated for a while
                self.doStuff()

        if not self.stdout:
            self.socket.close()
        
    def connect(self):
        print("Attempting to connect...")
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.connect( (HOST, PORT) )
        self.connected = True
        print("Successfully connected")

    def quit(self):
        self.running = False
        
    def doStuff(self):
        if self.cap.getCameraMatrix is None:
            frame = self.cap.getImage()
        else:
            frame = self.cap.getImageUndistort()

        frame = self.preprocessor.preprocess(frame)
        
        self.gui.updateLayer('raw', frame)

        ents = self.features.extractFeatures(frame)
        self.outputEnts(ents)

        self.gui.loop()

    def setNextPitchCorner(self, where):
        self.preprocessor.setNextPitchCorner(where)
        
        if self.preprocessor.hasPitchSize:
            print("Pitch size: {0!r}".format(self.preprocessor.pitch_size))
            self.outputPitchSize()
            self.gui.setShowMouse(False)
            self.gui.updateLayer('corner', None)
        else:
            self.gui.drawCrosshair(where, 'corner')
    
    def outputPitchSize(self):
        print(self.preprocessor.pitch_size)
        self.send('{0} {1} {2} \n'.format(
                PITCH_SIZE_BIT, self.preprocessor.pitch_size[0], self.preprocessor.pitch_size[1]))

    def outputEnts(self, ents):

        # Messyyy
        if not self.connected or not self.preprocessor.hasPitchSize:
            return

        self.send('{0} '.format(ENTITY_BIT))
        for name in ['yellow', 'blue', 'ball']:
            entity = ents[name]
            x, y = entity.coordinates()

            self.send('{0} {1} '.format(x, y))

            # The rest of the system needs (0, 0) at the bottom left
            if y != -1:
                y = self.preprocessor.pitch_size[1] - y

            if name == 'ball':
                angle = -1
            else:
                angle = 360 - (((entity.angle() * (180/math.pi)) - 360) % 360)
            self.filter.change(name, x, y, angle)

            #coords = self.filter.update()

        #for name in ['yellow', 'blue', 'ball']:
        #   if name == 'ball':
        #        self.send('{0} {1} '.format(coords[name][0], coords[name][1]))
        #    else:
        #        self.send('{0} {1} {2} '.format(coords[name][0], coords[name][1], coords[name][2]))

        self.send(str(int(time.time() * 1000)) + " \n")
        
    def send(self, string):
        if self.stdout:
            sys.stdout.write(string)
        else:
            self.socket.send(string)

class OptParser(OptionParser):
    """
    The default OptionParser exits with exit code 2
    if OptionParser.error() is called. Unfortunately this
    screws up our vision restart script which tries to indefinitely
    restart the vision system with bad options. This just exits with
    0 instead so everything works.
    """
    def error(self, msg):
        self.print_usage(sys.stderr)
        self.exit(0, "%s: error: %s\n" % (self.get_prog_name(), msg))

if __name__ == "__main__":

    parser = OptParser()

    parser.add_option('-p', '--pitch', dest='pitch', type='int', metavar='PITCH', default='0',
                      help='PITCH should be 0 for main pitch, 1 for the other pitch')

    parser.add_option('-f', '--file', dest='file', metavar='FILE',
                      help='Use FILE as input instead of capturing from Camera')

    parser.add_option('-s', '--stdout', action='store_true', dest='stdout', default=False,
                      help='Send output to stdout instead of using a socket')

    parser.add_option('-r', '--reset', action='store_true', dest='resetPitchSize', default=False,
                      help='Don\'t restore the last run\'s saved pitch size')

    parser.add_option('-t', '--thresholds', action='store_true', dest='resetThresholds', default=False,
                      help='Don\'t restore the last run\'s saved thresholds and blur values')

    parser.add_option('-b', '--blur', action='store_true', dest='displayBlur', default=False,
                      help='Display blurred stream')
                      
    parser.add_option('-n', '--normalize', action='store_true', dest='normalizeAtStartup', default=False,
                      help='Normalize at startup')

    parser.add_option('-d', '--no-dribbling', action='store_true', dest='noDribbling', default=False,
                      help='Disable robot-is-dribbling-the-ball filter')

    (options, args) = parser.parse_args()

    if options.pitch not in [0,1]:
        parser.error('Pitch must be 0 or 1')

    Vision(options.pitch, options.stdout, options.file, options.resetPitchSize, options.resetThresholds, options.displayBlur, options.normalizeAtStartup, options.noDribbling)




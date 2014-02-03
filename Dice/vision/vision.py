#!/usr/bin/python
# -*- coding: utf-8 -*-

"""
Vision subsystem for System Design Project 2014, group 3.
Based on work by group 6, SDP 2013.

Main class

Sends data (coordinates of robots and the ball) to localhost over TCP.
"""

from optparse import OptionParser

__author__ = "Ingvaras Merkys"

HOST = 'localhost'
PORT = 28541

class Vision:

    def __init__(self, pitch_num, stdout, reset_pitch_size, reset_thresholds, scale, colour_order):

        self.running = True
        self.connected = False
#        self.scale = scale
#        self.colour_order = colour_order
        self.stdout = stdout
        self.cam = Camera()
        self.preprocessor = Preprocessor(pitch_num, reset_pitch_size, scale)
        if self.preprocessor.has_pitch_size:
            self.gui = Gui(self.preprocessor.pitch_size)
        else:
            self.gui = Gui()
        self.threshold = Threshold(pitch_num, reset_thresholds)
        self.threshold_gui = ThresholdGui(self.threshold, self.gui)
        self.detection = Detection(self.gui, self.threshold, colour_order, scale, pitch_num)
        self.event_handler = self.gui.get_event_handler()
        self.event_handler.addListener('q', self.quit)

        while self.running:
            try:
                if not self.stdout:
                    self.connect()
                else:
                    self.connected = True

                if self.preprocessor.has_pitch_size:
                    self.output_pitch_size()
                    self.gui.set_show_mouse(False)
                else:
                    self.eventHandler.setClickListener(self.setNextPitchCorner)

                while self.running:
                    self.process_frame()
                    

            except socket.error:
                self.connected = False
                # If the rest of the system is not up yet/gets quit,
                # just wait for it to come available.
                time.sleep(1)
                print("Connection error, sleeping 1s...")
                # Strange things seem to happen to X sometimes if the
                # display isn't updated for a while
                self.process_frame()

        if not self.stdout:
            self.socket.close()

    def process_frame(self):
        """Get frame, detect objects and display frame
        """
        # This is where calibration comes in
        if self.cap.getCameraMatrix is None:
            frame = self.cap.getImage()
        else:
            frame = self.cap.getImageUndistort()

        frame = self.preprocessor.preprocess(frame)
        self.gui.update_layer('raw', frame)

        if self.preprocessor.has_pitch_size:
            entities = self.detection.detect_objects(frame)

        self.outputEnts(entities)
        self.gui.process_update()

if __name__ == "__main__":

    parser = OptionParser()

    parser.add_option('-p', '--pitch', dest='pitch', type='int', metavar='PITCH', default='0',
                      help='PITCH should be 0 for main pitch, 1 for the other pitch')

    parser.add_option('-s', '--stdout', action='store_true', dest='stdout', default=False,
                      help='Send output to stdout instead of using a socket')

    parser.add_option('-r', '--reset', action='store_true', dest='reset_pitch_size', default=False,
                      help='Don\'t restore the last run\'s saved pitch size')

    parser.add_option('-t', '--thresholds', action='store_true', dest='reset_thresholds', default=False,
                      help='Don\'t restore the last run\'s saved thresholds and blur values')

    parser.add_option('-c', '--scale', dest='scale', type='float', metavar='SCALE', default=0.0,
                      help='Scale down the image in preprocessing stage')

    parser.add_option('-i', '--colour-order', dest='colour_order', type='string', metavar='COLOUR_ORDER', default='yybb'
                      help='COLOUR_ORDER - the way different colour robots are put from left to right (e. g. "yybb")')

    (opts, args) = parser.parse_args()

    if opts.pitch not in [0, 1]:
        parser.error('Pitch must be 0 or 1.')

    if opts.colour_order not in ['yybb', 'bbyy', 'ybby', 'byyb', 'ybyb', 'byby']:
        parser.error('Invalid colour ordering specified.')

    Vision(opts.pitch, opts.stdout, opts.reset_pitch_size, opts.reset_thresholds, opts.scale, opts.colour_order)

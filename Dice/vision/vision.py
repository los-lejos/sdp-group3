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

    def __init__(self, pitch_num, stdout, reset_pitch_size, reset_thresholds, scale, our_colour, our_side):

        self.running = True
        self.connected = False
        self.stdout = stdout
        self.cam = Camera()
        self.preprocessor = Preprocessor(pitch_num, reset_pitch_size, scale)
        if self.preprocessor.has_pitch_size:
            self.gui = Gui(self.preprocessor.pitch_size)
        else:
            self.gui = Gui()
        self.threshold = Threshold(pitch_num, reset_thresholds)
        self.threshold_gui = ThresholdGui(self.threshold, self.gui)

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

    parser.add_option('-u', '--our-colour', dest='our_colour', type='string', metavar='COLOUR',
                      help='COLOUR - the colour of our team (yellow/blue)')

    # dumb, could be done automagically
    parser.add_option('-i', '--our-side', dest='our_side', type='string', metavar='SIDE',
                      help='SIDE - are we on the left or right side')

    (opts, args) = parser.parse_args()

    if opts.pitch not in [0, 1]:
        parser.error('Pitch must be 0 or 1.')

    if opts.our_colour not in ['yellow', 'blue']:
        parser.error('Invalid (or no) team colour specified.')

    if opts.our_side not in ['left', 'right']:
        parser.error('Invalid (or no) side specified.')

    Vision(opts.pitch, opts.stdout, opts.reset_pitch_size, opts.reset_thresholds, opts.scale, opts.our_colour, opts.our_side)

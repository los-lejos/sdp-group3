#!/usr/bin/python
# -*- coding: utf-8 -*-

"""
Vision subsystem for System Design Project 2014, group 3.
Based on work by group 6, SDP 2013.

Main class

Sends data (coordinates of robots and the ball) to localhost over TCP.
"""

import sys
import os
import time
import socket
from optparse import OptionParser
from SimpleCV import Camera, VirtualCamera
from preprocessor import Preprocessor
from gui import Gui, ThresholdGui
from threshold import Threshold
from detection import Detection, Entity

__author__ = "Ingvaras Merkys"

HOST = 'localhost'
PORT = 28541
ENTITY_BIT = 'E'
PITCH_SIZE_BIT = 'P'
BALL = 4

class Vision:

    def __init__(self, pitch_num, stdout, reset_pitch_size, reset_thresholds, scale, colour_order, file_input=None):

        self.running = True
        self.connected = False
        self.scale = scale
        self.stdout = stdout

        if file_input is None:
            self.cam = Camera(prop_set = {"width": 720, "height": 540})
        else:
            file_type = 'video'
            if file_input.endswith(('jpg', 'png')):
                file_type = 'image'
            self.cam = VirtualCamera(file_input, file_type)

        try:
            calibration_path = os.path.join('calibration', 'pitch{0}'.format(pitch_num))
            self.cam.loadCalibration(os.path.join(sys.path[0], calibration_path))
        except TypeError:
            print 'Calibration file not found.'

        self.preprocessor = Preprocessor(pitch_num, reset_pitch_size, scale)
        if self.preprocessor.has_pitch_size:
            self.gui = Gui(self.preprocessor.pitch_size)
        else:
            self.gui = Gui()
        self.threshold = Threshold(pitch_num, reset_thresholds)
        self.threshold_gui = ThresholdGui(self.threshold, self.gui, pitch_num = pitch_num)
        self.detection = Detection(self.gui, self.threshold, colour_order, scale, pitch_num)
        self.event_handler = self.gui.get_event_handler()
        self.event_handler.add_listener('q', self.quit)

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
                    self.event_handler.set_click_listener(self.set_next_pitch_corner)

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
        if self.cam.getCameraMatrix is None:
            frame = self.cam.getImage()
        else:
            frame = self.cam.getImageUndistort()

        frame = self.preprocessor.preprocess(frame, self.scale)
        self.gui.update_layer('raw', frame)

        if self.preprocessor.has_pitch_size:
            entities = self.detection.detect_objects(frame, self.preprocessor.pitch_size)
            self.output_entities(entities)

        self.gui.process_update()

    def set_next_pitch_corner(self, where):

        self.preprocessor.set_next_pitch_corner(where)

        if self.preprocessor.has_pitch_size:
            #print("Pitch size: {0!r}".format(self.preprocessor.pitch_size))
            self.output_pitch_size()
            self.gui.set_show_mouse(False)
            self.gui.update_layer('corner', None)
        else:
            self.gui.draw_crosshair(where, 'corner')

    def output_pitch_size(self):
        self.send('{0} {1}\n'.format(PITCH_SIZE_BIT, self.preprocessor.pitch_points_string))
        #f = open('pitch_size', 'w')
        #f.write('{0}'.format(self.preprocessor.pitch_points_string))
        #f.close()
        

    def output_entities(self, entities):

        if not self.connected or not self.preprocessor.has_pitch_size:
            return

        self.send('{0} '.format(ENTITY_BIT))

        for i in range(0, 4):
            entity = entities[i]
            x, y = entity.get_coordinates()
            angle = -1 if entity.get_angle() is None else entity.get_angle()
            self.send('{0} {1} {2} '.format(x, y, angle))

        x, y = entities[BALL].get_coordinates()
        self.send('{0} {1} '.format(x, y))
        self.send(str(int(time.time() * 1000)) + "\n")

    def send(self, string):
        if self.stdout:
            sys.stdout.write(string)
        else:
            self.socket.send(string)

    def connect(self):
        print('Attempting to connect...')
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.connect((HOST, PORT))
        self.connected = True
        print('Successfully connected.')

    def quit(self):
        self.running = False

if __name__ == "__main__":

    parser = OptionParser()

    parser.add_option('-p', '--pitch', dest='pitch', type='int', metavar='PITCH', default='0',
                      help='PITCH should be 0 for main pitch, 1 for the other pitch')

    parser.add_option('-s', '--stdout', action='store_true', dest='stdout', default=False,
                      help='Send output to stdout instead of using a socket')

    parser.add_option('-r', '--reset', action='store_true', dest='reset_pitch_size', default=False,
                      help='Don\'t restore the last run\'s saved pitch size')

    parser.add_option('-t', '--thresholds', action='store_true', dest='reset_thresholds', default=False,
                      help='Don\'t restore the last run\'s saved thresholds')

    parser.add_option('-c', '--scale', dest='scale', type='float', metavar='SCALE', default=1.0,
                      help='Scale down the image in preprocessing stage')

    parser.add_option('-i', '--colour-order', dest='colour_order', type='string', metavar='COLOUR_ORDER', default='yybb',
                      help=('COLOUR_ORDER - the way different colour robots are put from left to right '
                            '(e. g. "--colour-order=yybb" for sequence yellow-yellow-blue-blue)'))

    parser.add_option('-f', '--file', dest='file_input', type='string', metavar='FILE', help='File input, can be a video or image.')

    (opts, args) = parser.parse_args()

    if opts.pitch not in [0, 1]:
        parser.error('Pitch must be 0 or 1.')

    if opts.colour_order not in ['yybb', 'bbyy', 'ybby', 'byyb', 'ybyb', 'byby']:
        parser.error('Invalid colour ordering specified.')

    Vision(opts.pitch, opts.stdout, opts.reset_pitch_size, opts.reset_thresholds, opts.scale, opts.colour_order, file_input=opts.file_input)

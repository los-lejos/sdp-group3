#!/usr/bin/python
# -*- coding: utf-8 -*-

"""
Vision subsystem for System Design Project 2014, group 3.
Based on work by group 6, SDP 2013.

Preprocessor class

Preprocesses the frame (cropping and scaling).
"""

import os
import cv
from SimpleCV import Image
from operator import sub
import util

__author__ = "Ingvaras Merkys"

class Preprocessor:

    def __init__(self, pitch_num, reset_pitch_size, scale):

        self._path_pitch_size = os.path.join('data', 'default_pitch_size_{0}').format(pitch_num)
        self._cropRect = None

        if not reset_pitch_size:
            self.__loadPitchSize()

        if self._cropRect is None:
            self._cropRect = []
            self.hasPitchSize = False
        else:
            self.hasPitchSize = True

    def __loadPitchSize(self):
        self._cropRect = util.load_from_file(self._path_pitch_size)

    def __savePitchSize(self):
        util.dump_to_file(self._cropRect, self._path_pitch_size)

    def preprocess(self, frame, scale):
        
        if self.hasPitchSize:
            frame = frame.crop(*self._cropRect).scale(scale)
        return frame

    def setNextPitchCorner(self, point):

        assert len(point) == 2, "setNextPitchCorner takes a tuple (x, y)"

        length = len(self._cropRect)

        if length == 0:
            self._cropRect.extend(point)
        elif length == 2:
            next = map(sub, point, self._cropRect)
            self.hasPitchSize = True
            self._cropRect.extend(next)
            self.__savePitchSize()
        else:
            return
        print "Cropped rectangle {0}".format(self._cropRect)

    @property
    def pitch_size(self):
        if not self.hasPitchSize:
            return None
        return (self._cropRect[2], self._cropRect[3])


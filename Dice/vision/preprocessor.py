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
        self._crop_rect = None

        if not reset_pitch_size:
            self.__load_pitch_size()

        if self._crop_rect is None:
            self._crop_rect = []
            self.has_pitch_size = False
        else:
            self.has_pitch_size = True

    def __load_pitch_size(self):
        self._crop_rect = util.load_from_file(self._path_pitch_size)

    def __save_pitch_size(self):
        util.dump_to_file(self._crop_rect, self._path_pitch_size)

    def preprocess(self, frame, scale):
        
        if self.has_pitch_size:
            frame = frame.crop(*self._crop_rect).scale(scale)
        return frame

    def setNextPitchCorner(self, point):

        assert len(point) == 2, "setNextPitchCorner takes a tuple (x, y)"

        length = len(self._crop_rect)

        if length == 0:
            self._crop_rect.extend(point)
        elif length == 2:
            next = map(sub, point, self._crop_rect)
            self.has_pitch_size = True
            self._crop_rect.extend(next)
            self.__save_pitch_size()
        else:
            return
        print "Cropped rectangle {0}".format(self._crop_rect)

    @property
    def pitch_size(self):
        if not self.has_pitch_size:
            return None
        return (self._crop_rect[2], self._crop_rect[3])


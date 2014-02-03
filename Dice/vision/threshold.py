#!/usr/bin/python
# -*- coding: utf-8 -*-

"""
Vision subsystem for System Design Project 2014, group 3.
Based on work by group 6, SDP 2013.

Threshold class

Performs thresholding.
"""

import os
import cv
from SimpleCV import Image, ColorSpace
import util

__author__ = "Ingvaras Merkys"

class Threshold:

    #default_thresholds[0] for the main pitch, and default_thresholds[1] for the other table

    default_thresholds = [{ 'yellow': [[179, 235, 164], [24, 255, 255]],
                            'blue': [[88, 93, 0], [109, 129, 114]],
                            'ball': [[149, 178, 185], [0, 255, 240]],
                            'dot': [[0, 0, 0], [0, 0, 0]] },
                          { 'yellow': [[11, 32, 198], [42, 255, 255]],
                            'blue': [[84, 80, 17], [139, 122, 255]],
                            'ball': [[0, 0, 79], [15, 255, 255]],
                            'dot': [[0, 0, 0], [0, 0, 0]] }]

    def __init__(self, pitch_num, reset_thresholds):

        self._pitch_num = pitch_num
        self._path_thresholds = os.path.join('data', 'default_thresholds_{0}').format(self._pitch_num)
        self._reset_thresholds = reset_thresholds
        self.__get_defaults()

    def __get_defaults(self):

        self._threshold_values = None
        self._threshold_values = util.load_from_file(self._path_thresholds)
        if (self._threshold_values is None) or (self._reset_thresholds):
            self._threshold_values = dict(self.default_thresholds[self._pitch_num])

    def __save_defaults(self):
        util.dump_to_file(self._threshold_values, self._path_thresholds.format(self._pitch_num))

    def yellowT(self, frame):
        return self.threshold(frame, self._threshold_values['yellow'][0], self._threshold_values['yellow'][1])

    def blueT(self, frame):
        return self.threshold(frame, self._threshold_values['blue'][0], self._threshold_values['blue'][1])

    def ball(self, frame):
        return self.threshold(frame, self._threshold_values['ball'][0], self._threshold_values['ball'][1])

    def dotT(self, frame):
        return self.threshold(frame, self._threshold_values['dot'][0], self._threshold_values['dot'][1])
    
    def threshold(self, frame, threshmin, threshmax):
        """
        Performs thresholding on a frame.
        The image must be in the HSV colorspace!
        """

        assert frame.getColorSpace() == ColorSpace.HSV, "Image must be HSV!"

        iplframe = frame.getBitmap()

        crossover = False
        if threshmin[0] > threshmax[0]:
            # Handle hue threshold crossing over
            # angle boundry e. g. when thresholding on red

            hMax = threshmin[0]
            hMin = threshmax[0]

            crossover = True
            threshmax2 = [hMin, threshmax[1], threshmax[2]]
            threshmin = [hMax, threshmin[1], threshmin[2]] 
            threshmax = [255, threshmax[1], threshmax[2]]
            threshmin2 = [0, threshmin[1], threshmin[2]]

        iplresult = cv.CreateImage(cv.GetSize(iplframe), frame.depth, 1)
        cv.InRangeS(iplframe, threshmin, threshmax, iplresult)

        result = Image(iplresult)

        if crossover:
            iplresult2 = cv.CreateImage(cv.GetSize(iplframe), frame.depth, 1)
            cv.InRangeS(iplframe, threshmin2, threshmax2, iplresult2)

            result = result + Image(iplresult2)

        return result

    def update_values(self, entity, newValues):
        self._threshold_values[entity] = newValues
        self.__save_defaults()


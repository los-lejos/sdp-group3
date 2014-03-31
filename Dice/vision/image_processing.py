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
import cv2
import math
import sys
import numpy as np
from SimpleCV import Image, ColorSpace
from operator import sub
import util

__author__ = "Ingvaras Merkys"

class Processor:

    SPLITS = [0.224, 0.5, 0.776, 1.0]

    def __init__(self, pitch_num, reset_pitch_size, reset_thresholds, scale):

        self._bgr_frame = None
        self._green_channel = None
        self._gray_bin = 0
        self._contrasts = [100, 100, 100, 100]
        self._brightnesses = [1000, 1000, 1000, 1000]
        self._gray_thresholds = [255, 255, 255, 255]
        self._ball_thresholds = [[0, 0, 0], [0, 0, 0]]
        self._path_values = os.path.join('data', 'default_values_{0}').format(pitch_num)
        self._crop_rect = None
        self._pitch = pitch_num
        self._reset_thresholds = reset_thresholds
        self.__get_values()

    def __get_values(self):
        values = util.load_from_file(self._path_values)
        if not(values is None) and not(self._reset_thresholds):
            self._ball_thresholds, self._contrasts, self._brightnesses, self._gray_thresholds = values

    def __save_values(self):
        values = [self._ball_thresholds, self._contrasts, self._brightnesses, self._gray_thresholds]
        util.dump_to_file(values, self._path_values)

    def update_values(self, values, entity):
        if entity == 'ball':
            self._ball_thresholds = values
        else:
            i = int(entity[-1])-1
            self._gray_thresholds[i], self._brightnesses[i], self._contrasts[i] = values
        self.__save_values()

    def set_crop_rect(self, crop_rect):
        self._crop_rect = crop_rect

    def get_values(self, entity):
        try:
            i = int(entity[-1])-1
        except:
            i = 0
        values = [self._ball_thresholds, self._gray_thresholds[i], self._brightnesses[i], self._contrasts[i]]
        return values

    def preprocess(self, frame, scale):
        if not self._crop_rect is None:
            self._bgr_frame = frame.crop(*self._crop_rect).scale(scale)
            self._hsv_frame = self._bgr_frame.toHSV()
            areas = self._split_frame(self._bgr_frame)
            norm_areas = [ self._rgb_norm_frame_channel(area, i) for i, area in enumerate(areas)]
            thresholded_areas = [ self._threshold_gray(area, i) for i, area in enumerate(norm_areas)]
            self._green_channel = self._join_areas(norm_areas)
            self._green_thresh_frame = self._join_areas(thresholded_areas)
            self._red_thresh_frame = self.threshold(self._hsv_frame, self._ball_thresholds)

    def _join_areas(self, areas):
        w, h = self._bgr_frame.size()
        frame = Image((w, h), colorSpace = ColorSpace.GRAY)
        a1, a2, a3, a4 = areas
        frame = frame.blit(a1, pos=(0, 0))
        frame = frame.blit(a2, pos=(int(w*self.SPLITS[0]), 0))
        frame = frame.blit(a3, pos=(int(w*self.SPLITS[1]), 0))
        frame = frame.blit(a4, pos=(int(w*self.SPLITS[2]), 0))
        return frame

    def _split_frame(self, frame):
        w, h = frame.size()
        area1 = frame.crop((0, 0, int(w*self.SPLITS[0]), h))
        area2 = frame.crop((int(w*self.SPLITS[0]), 0, int(w*self.SPLITS[1]), h))
        area3 = frame.crop((int(w*self.SPLITS[1]), 0, int(w*self.SPLITS[2]), h))
        area4 = frame.crop((int(w*self.SPLITS[2]), 0, w, h))
        return (area1, area2, area3, area4)

    def toggle_gray_bin(self, value):
        self._gray_bin = value

    def _rgb_norm_frame_channel(self, frame, index):
        """RGB normalizes green channel and applies brightness/contrast operations,
        slightly more efficient than calculating full normalized RGB"""
        frame_arr = frame.getNumpy().astype(np.float32, copy=False)
        b = frame_arr[:,:,0]
        g = frame_arr[:,:,1]
        r = frame_arr[:,:,2]
        rgb_sum = b+g+r
        green_arr = (g*255.0/rgb_sum)
        # Squares - green
        alpha = self._contrasts[index] / 100.0     # [0.0-5.0]
        beta = self._brightnesses[index] - 1000     # [-500 - 500]
        green_arr = np.clip(alpha * green_arr + beta, 0, 255)
        green_img = Image(green_arr, colorSpace = ColorSpace.GRAY)
        return green_img

    def _threshold_gray(self, frame, i):
        img_bin = frame.threshold(self._gray_thresholds[i])
        return img_bin.morphClose().dilate(1)

    def get_grayscale_frame(self):
        if self._green_channel is None:
            return None
        #assert self._green_channel.getColorSpace() == ColorSpace.GRAY, "Image must be grayscale!"
        return self._green_channel

    def get_binary_frame(self, entity):
        if entity == 'squares':
            return self._green_thresh_frame
        elif entity == 'ball':
            return self._red_thresh_frame
        else:
            raise Exception('Unknown entity {0}'.format(entity))

    def get_bgr_frame(self):
        if self._bgr_frame is None:
            return None
        assert self._bgr_frame.getColorSpace() == ColorSpace.BGR, "Image must be BGR!"
        return self._bgr_frame

    def get_hsv_frame(self):
        if self._hsv_frame is None:
            return None
        assert self._hsv_frame.getColorSpace() == ColorSpace.HSV, "Image must be HSV!"
        return self._hsv_frame

    def threshold(self, frame, thresholds):
        threshmin, threshmax = thresholds
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

class Cropper:

    def __init__(self, pitch_num=0, reset_pitch=False):
        self._path_pitch_size = os.path.join('data', 'default_pitch_size_{0}').format(pitch_num)
        self._pitch_points = []
        self._crop_rect = []
        self._coord_rect = []
        self._all_set = False
        if not reset_pitch:
            self.__load_pitch_size()

    def __load_pitch_size(self):
        things = util.load_from_file(self._path_pitch_size)
        if not things is None:
            self._crop_rect, self._coord_rect = things
            self._all_set = True

    def __save_pitch_size(self):
        util.dump_to_file((self._crop_rect, self._pitch_points), self._path_pitch_size)

    def set_point(self, point):
        if len(self._crop_rect) < 2:
            self._set_crop_corner(point)
        elif len(self._coord_rect) < 2:
            self._set_coord_point(point)
        else:
            raise Exception('Too many points :(')

    def _set_crop_corner(self, point):
        self._crop_rect.append(point)

    def _set_coord_point(self, point):
        self._coord_rect.append(point)
        if len(self._coord_rect) == 2:
            (x1, y1), (x2, y2) = self._coord_rect
            (x3, y3), (x4, y4) = self._crop_rect
            x5 = x1 - x3
            y5 = y1 - y3
            x6 = x4 - x1
            y6 = y4 - y1
            self._coord_rect = [(x5, y5), (x6, y6)]
            self._all_set = True
            self.__save_pitch_size()

    def is_ready(self):
        return self._all_set

    def get_crop_rect(self):
        return self._crop_rect

    def get_coord_rect(self):
        return self._coord_rect

    @property
    def pitch_size(self):
        if not self._all_set:
            return None
        x = self._crop_rect[1][0] - self._crop_rect[0][0]
        y = self._crop_rect[1][1] - self._crop_rect[0][1]
        return (x, y)

'''    @property
    def pitch_points_string(self):
        if not self.has_pitch_size:
            return None
        pitch_points = ' '.join(['{0} {1}'.format(point[0], point[1]) for point in self._pitch_points])
        return pitch_points'''

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

    def __init__(self, pitch_num, reset_pitch_size, scale):

        self._bgr_frame = None
        self._red_channel = None
        self._green_channel = None
        self._gray_bin = 0
        self._contrasts = [100, 100]
        self._brightnesses = [1000, 1000]
        self._gray_thresholds = [255, 255]
        self._path_pitch_size = os.path.join('data', 'default_pitch_size_{0}').format(pitch_num)
        self._path_values = os.path.join('data', 'default_values_{0}').format(pitch_num)
        self._crop_rect = None
        self._corner_point = None
        self._pitch_points = []
        self._pitch = pitch_num

        if not reset_pitch_size:
            self.__load_pitch_size()

        if self._crop_rect is None:
            self._crop_rect = []
            self.has_pitch_size = False
        else:
            self.has_pitch_size = True

    def __load_pitch_size(self):
        things = util.load_from_file(self._path_pitch_size)
        if not things is None:
            self._crop_rect, self._pitch_points = things

    def __save_pitch_size(self):
        util.dump_to_file((self._crop_rect, self._pitch_points), self._path_pitch_size)
 
    def update_values(self, entity, values):
        if entity == 'squares':
            i = 0
        elif entity == 'ball':
            i = 1
        else:
            raise Exception('Unknown entity '.format(entity))
        self._thresholds[i], self._brightnesses[i], self._contrasts[i] = values

    def __get_values(self):
        values = util.load_from_file(self._path_values)
        if (self._threshold_values is None) or (self._reset_thresholds):
            self._threshold_values = dict(self.default_thresholds[self._pitch_num])

    def __save_values(self, values):
        util.dump_to_file(values, self._path_values)

    def preprocess(self, frame, scale):
        if self.has_pitch_size:
            self._bgr_frame = frame.crop(*self._crop_rect).scale(scale)
            self._hsv_frame = self._bgr_frame.toHSV()
            self._red_channel, self._green_channel, _ = self._rgb_norm_frame_channels(self._bgr_frame)
            self._gray_thresh_frame = self._threshold_gray(self._green_channel)

    def toggle_gray_bin(self, value):
        self._gray_bin = value

    def _rgb_norm_frame_channels(self, frame):
        """RGB normalizes green channel and applies brightness/contrast operations,
        slightly more efficient than calculating full normalized RGB"""
        frame_arr = frame.getNumpy().astype(np.float32, copy=False)
        b = frame_arr[:,:,0]
        g = frame_arr[:,:,1]
        r = frame_arr[:,:,2]
        rgb_sum = b+g+r
        red_arr = (r*255.0/rgb_sum)
        green_arr = (g*255.0/rgb_sum)
        # Squares - green
        alpha = self._contrasts[0] / 100.0     # [0.0-5.0]
        beta = self._brightnesses[0] - 1000     # [-500 - 500]
        green_arr = np.clip(alpha * green_arr + beta, 0, 255)
        green_img = Image(green_arr, colorSpace = ColorSpace.GRAY)
        # Ball - red
        alpha = self._contrasts[1] / 100.0     # [0.0-5.0]
        beta = self._brightnesses[1] - 1000     # [-500 - 500]
        red_arr = np.clip(alpha * red_arr + beta, 0, 255)
        red_img = Image(red_arr, colorSpace = ColorSpace.GRAY)
        return (red_img, green_img, None)

    def _threshold_gray(self, frame, entity):
        if entity == 'squares':
            i = 0
        elif entity == 'ball':
            i = 1
        else:
            raise Exception('Unknown entity '.format(entity))
        img_bin = frame.threshold(self._gray_thresholds[i])
        return img_bin.morphClose().dilate(2)

    def get_grayscale_frame(self):
        if self._green_channel is None:
            return None
        assert self._green_channel.getColorSpace() == ColorSpace.GRAY, "Image must be grayscale!"
        return self._green_channel

    def get_binary_frame(self):
        return self._gray_thresh_frame

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

    def set_next_pitch_corner(self, point):

        assert len(point) == 2, "set_next_pitch_corner takes a tuple (x, y)"

        length_rect = len(self._crop_rect)

        if length_rect == 0:
            self._crop_rect.extend(point)
            return
        elif length_rect == 2:
            next = map(sub, point, self._crop_rect)
            self._crop_rect.extend(next)
            return

        if self._corner_point is None and len(self._pitch_points) < 8:
            self._corner_point = point
        elif not self._corner_point is None and len(self._pitch_points) < 8:
            p1, p2 = self.get_corner_points(self._corner_point, point, self._crop_rect)
            self._pitch_points.extend((p1, p2))
            self._corner_point = None
        if len(self._pitch_points) == 8:
            self._pitch_points = self.sort_points(self._pitch_points)
            self.has_pitch_size = True
            self.__save_pitch_size()
            print "Pitch corners {0}".format(self._pitch_points)

    @property
    def pitch_points_string(self):
        if not self.has_pitch_size:
            return None
        pitch_points = ' '.join(['{0} {1}'.format(point[0], point[1]) for point in self._pitch_points])
        return pitch_points

    @property
    def pitch_size(self):
        if not self.has_pitch_size:
            return None
        return (self._crop_rect[2], self._crop_rect[3])

    def get_corner_points(self, p1, p2, crop_rect):

        w = crop_rect[2]
        h = crop_rect[3]

        x1 = p1[0] - crop_rect[0]
        y1 = p1[1] - crop_rect[1]

        x2 = p2[0] - crop_rect[0]
        y2 = p2[1] - crop_rect[1]
        
        a = (y1 - y2)/float((x1 - x2))
        b = y1 - a * x1
        corner_i = self.which_corner(p1, p2, self._crop_rect)
        if corner_i == 0:
            p1 = (0, b)
            p2 = ((0 - b)/a, 0)
        elif corner_i == 1:
            p1 = ((0 - b)/a, 0)
            p2 = (w - 1, a * (w - 1) + b)
        elif corner_i == 2:
            p1 = (0, b)
            p2 = ((h - 1 - b)/a, h - 1)
        elif corner_i == 3:
            p1 = ((h - 1 - b)/a , h - 1)
            p2 = (w - 1, a * (w - 1) + b)
        else:
            raise Exception('Unexpected corner index.')
        p1x = int((float(p1[0])/w)*580)
        p1y = int((float(p1[1])/h)*320)
        p2x = int((float(p2[0])/w)*580)
        p2y = int((float(p2[1])/h)*320)
        return ((p1x, p1y), (p2x, p2y))

    def which_corner(self, p1, p2, crop_rect):

        w = crop_rect[2]
        h = crop_rect[3]
        x1 = p1[0] - crop_rect[0]
        y1 = p1[1] - crop_rect[1]
        x2 = p2[0] - crop_rect[0]
        y2 = p2[1] - crop_rect[1]
        a = (y1 - y2)/float((x1 - x2))
        b = y1 - a * x1
        rect_corners = [(0, (0, 0)), (1, (w - 1, 0)), (2, (0, h - 1)), (3, (w - 1, h - 1))]
        distances = map(lambda (i, x): (i, self.point_to_line(x, (a, b))), rect_corners)
        corner_i, _ = reduce(lambda x, y: x if x[1] < y[1] else y, distances, (-1, sys.maxint))
        return corner_i

    def point_to_line(self, p, fn):

        x, y = p
        a, b = fn
        d = abs(y - a * x - b)/math.sqrt(a * a + 1)
        return d

    def sort_points(self, points):

        sorted_points = [None, None, None, None, None, None, None, None]
        
        for i in range(0, 4):
            j = i * 2
            k = i * 2 + 1
            corner = self.which_corner(points[j], points[k], [0, 0, 580, 320])
            if corner == 0:
                if points[j][0] > points[k][0]:
                    sorted_points[0] = points[j]
                    sorted_points[7] = points[k]
                else:
                    sorted_points[0] = points[k]
                    sorted_points[7] = points[j]
            elif corner == 1:
                if points[j][0] > points[k][0]:
                    sorted_points[2] = points[j]
                    sorted_points[1] = points[k]
                else:
                    sorted_points[2] = points[k]
                    sorted_points[1] = points[j]
            elif corner == 2:
                if points[j][0] > points[k][0]:
                    sorted_points[5] = points[j]
                    sorted_points[6] = points[k]
                else:
                    sorted_points[5] = points[k]
                    sorted_points[6] = points[j]
            elif corner == 3:
                if points[j][0] > points[k][0]:
                    sorted_points[3] = points[j]
                    sorted_points[4] = points[k]
                else:
                    sorted_points[3] = points[k]
                    sorted_points[4] = points[j]
            else:
                raise Exception('Invalid corner {0}'.format(corner))
        return sorted_points

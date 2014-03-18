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
        self._gray_frame = None
        self._gray_bin = 0
        self._contrast = 100
        self._brightness = 1000
        self._gray_thresh = (0, 255)
        self._path_pitch_size = os.path.join('data', 'default_pitch_size_{0}').format(pitch_num)
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

    def preprocess(self, frame, scale):
        if self.has_pitch_size:
            self._bgr_frame = frame.crop(*self._crop_rect).scale(scale)
            self._hsv_frame = self._bgr_frame.toHSV()
            self._gray_frame = self._norm_green_frame(self._bgr_frame)
            self._gray_thresh_frame = self._threshold_gray(self._gray_frame)

    def toggle_gray_bin(self, value):
        self._gray_bin = value

    def _norm_green_frame(self, frame):
        """RGB normalizes green channel and applies brightness/contrast operations,
        slightly more efficient than calculating full normalized RGB"""
        frame_arr = frame.getNumpy().astype(np.float32, copy=False)
        b = frame_arr[:,:,0]
        g = frame_arr[:,:,1]
        r = frame_arr[:,:,2]
        rgb_sum = b+g+r
        gray_frame = (g*255.0/rgb_sum)
        alpha = self._contrast / 100.0     # [0.0-5.0]
        beta = self._brightness - 1000     # [-500 - 500]
        frame_arr = np.clip(alpha * gray_frame + beta, 0, 255)
        return Image(frame_arr, colorSpace = ColorSpace.GRAY)

    def _threshold_gray(self, frame):
        thresh_min, thresh_max = self._gray_thresh
        img_bin = frame.threshold(thresh_max)
        return img_bin.morphClose().dilate(1)

    def get_grayscale_frame(self):
        if self._gray_frame is None:
            return None
        assert self._gray_frame.getColorSpace() == ColorSpace.GRAY, "Image must be grayscale!"
        return self._gray_frame

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

    def set_gray_thresholds(self, min_value, max_value):
        self._gray_thresh = (min_value, max_value)

    def set_contrast(self, value):
        self._contrast = value

    def set_brightness(self, value):
        self._brightness = value

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

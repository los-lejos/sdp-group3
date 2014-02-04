#!/usr/bin/python
# -*- coding: utf-8 -*-

"""
Vision subsystem for System Design Project 2014, group 3.
Based on work by group 6, SDP 2013.

Object detection class

Detects objects.
"""
import sys
import math
import cv
from SimpleCV import Image, Features, DrawingLayer, BlobMaker, Color
from threshold import Threshold

__author__ = "Ingvaras Merkys"

BALL = 4
DOT = 5

class Detection:

    # Format: (area_min, area_expected, area_max)
    # one for both colours COULD be sufficient
    shape_sizes = { 'ball': [10, 160, 175],
                    'yellow': [20, 95, 110],
                    'blue': [20, 95, 110],
                    'dot': [10, 90, 100] }
#(8, 16, 100),
#          'yellow'         : (30, 54, 169),
#          'blue'         : (30, 54, 166),

    # Areas of the robots (width). Symmetrical, allowing for some overlap.
    areas = [(0.0, 0.241), (0.207, 0.516), (0.484, 0.793), (0.759, 1.0)]

    def __init__(self, gui, threshold, colour_order, scale, pitch_num):
    
        self._threshold = threshold
        self._gui = gui
        self._scale = scale
        self._colour_order = colour_order
        self._pitch_num = pitch_num
        self._pitch_w = 720
        self._pitch_h = 540

    def detect_objects(self, frame, pitch_size):

        self._pitch_w, self._pitch_h = pitch_size
        hsv = frame.toHSV()
        # robots left to right, entities[4] is ball
        entities = [None, None, None, None, None]
        # TODO remove/turn off FULL image thresholding for black dots
        thresholds = [None, None, None, None, None, None]
        yellow = frame.copy()
        blue = frame.copy()

        for i in range(0, 4):
            #crop(x, y=None, w=None, h=None, centered=False, smart=False)
            x = int(self._scale*self.areas[i][0]*self._pitch_w)
            y = 0
            w = int(self._scale*self.areas[i][1]*self._pitch_w) - x
            h = frame.height
            cropped_img = hsv.crop(x, y, w, h)

            if self._colour_order[i] == 'b':
                thresholds[i] = self._threshold.blueT(cropped_img).smooth(grayscale=True)
                blue.dl().blit(thresholds[i], (x, y))
            elif self._colour_order[i] == 'y':
                thresholds[i] = self._threshold.yellowT(cropped_img).smooth(grayscale=True)
                yellow.dl().blit(thresholds[i], (x, y))

            entities[i] =  self.__find_entity(thresholds[i], i, cropped_img)

        thresholds[BALL] = self._threshold.ball(hsv).smooth(grayscale=True)
        entities[BALL] = self.__find_entity(thresholds[BALL], BALL, hsv)
        thresholds[DOT] = self._threshold.dotT(hsv).smooth(grayscale=True)
        self._gui.update_layer('threshY', yellow)
        self._gui.update_layer('threshB', blue)
        self._gui.update_layer('threshR', thresholds[BALL])
        self._gui.update_layer('threshD', thresholds[DOT])

        for i in range(0, 4):
            if self._colour_order[i] == 'b':
                self._gui.update_layer('blue', entities[i])
            elif self._colour_order[i] == 'y':
                self._gui.update_layer('yellow', entities[i])

        self._gui.update_layer('ball', entities[BALL])

        return entities

    def __find_entity(self, threshold_img, which, image):
        """
        # Work around OpenCV crash on some nearly black images
        nonZero = cv.CountNonZero(image.getGrayscaleMatrix())
        if nonZero < 10:
            return Entity()
        """
        size = None
        if which == BALL:
            size = map(lambda x: int(x*self._scale), self.shape_sizes['ball'])
        elif self._colour_order[which] == 'b':
            size = map(lambda x: int(x*self._scale), self.shape_sizes['blue'])
        elif self._colour_order[which] == 'y':
            size = map(lambda x: int(x*self._scale), self.shape_sizes['yellow'])

        entity_blob = self.__find_entity_blob(threshold_img, size)
        entity = Entity(self._pitch_w, self._pitch_h, self._colour_order, which, entity_blob, self.areas, self._scale)

        if which >= 0 and which < 4:
            self.__clarify_coords(entity, image)

        return entity

    def __find_entity_blob(self, image, size, dot=False):

        blobmaker = BlobMaker()
        blobs = blobmaker.extractFromBinary(image, image, minsize=size[0], maxsize=size[2])

        if blobs is None:
            return None

        size_matched_blobs = [(self.__match_size(b, size), b) for b in blobs]
        
        if dot:
            size_matched_blobs = filter(lambda (_, b): b.isCircle(0.5), size_matched_blobs)

        _, entity_blob = reduce(lambda x, y: x if x[0] < y[0] else y,
                                size_matched_blobs, (9999, None))

        if entity_blob is None:
            return None

        return entity_blob

    def __match_size(self, blob, expected_size):
        area = blob.area()
        if (expected_size[0] < area < expected_size[2]):
            # Absolute difference from expected size
            return abs(area-expected_size[1])
        # No match
        return sys.maxint

    def __clarify_coords(self, entity, image):
        """Given the coordinates of the colored part of 
        """
        col_x, col_y = entity.get_coordinates()

        if col_x == -1:
            return

        x_offset_left = int(self.areas[entity.which][0]*self._pitch_w*self._scale)
        crop_x = max((col_x - x_offset_left - 23), 0)
        crop_y = max((col_y - 23), 0)
        crop_w = min((max((col_x - x_offset_left), 0) + 23), image.width - crop_x)
        crop_h = min((col_y + 23), image.height - crop_y)
        cropped_img = image.crop(crop_x, crop_y, crop_w, crop_h)
        if cropped_img == None: return
        cropped_img_threshold = self._threshold.dotT(cropped_img).smooth(grayscale=True)
        size = map(lambda x: int(x*self._scale), self.shape_sizes['dot'])
        entity_blob = self.__find_entity_blob(cropped_img_threshold, size)

        if entity_blob is None:
            return

        dot_x, dot_y = entity_blob.centroid()
        a = float(col_y - col_x)
        b = float(col_x - dot_x)

        if a > 0 and b > 0:
            entity.set_angle(math.atan(a/b))
        elif a < 0 and b < 0:
            entity.set_angle(math.pi + math.atan(a/b))
        elif a > 0 and b < 0:
            entity.set_angle(math.pi + math.atan(a/b))
        elif a < 0 and b > 0:
            entity.set_angle(2*math.pi + math.atan(a/b))

        x = int(dot_x + col_x / 2)
        y = int(dot_y + col_y / 2)
        entity.set_coordinates(x, y)

class Entity:

    def __init__(self, pitch_w, pitch_h, colour_order, which = None, entity_blob = None, areas = None, scale = None):

        self._coordinates = (-1, -1)
        self._angle = None
        self._scale = scale
        self._has_angle = False
        self._pitch_w = pitch_w
        self._pitch_h = pitch_h
        self._colour_order = colour_order
        self.which = which
        
        if not entity_blob is None:
            self._entity_blob = entity_blob
            if which == BALL:
                x, y = entity_blob.centroid()
                x = min(int(x/scale), self._pitch_w)
                y = min(int(y/scale), self._pitch_h)
                self._coordinates = (x, y)
            elif which >= 0 and which < 4:
                self._has_angle = True
                x, y = entity_blob.centroid()
                # min to ensure it's never out of range
                x = min(int((x + int(areas[which][0]*scale*self._pitch_w))/scale), self._pitch_w)
                y = min(int(y/scale), self._pitch_h)
                self._coordinates = (x, y)

    def get_coordinates(self):
        return self._coordinates

    def get_angle(self):
        return self._angle

    def set_coordinates(self, x, y):
        self._coordinates = (x, y)

    def set_angle(self, angle):
        self._angle = angle

    def draw(self, layer):
        """Draw this entity to the specified layer
        If angle is true then orientation will also be drawn
        """
        if self.get_coordinates()[0] == -1: return
        if self.which >= 0 and self.which < 4:
            x = int(self._coordinates[0]*self._scale)
            y = int(self._coordinates[1]*self._scale)
            layer.circle((x, y), radius=2, filled=1)

            if self._colour_order[self.which] == 'b':
                colour = Color.BLUE
            else:
                colour = Color.YELLOW
            layer.circle((x, y), radius=int(25*self._scale), color=colour, width=2)
            # TODO improve what's below
            angle = self.get_angle()
            if not angle is None:
                endx = x + int(25.0*self._scale * math.cos(angle))
                endy = y + int(25.0*self._scale * math.sin(angle))
                degrees = abs(self._angle - math.pi)  / math.pi * 180 
                layer.line((x, y), (endx, endy), antialias=False)
        elif self.which == 4:
            w = layer.width
            h = layer.height
            x = int(self._coordinates[0]*self._scale)
            y = int(self._coordinates[1]*self._scale)
            layer.line((x, 0), (x, h), antialias=False, color=Color.RED)
            layer.line((0, y), (w, y), antialias=False, color=Color.RED)

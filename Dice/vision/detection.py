#!/usr/bin/python
# -*- coding: utf-8 -*-

"""
Vision subsystem for System Design Project 2014, group 3.
Based on work by group 6, SDP 2013.

Object detection class

Detects objects.
"""
#TODO decouple Detection and Entity classes
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
    shape_sizes = { 'ball': [145, 160, 175],  # actual 176
                    'yellow': [80, 95, 110],  # actual 104
                    'blue': [80, 95, 110],
                    'dot': [80, 90, 100] } # actual 95

    # Areas of the robots (width). Symmetrical, allowing for some overlap.
    areas = [(0.0, 0.241), (0.207, 0.516), (0.484, 0.793), (0.759, 1.0)]

    def __init__(self, gui, threshold, colour_order, scale, pitch_num):
    
        self._threshold = threshold
        self._gui = gui
        self._scale = scale
        self._colour_order = colour_order
        self._pitch_num = pitch_num

    def detect_objects(self, frame):

        hsv = frame.toHSV()
        # robots left to right, entities[4] is ball
        entities = [None, None, None, None, None]
        # TODO remove/turn off FULL image thresholding for black dots
        thresholds = [None, None, None, None, None, None]
        yellow = frame
        blue = frame

        for i in range(0, 4):
            #crop(x, y=None, w=None, h=None, centered=False, smart=False)
            x = int(self._scale*self.areas[i][0]*720)
            y = 0
            w = int(self._scale*self.areas[i][1]*720)-x
            h = frame.height
            print 'x={0} y={1} w={2} h={3}'.format(x, y, w, h)
            cropped_img = hsv.crop(x, y, w, h)

            if self._colour_order[i] == 'b':
                thresholds[i] = self._threshold.blueT(cropped_img).smooth(grayscale=True)
                blue.blit(thresholds[i], (x, y))
            elif self._colour_order[i] == 'y':
                thresholds[i] = self._threshold.yellowT(cropped_img).smooth(grayscale=True)
                yellow.blit(thresholds[i], (x, y))

            entities[i] =  self.__find_entity(thresholds[i], i, cropped_img)

        thresholds[BALL] = self._threshold.ball(hsv).smooth(grayscale=True)
        entities[BALL] = self.__find_entity(ball, BALL, hsv)
        thresholds[DOT] = self._threshold.dotT(hsv).smooth(grayscale=True)
        self._gui.update_layer('threshY', yellow)
        self._gui.update_layer('threshB', blue)
        self._gui.update_layer('threshR', thresholds[BALL])
        self._gui.update_layer('threshD', thresholds[DOT])
        return entities

    def __find_entity(self, image, which, orig):
        '''
        # Work around OpenCV crash on some nearly black images
        nonZero = cv.CountNonZero(image.getGrayscaleMatrix())
        if nonZero < 10:
            return Entity()
        '''
        size = None
        if which == BALL:
            size = map(lambda x: int(x*self._scale), self.shape_sizes['ball'])
        elif self._colour_order[which] == 'b':
            size = map(lambda x: int(x*self._scale), self.shape_sizes['blue'])
        elif self._colour_order[which] == 'y':
            size = map(lambda x: int(x*self._scale), self.shape_sizes['yellow'])

        entity_blob = self.__find_entity_blob(image, size)
        entity = Entity(self, which, entity_blob, orig.getBitmap(), self.areas, self._scale)

        return entity

    def __find_entity_blob(self, image, size):

        blobmaker = BlobMaker()
        blobs = blobmaker.extractFromBinary(image, image, minsize=size[0], maxsize=size[2])
        if blobs is None:
            return None

        # returns blob for which __match_size returns lowest value
        _, entity_blob = reduce(lambda x, y: x if x[0] < y[0] else y,
                                [(self.__match_size(b, size), b) for b in blobs], (9999, None))

        if entity_blob is None:
            return None

    def __match_size(self, blob, expected_size):
        area = blob.area()
        if (expected_size[0] < area < expected[2]):
            # Absolute difference from expected size
            return abs(area-expected[1])
        # No match
        return sys.maxint

class Entity:

    def __init__(self, detection, which = None, entity_blob = None, image = None, areas = None, scale = None):

        self._coordinates = (-1, -1)
        self._angle = None
        self._scale = scale
        self._has_angle = False
        self._detection = detection
        
        if not entity_blob is None:
            self._entity_blob = entity_blob
            if which == BALL:
                x, y = entity_blob.centroid()
                x = min(int(x/scale), 720)
                y = min(int(y/scale), 540)
                self._coordinates = (x, y)
            elif which >= 0 and which < 4:
                self._has_angle = True
                x, y = entity_blob.centroid()
                # min to ensure it's never out of range
                x = min(int((x + int(areas[which][0]*scale*720))/scale), 720)
                y = min(int(y/scale), 540)
                self._coordinates, self._angle = self.__clarify_coords(image, (x, y))

    def coordinates(self):
        return self._coordinates

    def angle(self):
        return self._angle

    def __clarify_coords(self, image, colour_coords):
        """Given the coordinates of the colored part of 
        """
        col_x, col_y = colour_coords
        crop_x = max((col_x - 23), 0)
        crop_y = max((col_y - 23), 0)
        crop_w = min((col_x + 23), image.width)
        crop_h = min((col_y + 23), image.height)
        cropped_img = image.crop(crop_x, crop_y, crop_w, crop_h)
        #TODO maybe check if circle??
        cropped_img_threshold = self._threshold.dotT(cropped_img).smooth(grayscale=True)
        size = int(self.detection.shape_sizes['dot']*self._scale)
        entity_blob = self.detection.__find_entity_blob(cropped_img_threshold, size)
        # if dot is not found return at least the coordinates
        if entity_blob is None:
            return (None, colour_coords)
        dot_x, dot_y = entity_blob.centroid
        a = float(col_y - col_x)
        b = float(col_x - dot_x)
        if a > 0 and b > 0:
            angle = math.atan(a/b)
        elif a < 0 and b < 0:
            angle = math.pi + math.atan(a/b)
        elif a > 0 and b < 0:
            angle = math.pi + math.atan(a/b)
        elif a < 0 and b > 0:
            angle = 2*math.pi + math.atan(a/b)
        x = dot_x + col_x / 2
        y = dot_y + col_y / 2
        return (angle, (x, y))

    def draw(self, layer, angle=True):
        """Draw this entity to the specified layer
        If angle is true then orientation will also be drawn
        """
        if which >= 0 and which < 4:
            x = int(self._coordinates[0]*self._scale)
            y = int(self._coordinates[1]*self._scale)
            layer.circle((x, y), radius=2, filled=1)

            if self.detection._colour_order[which] == 'b':
                colour = Color.BLUE
            else:
                colour = Color.YELLOW
            layer.circle((x, y), radius=int(25*self._scale), color=Color.BLUE)
            # TODO improve what's below
            if angle and self._has_angle:
                angle = self.angle()
                endx = x + int(25*self._scale) * math.cos(angle)
                endy = y + int(25*self._scale) * math.sin(angle)
                degrees = abs(self._angle - math.pi)  / math.pi * 180 
                layer.line((x, y), (endx, endy), antialias=False)
        elif which == 4:
            w = layer.width
            h = layer.height
            x = int(self._coordinates[0]*self._scale)
            y = int(self._coordinates[1]*self._scale)
            layer.line((x, 0), (x, h), antialias=False, color=Color.RED)
            layer.line((0, y), (w, y), antialias=False, color=Color.RED)

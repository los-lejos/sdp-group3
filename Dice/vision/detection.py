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
import time
from SimpleCV import Image, Features, DrawingLayer, BlobMaker, Color
from threshold import Threshold

__author__ = "Ingvaras Merkys"

BALL = 4
DOT = 5

class Detection:

    # Format: (area_min, area_expected, area_max)
    # one for both colours COULD be sufficient
    shape_sizes = { 'ball': [20, 160, 175],
                    'yellow': [50, 110, 150],
                    'blue': [50, 110, 150],
                    'dot': [20, 40, 80] }

    # Areas of the robots (width). Symmetrical, allowing for some overlap.
    areas = [(0.0, 0.241), (0.207, 0.516), (0.484, 0.793), (0.759, 1.0)]

    def __init__(self, gui, threshold, colour_order, scale, pitch_num):
    
        self._threshold = threshold
        self._gui = gui
        self._scale = scale
        self._colour_order = colour_order
        self._pitch_num = pitch_num
        self._pitch_w = 580
        self._pitch_h = 320

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
                self._gui.update_layer('blue{0}'.format(i), entities[i])
            elif self._colour_order[i] == 'y':
                self._gui.update_layer('yellow{0}'.format(i), entities[i])

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
        
        #if dot:
        #    a = len(size_matched_blobs)
        #    size_matched_blobs = filter(lambda (_, b): b.isCircle(tolerance=0.8), size_matched_blobs)
        #    print 'blobs1={0} blobs2={1}'.format(a, len(size_matched_blobs))

        _, entity_blob = reduce(lambda x, y: x if x[0] < y[0] else y,
                                size_matched_blobs, (9999, None))

        return entity_blob

    def __match_size(self, blob, expected_size):
        area = blob.area()
        if (expected_size[0] < area < expected_size[2]):
            # Absolute difference from expected size
            return abs(area-expected_size[1])
        # No match
        return sys.maxint

    def __clarify_coords(self, entity, image):
        """Given the coordinates of the colored part of 'i' find the dot,
        calculate the angle and update coordinates
        """
        radius = int(23.0*self._scale)
        x, y = entity.get_local_coords()
        # if coordinates are negative there is no object
        if x == -1: return
        # crop out a rectangle to look for the dot
        x1 = max(x - radius, 0)
        y1 = max(y - radius, 0)
        x2 = min(x + radius, image.width)
        y2 = min(y + radius, image.height)
        cropped_img = image.crop((x1, y1), (x2, y2))
        if cropped_img == None: return
        cropped_img_threshold = self._threshold.dotT(cropped_img).smooth(grayscale=True)
        # set entity.rect for drawing
        x_offset = int(self._scale * self._pitch_w * self.areas[entity.which][0])
        entity.rect = ((x1 + x_offset, y1), (x2 - x1, y2 - y1))
        # find the dot
        size = map(lambda x: int(x*self._scale), self.shape_sizes['dot'])
        entity_blob = self.__find_entity_blob(cropped_img_threshold, size, dot=True)
        if entity_blob is None: return
        entity.dot = tuple(map(lambda x: int(x), entity_blob.centroid()))

        # calculate the angle
        dot_x, dot_y = entity_blob.centroid()
        dot_local_x = dot_x + x1
        dot_local_y = dot_y + y1
        
        delta_x = float(abs(dot_local_x - x))
        delta_y = float(abs(dot_local_y - y))

        if x >= dot_local_x and y >= dot_local_y:
            entity.set_angle(math.atan(delta_y/delta_x))
        elif x <= dot_local_x and y >= dot_local_y:
            entity.set_angle(math.pi-math.atan(delta_y/delta_x))
        elif x >= dot_local_x and y <= dot_local_y:
            entity.set_angle(2*math.pi-math.atan(delta_y/delta_x))
        elif x <= dot_local_x and y <= dot_local_y:
            entity.set_angle(1.5*math.pi-math.atan(delta_x/delta_y))
        else:
            print 'wat'
        # update coordinates of the centre of the robot
        entity.clarify_coords(dot_local_x, dot_local_y)

class Entity:

    def __init__(self, pitch_w, pitch_h, colour_order, which = None, entity_blob = None, areas = None, scale = None):

        self._coordinates = (-1, -1)  # coordinates in 580x320 coordinate system
        self._local_coords = (-1, -1) # coordinates in the relevant area of the frame
        self._frame_coords = (-1, -1) # coordinates in the frame
        self._colour_frame_coords = (-1, -1)
        self._angle = None
        self._scale = scale
        self._has_angle = False
        self._pitch_w = pitch_w
        self._pitch_h = pitch_h
        self._colour_order = colour_order
        self._areas = areas
        self.which = which
        self.rect = None
        self.dot = None
        
        if not entity_blob is None:
            x_local, y_local = map(lambda x: int(x), entity_blob.centroid())
            self._local_coords = (x_local, y_local)
            if which == BALL:
                self._frame_coords = (x_local, y_local)
            elif which >= 0 and which < 4:
                self._has_angle = True
                x_frame = x_local + int(areas[which][0]*self._pitch_w*scale)
                self._frame_coords = (x_frame, y_local)
                self._colour_frame_coords = self._frame_coords
            x = int(self._frame_coords[0]/float(self._pitch_w)*580)
            y = int(self._frame_coords[1]/float(self._pitch_h)*320)
            self._coordinates = (x, y)

    def get_coordinates(self):
        return self._coordinates

    def get_local_coords(self):
        return self._local_coords

    def get_frame_coords(self):
        return self._frame_coords

    def get_angle(self):
        return self._angle

    def clarify_coords(self, dot_local_x, dot_local_y):
        local_x, local_y = self.get_local_coords()
        local_x = (local_x + dot_local_x)/2
        local_y = (local_y + dot_local_y)/2
        self._local_coords = (local_x, local_y)
        
        frame_x, frame_y = self.get_frame_coords()
        dot_frame_x = dot_local_x + int(self._areas[self.which][0]*self._pitch_w*self._scale)
        dot_frame_y = dot_local_y
        frame_x = int((frame_x + dot_frame_x)/2)
        frame_y = int((frame_y + dot_frame_y)/2)
        self._frame_coords = (frame_x, frame_y)
        
        x, y = self.get_coordinates()
        x = int((frame_x/float(self._pitch_w))*580)
        y = int((frame_y/float(self._pitch_h))*580)
        self._coordinates = (x, y)

    def set_angle(self, angle):
        self._angle = angle

    def draw(self, layer):
        """Draw this entity to the specified layer
        If angle is true then orientation will also be drawn
        """
        if self.get_coordinates()[0] == -1: return
        if not self._colour_frame_coords[0] == -1:
            layer.circle(self._colour_frame_coords, radius=2, filled=1)
        if not self.rect is None:
            layer.rectangle(self.rect[0], self.rect[1], color=Color.RED)
        if not self.dot is None:
            layer.circle((self.rect[0][0]+self.dot[0], self.rect[0][1]+self.dot[1]), radius=2, filled=1)
        if self.which >= 0 and self.which < 4:
            x, y = self.get_frame_coords()
            layer.circle((x, y), radius=2, filled=1)
            if self._colour_order[self.which] == 'b':
                colour = Color.BLUE
            else:
                colour = Color.YELLOW
            layer.circle((x, y), radius=int(25*self._scale), color=colour, width=2)
            angle = self.get_angle()
            if not angle is None:
                endx = x + int(25.0 * self._scale * math.cos(angle))
                endy = y + int(25.0 * self._scale * math.sin(angle))
                layer.line((x, y), (endx, endy), antialias=False)
                degrees = abs(self._angle - math.pi)  / math.pi * 180
                layer.ezViewText('{0:.1f} deg'.format(degrees), (x, y-int(40*self._scale)))
        elif self.which == BALL:
            w = layer.width
            h = layer.height
            x, y = self.get_frame_coords()
            layer.line((x, 0), (x, h), antialias=False, color=Color.RED)
            layer.line((0, y), (w, y), antialias=False, color=Color.RED)


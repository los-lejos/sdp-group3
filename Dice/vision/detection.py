#!/usr/bin/python
# -*- coding: utf-8 -*-

"""
Vision subsystem for System Design Project 2014, group 3.

Object detection class

Detects objects.
"""
import sys
import math
import cv
import time
from SimpleCV import Image, Features, DrawingLayer, BlobMaker, Color
from threshold import Threshold
from logger import Logger

__author__ = "Ingvaras Merkys"

BALL = 4
DOT = 5
WIDTH = 580
HEIGHT = 320
RADIUS = 23.0
DOT_RADIUS = 8

class Detection:

    # Format: (area_min, area_expected, area_max)
    # one for both colours COULD be sufficient
    shape_sizes = { 'ball': [40, 160, 175],
                    'yellow': [80, 110, 185],
                    'blue': [80, 110, 185],
                    'dot': [20, 40, 80] }

    # Areas of the robots (width). Symmetrical, allowing for some overlap.
    areas = [(0.0, 0.241), (0.207, 0.516), (0.484, 0.793), (0.759, 1.0)]

    def __init__(self, gui, threshold, colour_order, scale, pitch_num):
    
        self._threshold = threshold
        self._gui = gui
        self._scale = scale
        self._colour_order = colour_order
        self._pitch_num = pitch_num
        self._pitch_w = WIDTH
        self._pitch_h = HEIGHT
        self._logger = Logger('detection_errors.log')

    def detect_objects(self, frame, pitch_size):

        self._pitch_w, self._pitch_h = pitch_size
        hsv = frame.toHSV()
        # robots left to right, entities[4] is ball
        entities = [None, None, None, None, None]
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

        # Work around OpenCV crash on some nearly black images
        nonZero = cv.CountNonZero(image.getGrayscaleMatrix())
        if nonZero < 10:
            return Entity()

        size = None
        if which == BALL:
            size = map(lambda x: int(x*self._scale), self.shape_sizes['ball'])
        elif self._colour_order[which] == 'b':
            size = map(lambda x: int(x*self._scale), self.shape_sizes['blue'])
        elif self._colour_order[which] == 'y':
            size = map(lambda x: int(x*self._scale), self.shape_sizes['yellow'])
        else:
            self._logger.log('Unrecognized colour {0} for pitch area.'.format(self._colour_order[which]))

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
                                size_matched_blobs, (sys.maxint, None))

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
        radius = int(RADIUS*self._scale)
        x, y = entity.get_local_coords()
        angle = entity.get_angle()

        # if coordinates are negative there is no object
        if x == -1 : return

        # get coordinates of possible centers
        c1_x = x + int(0.7 * radius * math.cos(angle))
        c1_y = y + int(0.7 * radius * math.sin(angle))
        c2_x = x - int(0.7 * radius * math.cos(angle))
        c2_y = y - int(0.7 * radius * math.sin(angle))

        c1_x1 = max(c1_x - DOT_RADIUS, 0)
        c1_y1 = max(c1_y - DOT_RADIUS, 0)
        c1_x2 = min(c1_x + DOT_RADIUS, image.width)
        c1_y2 = min(c1_y + DOT_RADIUS, image.height)
        c2_x1 = max(c2_x - DOT_RADIUS, 0)
        c2_y1 = max(c2_y - DOT_RADIUS, 0)
        c2_x2 = min(c2_x + DOT_RADIUS, image.width)
        c2_y2 = min(c2_y + DOT_RADIUS, image.height)

        cropped_img1 = image.crop((c1_x1, c1_y1), (c1_x2, c1_y2))
        crop_w1 = cropped_img1.width
        crop_h1 = cropped_img1.height
        cropped_img2 = image.crop((c2_x1, c2_y1), (c2_x2, c2_y2))
        crop_w2 = cropped_img2.width
        crop_h2 = cropped_img2.height

        if cropped_img1 == None: return
        cropped_img1_threshold = self._threshold.dotT(cropped_img1).smooth(grayscale=True)
        if cropped_img2 == None: return
        cropped_img2_threshold = self._threshold.dotT(cropped_img2).smooth(grayscale=True)
        # set entity.rect1 and entity.rect2 for drawing
        x_offset = int(self._scale * self._pitch_w * self.areas[entity.which][0])
        entity.rect1 = ((c1_x1 + x_offset, c1_y1), (c1_x2 - c1_x1, c1_y2 - c1_y1))
        entity.rect2 = ((c2_x1 + x_offset, c2_y1), (c2_x2 - c2_x1, c2_y2 - c2_y1))

        # If cropped_img1_threshold is brighter (i. e. contains the black dot)
        # flip the angle and switch colours (so that black dot is in red rectangle)
        if cropped_img1_threshold.meanColor()[2] > cropped_img2_threshold.meanColor()[2]:
            entity.set_angle(angle + math.pi)
            entity.rect_colors = (Color.RED, Color.VIOLET)

        # TODO correct the coordinates and the angle based on where is the dot
        '''# find the dot
        size = map(lambda x: int(x*self._scale), self.shape_sizes['dot'])
        entity_blob = self.__find_entity_blob(cropped_img1_threshold, size, dot=True)
        if entity_blob is None: return
        entity.dot = tuple(map(lambda x: int(x), entity_blob.centroid()))

        # calculate the angle
        dot_x, dot_y = entity_blob.centroid()
        dot_local_x = dot_x + c1_x1
        dot_local_y = dot_y + c1_y1

        entity.clarify_coords(dot_local_x, dot_local_y)
        
        delta_x = float(abs(dot_local_x - x))
        delta_y = float(abs(dot_local_y - y))

        try:
            if x >= dot_local_x and y >= dot_local_y:
                dot_angle = math.atan(delta_y/delta_x)
            elif x <= dot_local_x and y >= dot_local_y:
                dot_angle = math.pi-math.atan(delta_y/delta_x)
            elif x >= dot_local_x and y <= dot_local_y:
                dot_angle = 2*math.pi-math.atan(delta_y/delta_x)
            elif x <= dot_local_x and y <= dot_local_y:
                dot_angle = 1.5*math.pi-math.atan(delta_x/delta_y)
            else:
                self._logger.log('wat')
        except ZeroDivisionError:
            self._logger.log('Angle detection failure - division by zero.')
        entity.set_angle(average_angles(curr_angle, dot_angle))
        '''

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
        self.rect_colors = (Color.VIOLET, Color.RED)
        self.rect1 = None
        self.rect2 = None
        self.dot = None
        
        if not entity_blob is None:
            self._angle = self.calculate_angle(entity_blob)
            x_local, y_local = map(lambda x: int(x), entity_blob.centroid())
            self._local_coords = (x_local, y_local)
            if which == BALL:
                self._frame_coords = (x_local, y_local)
            elif which >= 0 and which < 4:
                self._has_angle = True
                x_frame = x_local + int(areas[which][0]*self._pitch_w*scale)
                self._frame_coords = (x_frame, y_local)
                self._colour_frame_coords = self._frame_coords
            x = int(self._frame_coords[0]/float(self._pitch_w)*WIDTH)
            y = int(self._frame_coords[1]/float(self._pitch_h)*HEIGHT)
            self._coordinates = (x, y)

    def get_coordinates(self):
        return self._coordinates

    def get_local_coords(self):
        return self._local_coords

    def get_frame_coords(self):
        return self._frame_coords

    def get_angle(self):
        return self._angle

    def set_angle(self, angle):
        self._angle = angle

    def calculate_angle(self, entity_blob):
        cx, cy = entity_blob.centroid()
        m00 = entity_blob.m00
        mu11 = entity_blob.m11 - cx * entity_blob.m01
        mu20 = entity_blob.m20 - cx * entity_blob.m10
        mu02 = entity_blob.m02 - cy * entity_blob.m01
        # Compute the blob's covariance matrix
        # | a b |
        # | b c |
        a = mu20 / m00
        b = mu11 / m00
        c = mu02 / m00
        # Can derive the formula for the angle from the eigenvector associated with
        # the largest eigenvalue
        angle = 0.5 * math.atan2(2 * b, a - c)
        return angle

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
        x = int((frame_x/float(self._pitch_w))*WIDTH)
        y = int((frame_y/float(self._pitch_h))*HEIGHT)
        self._coordinates = (x, y)

    def draw(self, layer):
        """Draw this entity to the specified layer
        If angle is true then orientation will also be drawn
        """
        if self.get_coordinates()[0] == -1: return
        if not self._colour_frame_coords[0] == -1:
            layer.circle(self._colour_frame_coords, radius=2, filled=1)
        if not self.rect1 is None:
            layer.rectangle(self.rect1[0], self.rect1[1], color=self.rect_colors[0])
        if not self.rect2 is None:
            layer.rectangle(self.rect2[0], self.rect2[1], color=self.rect_colors[1])
        if not self.dot is None:
            layer.circle((self.rect1[0][0]+self.dot[0], self.rect1[0][1]+self.dot[1]), radius=2, filled=1)
        if self.which >= 0 and self.which < 4:
            x, y = self.get_frame_coords()
            layer.circle((x, y), radius=2, filled=1)
            if self._colour_order[self.which] == 'b':
                colour = Color.BLUE
            elif self._colour_order[self.which] == 'y':
                colour = Color.YELLOW
            else:
                self._logger.log('Unrecognized colour {0} for pitch area.'.format(self._colour_order[self.which]))
            layer.circle((x, y), radius=int(RADIUS*self._scale), color=colour, width=2)
            angle = self.get_angle()
            if not angle is None:
                endx = x + int(RADIUS * self._scale * math.cos(angle))
                endy = y + int(RADIUS * self._scale * math.sin(angle))
                layer.line((x, y), (endx, endy), antialias=False)
                degrees = (self._angle * 180) / math.pi
                layer.ezViewText('{0:.1f} deg'.format(degrees), (x, y-int(40*self._scale)))
        elif self.which == BALL:
            w = layer.width
            h = layer.height
            x, y = self.get_frame_coords()
            layer.line((x, 0), (x, h), antialias=False, color=Color.RED)
            layer.line((0, y), (w, y), antialias=False, color=Color.RED)


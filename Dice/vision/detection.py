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
import numpy as np
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
    shape_sizes = { 'ball': [40, 160, 175],
                    'blue': [120, 150, 280],
                    'dot': [30, 45, 60] }
    shape_sizes['yellow'] = map(lambda x: int(0.9*x), shape_sizes['blue'])

    # Areas of the robots (width). Symmetrical, allowing for some overlap.
    areas = [(0.0, 0.241), (0.207, 0.516), (0.484, 0.793), (0.759, 1.0)]

    def __init__(self, gui, threshold, processor, colour_order, scale, pitch_num, render_tlayers = True):
    
        self._render_tlayers = render_tlayers
        self._threshold = threshold
        self._processor = processor
        self._pitch_w = WIDTH
        self._pitch_h = HEIGHT
        self._gui = gui
        self._scale = scale
        self._colour_order = colour_order
        self._pitch_num = pitch_num
        self._logger = Logger('detection_errors.log')
        self._hsv_frame = None
        self._bgr_frame = None

    def detect_objects(self):
        self._pitch_w, self._pitch_h = self._processor.pitch_size
        self._hsv_frame = self._processor.get_hsv_frame()
        self._bgr_frame = self._processor.get_bgr_frame()
        experimental_frame, squares = self._find_squares()
        squares = self._sort_squares(squares)
        # robots left to right, entities[4] is ball
        entities = [Entity(self._pitch_w, self._pitch_h, self._colour_order), Entity(self._pitch_w, self._pitch_h, self._colour_order), Entity(self._pitch_w, self._pitch_h, self._colour_order), Entity(self._pitch_w, self._pitch_h, self._colour_order), Entity(self._pitch_w, self._pitch_h, self._colour_order)]
        thresholds = [None, None, None, None, None, None]
        if self._render_tlayers:
            yellow = self._bgr_frame.copy()
            blue = self._bgr_frame.copy()
        for which, square in enumerate(squares):
            if which < 4:
                entities[which] = Entity(self._pitch_w, self._pitch_h, self._colour_order, which, square,
                                         self.areas, self._scale, render_tlayers = self._render_tlayers)
                entities[which] = self._determine_angle(entities[which])

        thresholds[BALL] = self._threshold.ball(self._hsv_frame).smooth(grayscale=True)
        entities[BALL] = self.__find_entity(thresholds[BALL], BALL, self._hsv_frame)

        if self._render_tlayers:
            thresholds[DOT] = self._threshold.dotT(self._hsv_frame).smooth(grayscale=True)
            self._gui.update_layer('threshY', yellow)
            self._gui.update_layer('threshB', blue)
            self._gui.update_layer('threshR', thresholds[BALL])
            self._gui.update_layer('threshD', thresholds[DOT])
            self._gui.update_layer('experimental', experimental_frame)

        for i in range(0, 4):
            if self._colour_order[i] == 'b':
                self._gui.update_layer('blue{0}'.format(i), entities[i])
            elif self._colour_order[i] == 'y':
                self._gui.update_layer('yellow{0}'.format(i), entities[i])

        self._gui.update_layer('ball', entities[BALL])

        return entities

    def _determine_angle(self, entity):
        if entity.get_blob() is None:
            return entity
        corner_points = entity.get_blob().minRect()
        DOT_OFFSET = int(((entity.get_blob().minRectHeight() + entity.get_blob().minRectWidth())/2.0)*0.3)
        points = []
        points.append(self.get_middle_point(corner_points[0], corner_points[1]))
        points.append(self.get_middle_point(corner_points[1], corner_points[3]))
        points.append(self.get_middle_point(corner_points[0], corner_points[2]))
        points.append(self.get_middle_point(corner_points[2], corner_points[3]))
        alpha1, _, _ = self.get_line_fn(points[0], points[3])
        alpha2, _, _ = self.get_line_fn(points[1], points[2])
        points[0] = (points[0][0]-DOT_OFFSET*math.cos(alpha1), points[0][1]-DOT_OFFSET*math.sin(alpha1))
        points[3] = (points[3][0]+DOT_OFFSET*math.cos(alpha1), points[3][1]+DOT_OFFSET*math.sin(alpha1))
        points[1] = (points[1][0]-DOT_OFFSET*math.cos(alpha2), points[1][1]-DOT_OFFSET*math.sin(alpha2))
        points[2] = (points[2][0]+DOT_OFFSET*math.cos(alpha2), points[2][1]+DOT_OFFSET*math.sin(alpha2))
        values = [ (i, self._get_point_value(point)) for i, point in enumerate(points) ]
        dot_i = min(values, key=lambda x: x[1])[0]
        entity.set_dot(points[dot_i])
        center = entity.get_frame_coords()
        angle = math.atan2(points[dot_i][1] - center[1], points[dot_i][0] - center[0]) + math.pi
        a = math.cos(angle)
        b = math.sin(angle)
        angle = math.atan2(b, a)
        entity.set_angle(angle)
        return entity

    def _get_point_value(self, point):
        x, y = point
        return np.sum(self._bgr_frame.getNumpy()[x-2:x+2,y-2:y+2])

    def _find_squares(self):
        binary_frame = self._processor.get_binary_frame()
        squares = []
        if self._processor._gray_bin == 0:
            frame = self._processor.get_grayscale_frame()
        else:
            frame = binary_frame
        blobs = binary_frame.findBlobs(minsize=1000, appx_level=5)
        if not blobs is None:
            blobs.draw(color=Color.PUCE, width=2)
        try:
            square_blobs = blobs.filter([b.isSquare(0.4, 0.25) for b in blobs])
            if square_blobs:
                square_blobs.draw(color=Color.RED, width=2)
                for square in square_blobs:
                    square.drawMinRect(color=Color.LIME, width=2)
                    squares.append(square)
            frame.addDrawingLayer(binary_frame.dl())
            return (frame.applyLayers(), squares)
        except:
            return (frame, squares)

    def _sort_squares(self, squares):
        sorted_squares = [None, None, None, None]
        for i in range(0, 4):
            x_min = int(self._scale*self.areas[i][0]*self._pitch_w)
            x_max = int(self._scale*self.areas[i][1]*self._pitch_w)
            for square in squares:
                if square.minRectX() > x_min and square.minRectX() < x_max:
                    sorted_squares[i] = square
        return sorted_squares

    def __find_entity(self, threshold_img, which, image):

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
        entity = Entity(self._pitch_w, self._pitch_h, self._colour_order, which, entity_blob,
                        self.areas, self._scale, render_tlayers = self._render_tlayers)

        if which >= 0 and which < 4:
            self.__clarify_coords(entity, image)

        return entity

    def __find_entity_blob(self, image, size, dot=False):

        blobmaker = BlobMaker()
        blobs = blobmaker.extractFromBinary(image, image, minsize=size[0], maxsize=size[2])

        if blobs is None:
            return None

        size_matched_blobs = [(self.__match_size(b, size), b) for b in blobs]

        if not dot:
            size_matched_blobs = filter(lambda (_, b): b.isRectangle(tolerance=0.8), size_matched_blobs)
            
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

    def point_to_line_dist(self, p, fn):
        x, y = p
        a, b = fn
        d = abs(y - a * x - b)/math.sqrt(a * a + 1)
        return d

    def get_middle_point(self, p1, p2):
        x = int((p1[0] + p2[0])/2.0)
        y = int((p1[1] + p2[1])/2.0)
        return (x, y)

    def get_line_fn(self, p1, p2):
        x1, y1 = p1
        x2, y2 = p2
        try:
            a = (y1 - y2)/float((x1 - x2))
        except ZeroDivisionError:
            return (math.pi/2.0, None, None)
        b = y1 - a * x1
        alpha = math.atan(a)
        return (alpha, a, b)

class Entity:

    def __init__(self, pitch_w, pitch_h, colour_order, which = None, entity_blob = None,
                 areas = None, scale = None, render_tlayers = True):
        self._entity_blob = entity_blob
        self._coordinates = (-1, -1)  # coordinates in 580x320 coordinate system
        self._frame_coords = (-1, -1) # coordinates in the frame
        self._render_tlayers = render_tlayers
        self._dot_point = None
        self._angle = None
        self._scale = scale
        self._has_angle = False
        self._pitch_w = pitch_w
        self._pitch_h = pitch_h
        self._colour_order = colour_order
        self._areas = areas
        self.which = which
        
        if not entity_blob is None:
            x_frame = int(entity_blob.minRectX())
            y_frame = int(entity_blob.minRectY())
            self._frame_coords = (x_frame, y_frame)
            if which >= 0 and which < 4:
                self._has_angle = True
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

    def get_blob(self):
        return self._entity_blob

    def set_dot(self, point):
        self._dot_point = point

    def set_angle(self, angle):
        self._angle = angle

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
        if not self._dot_point is None:
            layer.circle(self._dot_point, radius=2, filled=1, color=Color.RED)
        if self.which >= 0 and self.which < 4:
            if self._colour_order[self.which] == 'b':
                colour = Color.BLUE
            elif self._colour_order[self.which] == 'y':
                colour = Color.YELLOW
            else:
                self._logger.log('Unrecognized colour {0} for pitch area.'.format(self._colour_order[self.which]))
            self._entity_blob.drawMinRect(layer, color=colour, width=5)
            angle = self.get_angle()
            if not angle is None:
                x, y = self._frame_coords
                endx = x + int(RADIUS * self._scale * math.cos(angle))
                endy = y + int(RADIUS * self._scale * math.sin(angle))
                layer.line((x, y), (endx, endy), antialias=False)
                if self._render_tlayers:
                    degrees = (self._angle * 180) / math.pi
                    layer.ezViewText('{0:.1f} deg'.format(degrees), (x, y-int(40*self._scale)))
        elif self.which == BALL:
            w = layer.width
            h = layer.height
            x, y = self.get_frame_coords()
            layer.line((x, 0), (x, h), antialias=False, color=Color.RED)
            layer.line((0, y), (w, y), antialias=False, color=Color.RED)

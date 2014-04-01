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
from logger import Logger
import util

__author__ = "Ingvaras Merkys"

BALL = 4
WIDTH = 580
HEIGHT = 320
RADIUS = 23.0
DOT_RADIUS = 8

class Detection:

    # Areas of the robots (width). Symmetrical, allowing for some overlap.
    areas = [(0.0, 0.241), (0.207, 0.516), (0.484, 0.793), (0.759, 1.0)]

    def __init__(self, gui, processor, colour_order, scale, pitch_num, render_tlayers = True):

        self._processor = processor
        self._pitch_w = WIDTH
        self._pitch_h = HEIGHT
        self._gui = gui
        self._scale = scale
        self._colour_order = colour_order
        self._pitch_num = pitch_num
        self._coord_rect = None
        self._logger = Logger('detection_errors.log')
        self._hsv_frame = None
        self._bgr_frame = None

    def detect_objects(self):
        self._hsv_frame = self._processor.get_hsv_frame()
        self._bgr_frame = self._processor.get_bgr_frame()
        squares_frame, squares = self._find_squares()
        squares = self._join_split_squares(squares)
        squares = self._sort_squares(squares)
        # robots left to right, entities[4] is ball
        entities = [Entity(self._pitch_w, self._pitch_h, self._colour_order, self._coord_rect) for i in xrange(5)]
        for which, square in enumerate(squares):
            if which < 4:
                entities[which] = Entity(self._pitch_w, self._pitch_h, self._colour_order, self._coord_rect, which, square,
                                         self.areas, self._scale)
                entities[which] = self._determine_angle(entities[which])
        ball_frame, ball_blob = self._find_ball()
        entities[BALL] = Entity(self._pitch_w, self._pitch_h, self._colour_order, self._coord_rect, BALL, ball_blob, self.areas,
                                self._scale)

        [self._gui.update_layer('robot' + str(i), entities[i]) for i in xrange(4)]
        self._gui.update_layer('threshR', ball_frame)
        self._gui.update_layer('squares', squares_frame)

        self._gui.update_layer('ball', entities[BALL])

        return entities

    def _find_ball(self):
        frame = self._processor.get_binary_frame('ball')
        blobs = frame.findBlobs(minsize=int(50*math.pow(self._scale, 2)), appx_level=5)
        if not blobs is None:
            return (frame, blobs[0])
        else:
            return (frame, None)

    def _determine_angle(self, entity):
        if entity.get_blob() is None:
            return entity
        corner_points = entity.get_blob().minRect()
        points = []
        points.append(self.get_middle_point(corner_points[0], corner_points[1]))#
        points.append(self.get_middle_point(corner_points[0], corner_points[2]))
        points.append(self.get_middle_point(corner_points[2], corner_points[3]))#
        points.append(self.get_middle_point(corner_points[1], corner_points[3]))

        alpha1, _, _ = self.get_line_fn(points[0], points[2])
        alpha2, _, _ = self.get_line_fn(points[1], points[3])
        alphas = [alpha1, alpha2]
        for i in xrange(4):
            alpha = alphas[i%2]
            c = 1
            if (alpha == -math.pi/2.0 or alpha == math.pi/2.0):
                if points[(i+2)%4][1]-points[i][1] < 0:
                    c = -1
            elif (alpha > 0 and alpha < math.pi/2.0) or (alpha < -math.pi/2.0):
                if points[(i+2)%4][0]-points[i][0] < 0:
                    c = -1
            else:
                if points[(i+2)%4][0]-points[i][0] < 0:
                    c = -1
            dot_offset = util.euclidean(points[i], points[(i+2)%4]) * 0.3
            x = points[i][0] + dot_offset * math.cos(alpha) * c
            y = points[i][1] + dot_offset * math.sin(alpha) * c
            points[i] = (x, y)
        values = [ (i, self._get_point_value(point)) for i, point in enumerate(points) ]
        dot_i = min(values, key=lambda x: x[1])[0]
        entity.set_dot((int(points[dot_i][0]), int(points[dot_i][1])))
        center = entity.get_frame_coords()
        angle = math.atan2(points[dot_i][1] - center[1], points[dot_i][0] - center[0]) + math.pi
        a = math.cos(angle)
        b = math.sin(angle)
        angle = math.atan2(b, a)
        entity.set_angle(angle)
        return entity

    def _get_point_value(self, point):
        x = int(point[0])
        y = int(point[1])
        offset = int(round(3*self._scale))
        return np.sum(self._bgr_frame.getNumpy()[x-offset:x+offset,y-offset:y+offset])

    def _find_squares(self):
        binary_frame = self._processor.get_binary_frame('squares')
        if self._processor._gray_bin == 0:
            frame = self._processor.get_grayscale_frame()
        else:
            frame = binary_frame
        blobs = binary_frame.findBlobs(minsize=int(math.pow(self._scale,2)*300), appx_level=5)
        return (frame, blobs if not blobs is None else [])

    def _join_split_squares(self, squares):
        squares_proper = []
        half_squares = []
        for i in xrange(len(squares)):
                half_size = 700*math.pow(self._scale, 2)
                if squares[i].area() < half_size:
                    half_squares.append(squares[i])
                else:
                    squares_proper.append(squares[i])
        if len(half_squares) % 2 == 1:
            half_squares.pop()
        while len(half_squares) > 0:
            min_dist = float('inf')
            min_squares = ((-1, None), (-1, None))
            for i in xrange(len(half_squares)):
                for j in xrange(i+1, len(half_squares)):
                    d = util.euclidean(half_squares[i].centroid(), half_squares[j].centroid())
                    if d < min_dist:
                        min_dist = d
                        min_squares = ((i, half_squares[i]), (j, half_squares[j]))
            if min_dist < 100*math.pow(self._scale, 2):
                joint_square = self._join_squares(min_squares[0][1], min_squares[1][1])
                squares_proper.append(joint_square)
            if min_squares[0][0] > min_squares[1][0]:
                del half_squares[min_squares[0][0]]
                del half_squares[min_squares[1][0]]
            else:
                del half_squares[min_squares[1][0]]
                del half_squares[min_squares[0][0]]
        return squares_proper

    def _join_squares(self, square1, square2):
        new_min_rect = []
        points1 = list(square1.minRect())
        points2 = list(square2.minRect())
        def min_dist_point(p, points):
            return min([(util.euclidean(p, p2), i) for i, p2 in enumerate(points)], key=lambda x: x[0])
        point_distances = [(min_dist_point(p, points2), i) for i, p in enumerate(points1)]
        # [((dist, i), j)]
        (_, p2), p1 = min(point_distances, key=lambda x: x[0][0])
        del points1[p1]
        del points2[p2]
        point_distances = [(min_dist_point(p, points2), i) for i, p in enumerate(points1)]
        (_, p2), p1 = min(point_distances, key=lambda x: x[0][0])
        del points1[p1]
        del points2[p2]
        if points1[0][0] > points1[1][0]:
            points1.reverse()
        if points2[0][0] > points2[1][0]:
            points2.reverse()
        new_min_rect.extend(points1)
        new_min_rect.extend(points2)
        return MockBlob(new_min_rect)

    def _sort_squares(self, squares):
        sorted_squares = [None, None, None, None]
        for i in xrange(4):
            x_min = int(self._scale*self.areas[i][0]*self._pitch_w)
            x_max = int(self._scale*self.areas[i][1]*self._pitch_w)
            for square in squares:
                if square.minRectX() > x_min and square.minRectX() < x_max:
                    sorted_squares[i] = square
                    break
        return sorted_squares

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

    def set_pitch_dims(self, dims):
        self._pitch_w, self._pitch_h = dims

    def set_coord_rect(self, coord_rect):
        self._coord_rect = coord_rect

class Entity:

    def __init__(self, pitch_w, pitch_h, colour_order, coord_rect, which = None, entity_blob = None,
                 areas = None, scale = 1.0):
        self._entity_blob = entity_blob
        self._coordinates = (-1, -1)  # coordinates in 580x320 coordinate system
        self._frame_coords = (-1, -1) # coordinates in the frame
        self._frame_coords_c = (-1, -1)
        self._dot_point = None
        self._angle = None
        self._scale = scale
        self._has_angle = False
        self._pitch_w = pitch_w
        self._pitch_h = pitch_h
        self._colour_order = colour_order
        self._areas = areas
        self.which = which

        (x_min, y_min), (x_max, y_max) = coord_rect
        x_min *= scale
        y_min *= scale
        x_max *= scale
        y_max *= scale
        coord_w = x_max - x_min
        coord_h = y_max - y_min

        if not entity_blob is None:
            x_frame, y_frame = (entity_blob.minRectX(), entity_blob.minRectY())
            self._frame_coords = (x_frame, y_frame)
		
            if which != BALL:
                self._has_angle = True
                self._frame_coords_c = self._perspective_correction(x_frame, y_frame)
                x = (self._frame_coords_c[0] - x_min)/coord_w*WIDTH
                y = (self._frame_coords_c[1] - y_min)/coord_h*HEIGHT
            else:
                x = (self._frame_coords[0] - x_min)/coord_w*WIDTH
                y = (self._frame_coords[1] - y_min)/coord_h*HEIGHT

            if x > WIDTH:
                x = WIDTH
            elif x < 0:
                x = 0

            if y > HEIGHT:
                y = HEIGHT
            elif y < 0:
                y = 0
          
            self._coordinates = (int(x), int(y))

    def _perspective_correction(self, x, y):
        width, height = (self._pitch_w, self._pitch_h)
        c = 720/0.635
        H = 18.25
        h = 17.5
        a = width/2.0
        b = height/2.0
        if x < a:
            d = a - x
            displacement = ((H-h)*d)/H
            x = x + displacement
        else:
            d = x - a
            displacement = ((H-h)*d)/H
            x = x - displacement
        if y < b:
            d = b - y
            displacement = ((H-h)*d)/H
            y = y + displacement
        else:
            d = y - b
            displacement = ((H-h)*d)/H
            y = y - displacement
        return (int(x), int(y))

    def get_coordinates(self):
        return self._coordinates

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

    def draw(self, layer):
        """Draw this entity to the specified layer
        If angle is true then orientation will also be drawn
        """
        if self.get_coordinates()[0] == -1: return
        if not self._dot_point is None:
            layer.circle(self._dot_point, radius=2, filled=1, color=Color.RED)
        if self.which != BALL:
            o, p = self._frame_coords_c
            layer.circle((o, p), radius=2, filled=1, color=Color.BLUE)
            if self._colour_order[self.which] == 'b':
                colour = Color.BLUE
            elif self._colour_order[self.which] == 'y':
                colour = Color.YELLOW
            else:
                self._logger.log('Unrecognized colour {0} for pitch area.'.format(self._colour_order[self.which]))
            self._entity_blob.drawMinRect(layer, color=colour, width=3)
            angle = self.get_angle()
            if not angle is None:
                x, y = self._frame_coords
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

class MockBlob:

    def __init__(self, min_rect_points):
        self._min_rect_points = min_rect_points
        self._x_coord = sum([p[0] for p in min_rect_points])/float(len(min_rect_points))
        self._y_coord = sum([p[1] for p in min_rect_points])/float(len(min_rect_points))

    def drawMinRect(self, layer, color=Color.BLACK, width=1):
        p = self._min_rect_points
        layer.line(p[0], p[1], color=color, width=width)
        layer.line(p[1], p[3], color=color, width=width)
        layer.line(p[2], p[3], color=color, width=width)
        layer.line(p[0], p[2], color=color, width=width)

    def minRectX(self):
        return self._x_coord

    def minRectY(self):
        return self._y_coord

    def minRect(self):
        return self._min_rect_points

    def minRectWidth(self):
        p = self._min_rect_points
        d1 = util.euclidean(p[0], p[1])
        d2 = util.euclidean(p[2], p[3])
        return (d1+d2)/2.0

    def minRectHeight(self):
        p = self._min_rect_points
        d1 = util.euclidean(p[1], p[3])
        d2 = util.euclidean(p[0], p[2])
        return (d1+d2)/2.0

#!/usr/bin/python
# -*- coding: utf-8 -*-

"""
Vision subsystem for System Design Project 2014, group 3.
Based on work by group 6, SDP 2013.

Gui classes

Provides the user interface.
"""

import time
import cv
import pygame
from SimpleCV import Display, DrawingLayer, Image, Blob

__author__ = "Ingvaras Merkys"

class Gui:

    _layer_sets = { 'default': ['raw', 'yellow0', 'yellow1', 'yellow2', 'yellow3', 'blue0', 'blue1', 'blue2', 'blue3', 'ball'],
                    'yellow': ['threshY', 'yellow0', 'yellow1', 'yellow2', 'yellow3'],
                    'blue': ['threshB', 'blue0', 'blue1', 'blue2', 'blue3'],
                    'yellow2': ['threshY', 'yellow0', 'yellow1', 'yellow2', 'yellow3'],
                    'blue2': ['threshB', 'blue0', 'blue1', 'blue2', 'blue3'],
                    'experimental': ['experimental'],
                    'ball': ['threshR', 'ball'],
                    'dot': ['threshD'] }

    _layers = { 'raw': None,
                'threshY': None,
                'threshB': None,
                'threshR': None,
                'threshD': None,
                'yellow0': None,
                'yellow1': None,
                'yellow2': None,
                'yellow3': None,
                'blue0': None,
                'blue1': None,
                'blue2': None,
                'blue3': None,
                'ball': None,
                'experimental': None }

    _persistent_layers = { 'mouse': None }

    def __init__(self, size=(720, 540)):

        self._current_layer_set = self._layer_sets['default']
        self._display = Display(size)
        self._event_handler = Gui.EventHandler()
        self._last_mouse_state = 0
        self._show_mouse = True
        self._last_frame = None
        self._last_frame_time = time.time()

    def __draw(self):

        iterator = iter(self._current_layer_set)
        base_layer = self._layers[iterator.next()]

        if base_layer is None:
            return

        size = base_layer.size()
        entity_layer = base_layer.dl()

        for key in iterator:
            to_draw = self._layers[key]
            if to_draw is None:
                continue
            elif isinstance(to_draw, DrawingLayer):
                base_layer.addDrawingLayer(to_draw)
            else:
                to_draw.draw(entity_layer)

        for layer in self._persistent_layers.itervalues():
            if layer is not None:
                base_layer.addDrawingLayer(layer)

        final_image = base_layer.applyLayers()
        self._display.writeFrame(final_image, fit=False)

    def __update_fps(self):

        this_frame_time = time.time()
        this_frame = this_frame_time - self._last_frame_time
        fps = 1.0 / this_frame
        self._lastFrame = this_frame
        self._last_frame_time = this_frame_time
        layer = self._layers['raw'].dl()
        layer.ezViewText('{0:.1f} fps'.format(fps), (10, 10))

    def draw_crosshair(self, pos, layer_name = None):

        size = self._layers['raw'].size()

        if layer_name is not None:
            layer = DrawingLayer(self._layers['raw'].size())
        else:
            layer = self._layers['raw'].dl()

        layer.line((0, pos[1]), (size[0], pos[1]), color=(0, 0, 255))
        layer.line((pos[0], 0), (pos[0], size[1]), color=(0, 0, 255))

        if layer_name is not None:
            self.update_layer(layer_name, layer)

    def process_update(self):
        """Draw the image to the display, and process any events
        """
        for event in pygame.event.get(pygame.KEYDOWN):
            self._event_handler.process_key(chr(event.key % 0x100))

        self._display.checkEvents()
        mouseX = self._display.mouseX
        mouseY = self._display.mouseY

        if self._show_mouse:
            self.draw_crosshair((mouseX, mouseY), 'mouse')

        mouse_left = self._display.mouseLeft
        # Only fire click event once for each click
        if mouse_left == 1 and self._last_mouse_state == 0:
            self._event_handler.process_click((mouseX, mouseY))

        self._last_mouse_state = mouse_left

        # Processing OpenCV events requires calling cv.WaitKey() with a reasonable timeout,
        # which hits our framerate hard (NOTE: Need to confirm this on DICE), so only do
        # this if the focus isn't on the pygame (image) window
        if not pygame.key.get_focused():
            c = cv.WaitKey(2)
            self._event_handler.process_key(chr(c % 0x100))

        self.__update_fps()
        self.__draw()

    def get_event_handler(self):
        return self._event_handler

    def update_layer(self, name, layer):
        """Update the layer specified by 'name'
        If the layer name is not in the known list of layers, 
        then it will be drawn regardless of the current view setting
        """
        if name in self._layers.keys():
            self._layers[name] = layer
        else:
            self._persistent_layers[name] = layer

    def switch_layer_set(self, name):

        assert name in self._layer_sets.keys(), 'Unknown layerset ' + name + '!'

        self._current_layer_set = self._layer_sets[name]

    def set_show_mouse(self, show_mouse):

        if not show_mouse:
            self.update_layer('mouse', None)

        self._show_mouse = show_mouse

    class EventHandler:
        
        def __init__(self):
            self._listeners = {}
            self._click_listener = None
        
        def process_key(self, key):
            if key in self._listeners.keys():
                self._listeners[key]()

        def process_click(self, where):
            if self._click_listener is not None:
                self._click_listener(where)
            
        def add_listener(self, key, callback):
            """Adds a function callback for a key.
            """
            assert callable(callback), '"callback" must be callable'
            self._listeners[key] = callback

        def set_click_listener(self, callback):
            """Sets a function to be called on clicking on the image.
            The function will be passed a tuple with the (x, y) of the click.

            Setting a new callback will override the last one (or pass None to clear)
            """
            assert callback is None or callable(callback), '"callback" must be callable'
            self._click_listener = callback

class ThresholdGui:

    def __init__(self, threshold_instance, gui, window=None, pitch_num=0):

        self.pitch_num = pitch_num
        if window is None:
            self.window = 'Threshold Adjustments'
            cv.NamedWindow(self.window, 0)
        else:
            self.window = window

        self._gui = gui
        self.threshold = threshold_instance
        self._show_on_gui = False
        self.__create_trackbars()
        self.__setup_key_events()
        self.change_entity('yellow')

    def __setup_key_events(self):
        """Adds key listeners to the main gui for switching between entities
        """
        def yellow(): self.change_entity('yellow')
        def blue(): self.change_entity('blue')
        def yellow2(): self.change_entity('yellow2')
        def blue2(): self.change_entity('blue2')
        def ball(): self.change_entity('ball')
        def dot(): self.change_entity('dot')
        def experimental(): self.change_entity('experimental')

        key_handler = self._gui.get_event_handler()
        key_handler.add_listener('y', yellow)
        key_handler.add_listener('b', blue)
        key_handler.add_listener('u', yellow2)
        key_handler.add_listener('n', blue2)
        key_handler.add_listener('r', ball)
        key_handler.add_listener('d', dot)
        key_handler.add_listener('e', experimental)
        key_handler.add_listener('t', self.toggle_gui)

    def __create_trackbars(self):

        cv.CreateTrackbar('H min', self.window, 0, 179, self.__on_trackbar_changed)
        cv.CreateTrackbar('S min', self.window, 0, 255, self.__on_trackbar_changed)
        cv.CreateTrackbar('V min', self.window, 0, 255, self.__on_trackbar_changed)

        cv.CreateTrackbar('H max', self.window, 0, 179, self.__on_trackbar_changed)
        cv.CreateTrackbar('S max', self.window, 0, 255, self.__on_trackbar_changed)
        cv.CreateTrackbar('V max', self.window, 0, 255, self.__on_trackbar_changed)

    def __on_trackbar_changed(self, x):

        all_values = []

        for which in ['min', 'max']:
            values = []

            for channel in ['H', 'S', 'V']:
                pos = cv.GetTrackbarPos('{0} {1}'.format(channel, which), self.window)
                values.append(pos)

            all_values.append(values)

        self.threshold.update_values(self.current_entity, all_values)

    def toggle_gui(self):
        self._show_on_gui = not self._show_on_gui
        
        if self._show_on_gui:
            self._gui.switch_layer_set(self.current_entity)
        else:
            self._gui.switch_layer_set('default')

    def change_entity(self, name):
        """Change which entity to adjust thresholding
        Can be 'blue', 'yellow', 'ball' or 'dot'
        """
        self.current_entity = name
        self.set_trackbar_values(self.threshold.get_threshold_values()[name])

        # Make sure trackbars update immediately
        cv.WaitKey(2)

        if self._show_on_gui:
            self._gui.switch_layer_set(name)

    def set_trackbar_values(self, values):
        for i, which in enumerate(['min', 'max']):
            for j, channel in enumerate(['H', 'S', 'V']):
                cv.SetTrackbarPos('{0} {1}'.format(channel, which), self.window, values[i][j])


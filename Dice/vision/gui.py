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

    _layer_sets = { 'default': ['raw', 'robot0', 'robot1', 'robot2', 'robot3', 'ball'],
                    'squares1': ['squares', 'robot0', 'robot1', 'robot2', 'robot3', 'ball'],
                    'squares2': ['squares', 'robot0', 'robot1', 'robot2', 'robot3', 'ball'],
                    'squares3': ['squares', 'robot0', 'robot1', 'robot2', 'robot3', 'ball'],
                    'squares4': ['squares', 'robot0', 'robot1', 'robot2', 'robot3', 'ball'],
                    'ball': ['threshR', 'ball'] }

    _layers = { 'raw': None,
                'threshR': None,
                'ball': None,
                'robot0': None,
                'robot1': None,
                'robot2': None,
                'robot3': None,
                'squares': None }

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
        layer = self._layers[self._current_layer_set[0]].dl()
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

    def __init__(self, processor, gui, window=None, pitch_num=0):

        self.pitch_num = pitch_num
        if window is None:
            self.window = 'Threshold Adjustments'
            cv.NamedWindow(self.window, 0)
        else:
            self.window = window

        self._gui = gui
        self._processor = processor
        self._show_on_gui = False
        self.__create_trackbars()
        self.__setup_key_events()
        self.change_entity('squares1')

    def __setup_key_events(self):
        """Adds key listeners to the main gui for switching between entities
        """
        def ball(): self.change_entity('ball')
        def squares(i):
            def s():
                self.change_entity('squares' + str(i))
            return s

        key_handler = self._gui.get_event_handler()
        key_handler.add_listener('r', ball)
        key_handler.add_listener('a', squares(1))
        key_handler.add_listener('s', squares(2))
        key_handler.add_listener('d', squares(3))
        key_handler.add_listener('f', squares(4))
        key_handler.add_listener('t', self.toggle_gui)

    def __create_trackbars(self):
        cv.CreateTrackbar('H min', self.window, 0, 179, self.__on_trackbar_changed)
        cv.CreateTrackbar('S min', self.window, 0, 255, self.__on_trackbar_changed)
        cv.CreateTrackbar('V min', self.window, 0, 255, self.__on_trackbar_changed)
        cv.CreateTrackbar('H max', self.window, 0, 179, self.__on_trackbar_changed)
        cv.CreateTrackbar('S max', self.window, 0, 255, self.__on_trackbar_changed)
        cv.CreateTrackbar('V max', self.window, 0, 255, self.__on_trackbar_changed)
        cv.CreateTrackbar('Threshold', self.window, 0, 255, self.__on_trackbar_changed)
        cv.CreateTrackbar('Brightness', self.window, 1000, 2000, self.__on_trackbar_changed)
        cv.CreateTrackbar('Contrast', self.window, 100, 2000, self.__on_trackbar_changed)
        cv.CreateTrackbar('GRAY-BINARY', self.window, 0, 1, self.__on_trackbar_changed)

    def __on_trackbar_changed(self, x):
        if self._current_entity == 'ball':
            values = [[cv.GetTrackbarPos(' '.join([j, i]), self.window) for j in ['H', 'S', 'V']] for i in ['min', 'max']]
        else:
            values = []
            values.append(cv.GetTrackbarPos('Threshold', self.window))
            values.append(cv.GetTrackbarPos('Brightness', self.window))
            values.append(cv.GetTrackbarPos('Contrast', self.window))
        self._processor.update_values(values, self._current_entity)

        pos_gray_bin = cv.GetTrackbarPos('GRAY-BINARY', self.window)
        self._processor.toggle_gray_bin(pos_gray_bin)

    def toggle_gui(self):
        self._show_on_gui = not self._show_on_gui
        
        if self._show_on_gui:
            self._gui.switch_layer_set(self._current_entity)
        else:
            self._gui.switch_layer_set('default')

    def change_entity(self, name):
        self._current_entity = name
        self.set_trackbar_values(self._processor.get_values(name))

        # Make sure trackbars update immediately
        cv.WaitKey(2)

        if self._show_on_gui:
            self._gui.switch_layer_set(name)

    def set_trackbar_values(self, values):
        [[cv.SetTrackbarPos(' '.join([j, i]), self.window, values[0][y][x]) for x, j in enumerate(['H', 'S', 'V'])] for y, i in enumerate(['min', 'max'])]
        cv.SetTrackbarPos('Threshold', self.window, values[1])
        cv.SetTrackbarPos('Brightness', self.window, values[2])
        cv.SetTrackbarPos('Contrast', self.window, values[3])


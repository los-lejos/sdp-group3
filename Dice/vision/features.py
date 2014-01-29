import math

import cv
from SimpleCV import Image, Features, DrawingLayer, BlobMaker, Color
from threshold import Threshold

class Features:

    def __init__(self, display, threshold):
        self.threshold = threshold
        self._display = display

        # Sizes of various features
        # Format: (area_min, area_expected, area_max)
        self.Sizes = { 'ball': (8, 16, 100),
                       'yellow1': (30, 54, 169),
                       'blue1': (30, 54, 166),
                       'yellow2': (30, 54, 169),
                       'blue2': (30, 54, 166) }
        self.Areas = { 'ball' : (0, 0, 256, 152),
                       'blue1': (0, 0, 65, 152),
                       'blue2': (45, 0, 90, 152),
                       'yellow1': (115, 0, 90, 152),
                       'yellow2': (190, 0, 65, 152) }
    
    def extractFeatures(self, frame):

        if (self.threshold._blur > 0 and self.threshold._displayBlur):
            frameBmp = frame.getBitmap()
            cv.Smooth(frameBmp, frameBmp, cv.CV_BLUR, self.threshold._blur)

        hsv = frame.toHSV()
        ents = { 'yellow1': None,
                 'yellow2': None,
                 'blue1': None,
                 'blue2': None,
                 'ball': None }
        yellow1 = self.threshold.yellowT(hsv.crop(self.Areas['yellow1'])).smooth(grayscale=True)
        yellow2 = self.threshold.yellowT(hsv.crop(self.Areas['yellow2'])).smooth(grayscale=True)
        blue1 = self.threshold.blueT(hsv.crop(self.Areas['blue1'])).smooth(grayscale=True)
        blue2 = self.threshold.blueT(hsv.crop(self.Areas['blue2'])).smooth(grayscale=True)
        ball = self.threshold.ball(hsv).smooth(grayscale=True)

        self._display.updateLayer('threshY', yellow1)
        self._display.updateLayer('threshB', blue1)
        self._display.updateLayer('threshR', ball)

        # assuming blues are goalkeepers
        # letting areas overlap for better detection
        # MAY be a bad idea for strikers, since they are both of the same colour
        ents['blue1'] = self.findEntity(blue1, 'blue1', hsv.crop(self.Areas['blue1']))
        ents['yellow1'] = self.findEntity(yellow1, 'yellow1', hsv.crop(self.Areas['yellow1']))
        ents['yellow2'] = self.findEntity(yellow2, 'yellow2', hsv.crop(self.Areas['yellow2']))
        ents['blue2'] = self.findEntity(blue2, 'blue2', hsv.crop(self.Areas['blue2']))
        ents['ball'] = self.findEntity(ball, 'ball', hsv)


        self._display.updateLayer('blue', ents['blue1'])
        self._display.updateLayer('yellow', ents['yellow1'])
        self._display.updateLayer('yellow', ents['yellow2'])
        self._display.updateLayer('blue', ents['blue2'])
        self._display.updateLayer('ball', ents['ball'])

        return ents

    def findEntity(self, image, which, orig):

        # Work around OpenCV crash on some nearly black images
        nonZero = cv.CountNonZero(image.getGrayscaleMatrix())
        if nonZero < 10:
            return Entity()
        
        size = self.Sizes[which]
        blobmaker = BlobMaker()
        blobs = blobmaker.extractFromBinary(image, image, minsize=size[0], maxsize=size[2])

        if blobs is None:
            return Entity()

        entityblob = None
        mindiff = 9999
        for blob in blobs:
            diff = self.sizeMatch(blob, which)
            if diff >= 0 and diff < mindiff:
                #avoiding long lines along the edges to be detected as blue blob
                if which == 'ball' or blob.isSquare(tolerance=0.8, ratiotolerance=0.4):
                    entityblob = blob
                    mindiff = diff
        
        if entityblob is None:
            return Entity()
        
        entity = Entity(which, entityblob, orig.getBitmap(), self.threshold._diff, self.Areas)

        return entity

    def sizeMatch(self, feature, which):
        expected = self.Sizes[which]

        area = feature.area()

        if (expected[0] < area < expected[2]):
            # Absolute difference from expected size:
            return abs(area-expected[1])
        
        # No match
        return -1

class Entity:

    def __init__(self, which = None, entityblob = None, image = None, diff = None, areas = None):
        
        self._angle = None
        if which == None:
            self._coordinates = (-1, -1)
            self._feature = None
            self._defaultDiff = -50
        else:
            self._feature =  entityblob
            self._hasAngle = which != 'ball'
            self._useBoundingBox = which != 'ball'
            if self._useBoundingBox:
                x, y = entityblob.coordinates()
                x += areas[which][0]
                self._coordinates = (x, y)
            else:
                self._coordinates = entityblob.centroid()
    
    def coordinates(self):
        return self._coordinates

    def move (self, (cx, cy), angle, distance):
        x = int(cx + distance * math.cos(angle))
        y = int(cy + distance * math.sin(angle))
        x = min(max(0, x), self._ResX)
        y = min(max(0, y), self._ResY)
        return (x, y)

    def angle(self, image = None):
        """
        Calculates the orientation of the entity
        """

        feature = self._feature

        if self._feature is None:
            return -1

        if self._angle is None:
            # Use moments to do magic things.
            # (finds precise line through blob)

            f = self._feature;
            cx, cy = f.centroid()
            m00 = f.m00
            mu11 = f.m11 - cx * f.m01
            mu20 = f.m20 - cx * f.m10
            mu02 = f.m02 - cy * f.m01
            
            # Compute the blob's covariance matrix
            # | a b |
            # | b c |
            a = mu20 / m00;
            b = mu11 / m00;
            c = mu02 / m00;
 
            # Can derive the formula for the angle from the eigenvector associated with
            # the largest eigenvalue
            self._angle = 0.5 * math.atan2(2 * b, a - c)

            # We don't actually have the direction, so roughly calculate the angle
            # using the difference between the center of the shape and the centroid,
            # and flip the previous answer if necessary
            center = (feature.minRectX(), feature.minRectY())
            roughAngle = math.atan2(center[1] - cy, center[0] - cx) 
            if abs(self._angle - roughAngle) > (math.pi / 2):
                self._angle += math.pi
            
            if image != None:
                # Trying to confirm the direction by checking points around the centroid
                dist = 4       # Distance from the centroid in px
                diff = 0.85     # Angle in rads
                angle = self._angle
                self._ResX = image.width - 1
                self._ResY = image.height - 1
                try:
                    p_centr = cv.Get2D(image, int(cy), int(cx))
                except:
                    print 'cy IS ' + str(cy)
                    print 'cx IS ' + str(cx)
                
                (x, y) = self.move((cx, cy), angle+diff, -dist)
                p_right1 = cv.Get2D(image, y, x)

                (x, y) = self.move((cx, cy), angle-diff, -dist)
                p_right2 = cv.Get2D(image, y, x)
                

                (x, y) = self.move((cx, cy), angle+diff, dist)
                p_wrong1 = cv.Get2D(image, y, x)

                (x, y) = self.move((cx, cy), angle-diff, dist)
                p_wrong2 = cv.Get2D(image, y, x)
                                            
                difference = 0.0

                for i in range(0,2): # Compare only hue and saturation
                    difference += -abs(p_centr[i]-p_right1[i])+abs(p_centr[i]-p_wrong1[i])
                    difference += -abs(p_centr[i]-p_right2[i])+abs(p_centr[i]-p_wrong2[i])

                # print difference

                if difference < self._defaultDiff: # Probably facing the wrong direction
                    # Maybe "diff < -50" or something like that would work better.
                    # TODO: Requires some testing at various conditions.
                    self._angle += math.pi
                
        return self._angle

    def draw(self, layer, angle=True):
        """
        Draw this entity to the specified layer
        If angle is true then orientation will also be drawn
        """
        feature = self._feature

        if feature is not None:
            feature.draw(layer=layer)
            center = self._coordinates
            layer.circle((int(center[0]), int(center[1])), radius=2, filled=1)
            if angle and self._hasAngle:
                angle = self.angle()
                endx = center[0] + 30 * math.cos(angle)
                endy = center[1] + 30 * math.sin(angle)

                degrees = abs(self._angle - math.pi)  / math.pi * 180 

                layer.line(center, (endx, endy), antialias=False)

                """
                Draw points which are considered in direction confirmation:

                dist = 13
                diff = 0.85

                (endx, endy) = self.move((center[0], center[1]), angle+diff, -dist)
                layer.circle((endx, endy), radius=2, filled=1)

                (endx, endy) = self.move((center[0], center[1]), angle-diff, -dist)
                layer.circle((endx, endy), radius=2, filled=1)

                (endx, endy) = self.move((center[0], center[1]), angle+diff, dist)
                layer.circle((endx, endy), radius=2, filled=1)

                (endx, endy) = self.move((center[0], center[1]), angle-diff, dist)
                layer.circle((endx, endy), radius=2, filled=1)
                """


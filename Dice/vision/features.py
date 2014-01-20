import math

import cv
from SimpleCV import Image, Features, DrawingLayer, BlobMaker, Color
from threshold import Threshold

class Features:
    # Sizes of various features
    # Format: (area_min, area_expected, area_max)

    Sizes = { 'ball'     : (35, 131, 300),
          'yellow'         : (200, 510, 1000),
          'blue'         : (100, 400, 800),
        }

    def __init__(self, display, threshold):
        self.threshold = threshold
        self._display = display

    def extractFeatures(self, frame):

        if (self.threshold._blur > 0 and self.threshold._displayBlur):
            frameBmp = frame.getBitmap()
            cv.Smooth(frameBmp, frameBmp, cv.CV_BLUR, self.threshold._blur)

        hsv = frame.toHSV()
        ents = {'yellow': None, 'blue': None, 'ball': None}
        yellow = self.threshold.yellowT(hsv).smooth(grayscale=True)
        blue = self.threshold.blueT(hsv).smooth(grayscale=True)
        ball = self.threshold.ball(hsv).smooth(grayscale=True)

        self._display.updateLayer('threshY', yellow)
        self._display.updateLayer('threshB', blue)
        self._display.updateLayer('threshR', ball)

        ents['yellow'] = self.findEntity(yellow, 'yellow', hsv)
        ents['blue'] = self.findEntity(blue, 'blue', hsv)
        ents['ball'] = self.findEntity(ball, 'ball', hsv)

        self._display.updateLayer('yellow', ents['yellow'])
        self._display.updateLayer('blue', ents['blue'])
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
                entityblob = blob
                mindiff = diff
        
        if entityblob is None:
            return Entity()
        
        notBall = which != 'ball'
        entity = Entity.fromFeature(entityblob, notBall, notBall, orig.getBitmap(), self.threshold._diff)

        return entity

    def sizeMatch(self, feature, which):
        expected = self.Sizes[which]

        area = feature.area()
        
        #print which, area

        if (expected[0] < area < expected[2]):
            # Absolute difference from expected size:
            return abs(area-expected[1])
        
        # No match
        return -1

class Entity:
    @classmethod
    def fromFeature(cls, feature, hasAngle, useBoundingBox = True, image = None, diff = -50):
        entity = Entity(hasAngle)
        if useBoundingBox:
            entity._coordinates = feature.coordinates()
        else:
            entity._coordinates = feature.centroid()
        
        entity._feature = feature
        entity._defaultDiff = diff

        if hasAngle:
            entity._angle = entity.angle(image)

        return entity

    def __init__(self, hasAngle=True):
        """
        hasAngle = True if it makes sense for this entity to have an angle
        i.e. it isn't a ball
        """

        self._coordinates = (-1, -1)
        self._hasAngle = hasAngle
        self._angle = None
        self._feature = None
        self._defaultDiff = -50
    
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
                dist = 13       # Distance from the centroid in px
                diff = 0.85     # Angle in rads
                angle = self._angle
                self._ResX = image.width - 1
                self._ResY = image.height - 1
                p_centr = cv.Get2D(image, int(cy), int(cx))
                
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


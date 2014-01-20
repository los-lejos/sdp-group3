import cv
import os
import util
from SimpleCV import Image, ColorSpace

class Threshold:
    
    # File for storing temporary threshold defaults
    filepathThresh = os.path.join('data', 'threshdefaults_{0}')
    filepathBlur = os.path.join('data', 'blurdefaults_{0}')

    def __init__(self, pitch, resetThresholds, displayBlur, normalizeAtStartup):
        
        self._pitch = pitch
        self._resetThresholds = resetThresholds
        self._displayBlur = displayBlur
        self._normalizeAtStartup = normalizeAtStartup
        self.__getDefaults()
        
    def __getDefaults(self):
        self._values = None
        # Normalizing at startup?
        self._normalize = self._normalizeAtStartup
        self._normalDiff = 0
        # Average HSV value for normalization
        self._normalVal = 111

        pathThresh = self.filepathThresh.format(self._pitch)
        self._values = util.loadFromFile(pathThresh)

        if (self._values is None) or (self._resetThresholds):
            self._values = dict(defaults[self._pitch])
            
        # Blur? FPS drops from 17 to 15.
        self._blur = None
        pathBlur = self.filepathBlur.format(self._pitch)
        self._blur = util.loadFromFile(pathBlur)
        
        if (self._blur is None) or (self._resetThresholds):
            self._blur = defaultBlur[self._pitch]

        self._diff = defaultDiff[self._pitch]
        
            
    def __saveDefaults(self):
        util.dumpToFile(self._values, self.filepathThresh.format(self._pitch))
        util.dumpToFile(self._blur, self.filepathBlur.format(self._pitch))
        

    def yellowT(self, frame):
        return self.threshold(frame,
            [self._values['yellow'][0][0], self._values['yellow'][0][1],
            # + normalization
                self._values['yellow'][0][2] + self._normalDiff],
            self._values['yellow'][1])

    def blueT(self, frame):
        return self.threshold(frame, self._values['blue'][0], self._values['blue'][1])

    def ball(self, frame):
        return self.threshold(frame, self._values['ball'][0], self._values['ball'][1])
    
    def threshold(self, frame, threshmin, threshmax):
        """
        Performs thresholding on a frame.
        The image must be in the HSV colorspace!
        """

        assert frame.getColorSpace() == ColorSpace.HSV, "Image must be HSV!"

        iplframe = frame.getBitmap()

        if (self._blur > 0 and not self._displayBlur):
            cv.Smooth(iplframe, iplframe, cv.CV_BLUR, self._blur)
            
        if (self._normalize):
            avg = self.get_average_val(iplframe)
            self._normalDiff = int(avg - self._normalVal)
            self._normalize = False

        crossover = False
        if threshmin[0] > threshmax[0]:
            # Handle hue threshold crossing over
            # angle boundry e.g. when thresholding on red

            hMax = threshmin[0]
            hMin = threshmax[0]

            crossover = True
            threshmax2 = [hMin, threshmax[1], threshmax[2]]
            threshmin = [hMax, threshmin[1], threshmin[2]] 
            threshmax = [255, threshmax[1], threshmax[2]]
            threshmin2 = [0, threshmin[1], threshmin[2]]

        iplresult = cv.CreateImage(cv.GetSize(iplframe), frame.depth, 1)
        cv.InRangeS(iplframe, threshmin, threshmax, iplresult)

        result = Image(iplresult)

        if crossover:
            iplresult2 = cv.CreateImage(cv.GetSize(iplframe), frame.depth, 1)
            cv.InRangeS(iplframe, threshmin2, threshmax2, iplresult2)
            
            result = result + Image(iplresult2)

        return result

    def updateValues(self, entity, newValues):
        self._values[entity] = newValues
        
        self.__saveDefaults()

    def updateBlur(self, blur):
        self._blur = blur
        
        self.__saveDefaults()
        
    def normalizeImg(self):
        self._normalize = True
    
    def get_average_val(self, frame):
        sum = 0
        c = 0
        w = frame.width
        h = frame.height
        # Only look at the middle of the frame:
        for i in range(w/20, 19*w/20):
            for j in range(h/5, 4*h/5):
                s = cv.Get2D(frame, j, i)
                sum += s[2]
                c += 1
        avg = sum / c
        return avg

"""
defaults[0] for the main pitch, and defaults[1] for the other table
"""
defaults =[
        {
        'yellow' : [[20, 56, 175], [36, 255, 255]],
        'blue' : [[80,  57,  74], [115, 255, 255]],
        'ball' : [[0, 160, 90], [10, 255, 255]]
        },
        {
        'yellow' : [[11, 32, 198], [42, 255, 255]],
        'blue' : [[84, 80, 17], [139, 122, 255]],
        'ball' : [[2, 126, 140], [15, 255, 255]]
        }]


# defaultBlur[0] for the main pitch and defaultBlur[1] for the other one
defaultBlur = [3, 3]

# defaultDiff[0] for the main pitch and defaultDiff[1] for the other one
# Used in features.py
defaultDiff = [-20, -50]


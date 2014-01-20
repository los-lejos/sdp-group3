class Filter:
    def __init__(self, disable=False):
        self.coords = dict()
        self.ballIsLost = False
        self.lastBallCoords = [-1, -1, -1]
        self.lastRobotCoords = [-1, -1, -1]
        self.dribbler = '-'
        self.minDist = 2000
        self.dribblingDist = 250 # TODO: test this value
        self.disable = disable
        pass

    def change(self, name, x, y, angle):
        self.coords[name] = [x, y, angle]

    def update(self):
        if self.disable:
            return self.coords
        if self.coords['ball'][0] != -1: # the ball is visible
            self.lastBallCoords = self.coords['ball']
            self.ballIsLost = False
            return self.coords
        else:
            if (not self.ballIsLost):
                self.ballIsLost = True
                dYellow = self.findDist(self.lastBallCoords, self.coords['yellow'])
                dBlue = self.findDist(self.lastBallCoords, self.coords['blue'])
                print 'dYellow', dYellow
                print 'dBlue', dBlue
                print self.coords
                print self.lastBallCoords
                if (min(dYellow, dBlue) < self.minDist):
                    if (dYellow < dBlue):
                        self.dribbler = 'yellow'
                    else:
                        self.dribbler = 'blue'
                else:
                    self.dribbler = '-'
                    return self.coords
                self.lastRobotCoords = self.coords[self.dribbler]
                if (self.correct(self.dribbler)):
                    self.lastBallCoords = self.moveBallToRobot(self.lastBallCoords, self.lastRobotCoords)
            if (self.correct(self.dribbler)):
                if (not self.coords[self.dribbler][0] == -1):
                    print (self.dribbler, 'is dribbling!')
                    self.coords['ball'] = self.findBallCoords(self.lastBallCoords, self.lastRobotCoords, self.coords[self.dribbler])
            return self.coords

    def findDist(self, p1, p2):
        return (p1[0]-p2[0])**2+(p1[1]-p2[1])**2

    def findBallCoords(self, oldBall, oldRobot, newRobot):
        x = oldBall[0] - oldRobot[0] + newRobot[0]
        y = oldBall[1] - oldRobot[1] + newRobot[1]
        return [x, y, 0]

    # If the ball is lost, assume it is self.dribblingDist away from the nearest robot.
    def moveBallToRobot(self, ballCoords, robotCoords):
        realDist = self.findDist(ballCoords, robotCoords)
        if (realDist == 0):
            # Do not move the ball
            return ballCoords
        if (realDist < self.dribblingDist):
            # Close enough
            return ballCoords
        k = self.dribblingDist*1.0/realDist
        # relative coordinates:
        x = robotCoords[0] - ballCoords[0]
        y = robotCoords[1] - ballCoords[1]
        # new relative coordinates:
        x2 = k*x
        y2 = k*y
        # new absolute coordinates:
        ballCoords[0] = robotCoords[0] - x2
        ballCoords[1] = robotCoords[1] - y2
        return ballCoords

    def correct(self, robot):
        return (robot == 'yellow' or robot == 'blue')


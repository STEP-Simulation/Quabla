from PlotLandingScatter.judge_inside.judge_inside import JudgeInside
import numpy as np

class JudgeInsideCircle(JudgeInside):
    def __init__(self, center_point, radius):
        self.center_point = center_point
        self.radius = radius


    def judge_inside(self, point):
        x = point[0]
        y = point[1]

        distance = np.sqrt((x - self.center_point[0])**2 + (y - self.center_point[1])**2)
        judge = True if distance < self.radius else False
        return judge

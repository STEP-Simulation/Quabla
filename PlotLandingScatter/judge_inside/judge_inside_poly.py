from PlotLandingScatter.judge_inside.judge_inside import JudgeInside
import numpy as np

class JudgeInsidePoly(JudgeInside):
    def __init__(self, poly_point):
        poly_point_list = poly_point.tolist()
        self.length = len(poly_point_list)
        if poly_point_list[0] == poly_point_list[-1]:
            pass
        else:
            poly_point_list.append(poly_point_list[0])
        self.poly_point = np.array(poly_point_list)
    def judge_inside(self, point):
        # Crossing Number Algorithmにより実装
        x = point[0]
        y = point[1]

        cn = 0

        for i in range(self.length):
            if ((self.poly_point[i, 1] <= y and self.poly_point[i+1, 1] > y)
                or (self.poly_point[i, 1] > y and self.poly_point[i+1, 1] <= y)):
                vt = (y - self.poly_point[i,1]) / (self.poly_point[i+1,1] - self.poly_point[i,1])
                if x < (self.poly_point[i,0] + ( vt * (self.poly_point[i+1, 0] - self.poly_point[i,0]))):
                    cn += 1
        judge = False if cn % 2 == 0 else True
        return judge
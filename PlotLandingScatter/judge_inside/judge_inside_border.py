from PlotLandingScatter.judge_inside.judge_inside import JudgeInside
#from judge_inside import JudgeInside
import numpy as np
import PlotLandingScatter.coordinate as cd

class JudgeInsideBorder(JudgeInside):
    def __init__(self, edge_point1, edge_point2, point_center):
        self.edge_point1 = edge_point1
        self.edge_point2 = edge_point2
        self.x1 = edge_point1[0]
        self.x2 = edge_point2[0]
        self.y1 = edge_point1[1]
        self.y2 = edge_point2[1]
        self.dx = edge_point2[0] - edge_point1[0]
        self.dy = edge_point2[1] - edge_point1[1]
        self.normal_vector_ref = self.normal_vector(point_center)

    def normal_vector(self, point):
        x = point[0]
        y = point[1]
        k = ((x - self.x1) * self.dx + (y - self.y1) * self.dy) / (self.dx**2 + self.dy**2)
        normal_vec = np.array([x - self.x1, y - self.y1]) - k * np.array([self.dx, self.dy])
        normal_vec /= np.linalg.norm(normal_vec)
        return normal_vec

    def judge_inside(self, point):
        x = point[0]
        y = point[1]

        normal_vec = self.normal_vector(np.array([x, y]))
        #境界線が保安円の中心と同じ方向を向いているかどうかで判断
        judge = True if self.normal_vector_ref.dot(normal_vec) > 0 else False
        return judge


if __name__ == '__main__':
    point_launch_LLH = np.array([40.242865, 140.010450, 0.0])
    edge_list = [[40.243015, 140.007566, 0.0],
            [40.235585, 140.005619, 0.0]]
    edge1, edge2 = np.array([cd.LLH2ENU(point_launch_LLH, edge) for edge in edge_list])
    center_ENU = cd.LLH2ENU(point_launch_LLH, np.array([40.245567, 139.993297, 0.0]))
    jib = JudgeInsideBorder(edge1, edge2, center_ENU)
    point = np.array([-57.4459766, 155.3433779])
    judge = jib.judge_inside(point)
    print(judge)

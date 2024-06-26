import numpy as np
import csv

class GetLandingPoint:
    def __init__(self, windmap_filepath):
        
        with open(windmap_filepath, 'r') as windmap_file:
            reader = csv.reader(windmap_file)
            self.wind_map_array = np.array([row for row in reader]) # 2次元配列のndarray型
            self.row, self.col = self.wind_map_array.shape
            self.row = int((self.row - 1) / 2)
        
        self.num_speed = self.row
        self.num_angle = self.col - 4

        self.x_array = np.zeros((self.num_speed, self.num_angle + 1))
        self.y_array = np.zeros((self.num_speed, self.num_angle + 1))

        for i in range(self.row):
            self.y_array[i, :] = self.wind_map_array[2*i + 1, 2:self.col-1].astype(np.float64)
            self.x_array[i, :] = self.wind_map_array[2*(i + 1), 2:self.col-1].astype(np.float64)
        
        self.pos_ENU_array = np.zeros((self.num_speed, self.num_angle + 1, 3))
        self.pos_ENU_array[:,:,0] = np.array([x for x in self.x_array])
        self.pos_ENU_array[:,:,1] = np.array([y for y in self.y_array])

    def get_wind_name(self):
        wind_name_array = []
        for i in range(self.row):
            wind_name_array.append(self.wind_map_array[2*i + 1, 0])

        return np.array(wind_name_array)

if __name__ == '__main__':
    filepath = '../Result_multi_sample/trajectory70.0[deg].csv'
    glp = GetLandingPoint(filepath)
    wind_name_array = glp.get_wind_name()
    print(wind_name_array)

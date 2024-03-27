import matplotlib.pyplot as plt
import matplotlib.cm as cm
import matplotlib.patches as patches
import numpy as np
from PIL import Image
import simplekml
from polycircles import polycircles
import PlotLandingScatter.coordinate as cd
from PlotLandingScatter.launch_site.launch_site import trans_matrix

class PlotGraph:

    def __init__(self, title, result_dir, get_landing_point):
        self.result_dir = result_dir

        self.pos_ENU_array = get_landing_point.pos_ENU_array
        self.row = get_landing_point.num_speed

        self.x_array = get_landing_point.x_array
        self.y_array = get_landing_point.y_array

        self.wind_array = get_landing_point.get_wind_name()
        self.title = title

        self.fig = plt.figure(title, figsize=(8, 8))
        self.ax = self.fig.add_subplot()
        self.ax.set_title(title)

    def plot_scatter(self, img, xlim, ylim, color_cm, magnetic_dec):

        # self.ax.plot(0.0, 0.0, color='r', marker='o', markersize=3)
        self.ax.scatter(0.0, 0.0, color='r', marker='o', label='Launch point')
        i = 0
        for x, y in zip(self.x_array, self.y_array):
            self.ax.plot(x, y,label=str(self.wind_array[i]), color=color_cm(i / self.row), marker='o')
            i += 1
        self.ax.set_aspect('equal')
        self.ax.set_xlim(xlim[0], xlim[1])
        self.ax.set_ylim(ylim[0], ylim[1])
        self.ax.imshow(Image.open(img).rotate(magnetic_dec), extent=(xlim[0], xlim[1], ylim[0], ylim[1]), aspect='equal')

    
    def plot_circle(self, point_center_circle, radius_):
        self.ax.plot(point_center_circle[0], point_center_circle[1], color='r', marker='o', markersize=3)
        self.ax.add_patch(patches.Circle(xy=(point_center_circle[0], point_center_circle[1]), radius=radius_, ls='dashed', ec='y', fc='None'))

    def plot_polygon(self, apex):
        apex_list = apex.tolist()
        if apex_list[-1] == apex_list[0]:
            pass
        else :
            apex_list.append(apex_list[0])
        apex_ = np.array(apex_list)
        self.ax.plot(apex_[:, 0], apex_[:, 1], color='y', ls='dashed')
        a = 1

    def plot_line(self, edge1, edge2):
        edge_x = np.array([edge1[0], edge2[0]])
        edge_y = np.array([edge1[1], edge2[1]])
        self.ax.plot(edge_x, edge_y, color='y', ls='dashed')

    def grid_on(self):
        self.ax.grid(ls='--', alpha=0.6)

    def save_fig(self):
        self.ax.legend()
        self.fig.savefig(self.result_dir + '/' + self.title + '.jpg')

    def output_kml(self, launch_LLH, magnetic_dec, color_cm, radius, safety_line1, safety_line2, safety_circle, safety_area, safety_exist):
        kml = simplekml.Kml()
        matrix = trans_matrix(- np.deg2rad(magnetic_dec))
        #pos_ENU_array = np.array([matrix.dot(pos_ENU) for pos_ENU in pos_ENU_row for pos_ENU_row in self.pos_ENU_array])
        #pos_ENU_array = np.array([matrix.dot(pos_ENU) for pos_ENU in self.pos_ENU_array])
        
        i = 0
        for pos_ENU in self.pos_ENU_array:
            pos_ENU = np.array([matrix.dot(point) for point in pos_ENU])
            pos_LLH = [cd.ENU2LLHforKml(launch_LLH, point) for point in pos_ENU]
            linestring = kml.newlinestring(name=str(self.wind_array[i]))
            r = int(color_cm(i/len(self.pos_ENU_array))[0] * 255)
            g = int(color_cm(i/len(self.pos_ENU_array))[1] * 255)
            b = int(color_cm(i/len(self.pos_ENU_array))[2] * 255)
            # r, g, b = int(color_cm(i/len(self.pos_ENU_array)) * 255)
            # linestring.style.linestyle.color = kml_color
            linestring.style.linestyle.color = simplekml.Color.rgb(r, g, b)
            linestring.style.linestyle.width = 2
            linestring.coords = pos_LLH
            i += 1
        #kml.save(self.result_dir + '/' + self.title + '.kml')

        if safety_exist == True:
            if radius != 0:
                lin = kml.newlinestring(name="breakwater line")
                lin.coords = [(safety_line1[1], safety_line1[0],1),(safety_line2[1], safety_line2[0],1)]
                polycircle = polycircles.Polycircle(safety_circle[0], safety_circle[1], radius, number_of_vertices=36)
                pol = kml.newpolygon(name="Safety Circle",
                                            outerboundaryis=polycircle.to_kml())
                pol.style.polystyle.color = \
                        simplekml.Color.changealphaint(60, simplekml.Color.white) # ここをいじることで円の色を変更できる
                pol.style.linestyle.color = simplekml.Color.gold
                lin.style.linestyle.width = 3
                lin.altitudemode = simplekml.AltitudeMode.relativetoground
                lin.style.linestyle.color = \
                    simplekml.Color.changealphaint(255, simplekml.Color.gold)
            elif safety_circle == 0:
                safety_area = np.vstack([safety_area,safety_area[0]])
                lin = kml.newlinestring(name="breakwater line")
                lin.coords = (safety_area[ : ,[1,0,2]])
                lin.style.linestyle.width = 3
                lin.altitudemode = simplekml.AltitudeMode.relativetoground
                lin.style.linestyle.color = \
                        simplekml.Color.changealphaint(255, simplekml.Color.gold)


        kml.save(self.result_dir + '/' + self.title + '.kml')
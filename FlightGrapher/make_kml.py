import numpy as np
import simplekml
from polycircles import polycircles
import os

def getparachutepoint(point):
    global point_parachute
    point_parachute = point

def gettrajectorypoint(point):
    global point_trajectory
    point_trajectory = point

def makekml(flightlog_path, safety_circle, radius, safety_area, safety_line1, safety_line2, safety_exist):

    kml = simplekml.Kml()

    parachute = kml.newpoint(name = "parachute", coords=[(point_parachute[1], point_parachute[0])])
    parachute.style.labelstyle.color = simplekml.Color.red
    trajectory = kml.newpoint(name = "trajectory", coords=[(point_trajectory[1], point_trajectory[0])])
    trajectory.style.labelstyle.color = simplekml.Color.blue

    if safety_exist == True:
        if radius != 0:
            lin = kml.newlinestring(name="breakwater line")
            lin.coords = [(safety_line1[1], safety_line1[0],1),(safety_line2[1], safety_line2[0],1)]
            polycircle = polycircles.Polycircle(safety_circle[0], safety_circle[1], radius, number_of_vertices=36)
            pol = kml.newpolygon(name="Safety Circle",
                                            outerboundaryis=polycircle.to_kml())
            pol.style.polystyle.color = \
                    simplekml.Color.changealphaint(60, simplekml.Color.aqua) # ここをいじることで円の色を変更できる
            lin.style.linestyle.width = 3
            lin.altitudemode = simplekml.AltitudeMode.relativetoground
            lin.style.linestyle.color = \
                 simplekml.Color.changealphaint(255, simplekml.Color.aqua)
        elif safety_circle.all() == 0:
            safety_area = np.vstack([safety_area,safety_area[0]])
            lin = kml.newlinestring(name="breakwater line")
            lin.coords = (safety_area[ : ,[1,0,2]])
            lin.style.linestyle.width = 3
            lin.altitudemode = simplekml.AltitudeMode.relativetoground
            lin.style.linestyle.color = \
                    simplekml.Color.changealphaint(255, simplekml.Color.aqua)

    kml.save(flightlog_path + os.sep + '落下地点.kml')

def post_kml(log_LLH, result_dir, name):
    lat_log = log_LLH[:, 0]
    lon_log = log_LLH[:, 1]
    height_log = log_LLH[:, 2]
    kml = simplekml.Kml(open=1)
    line = kml.newlinestring()
    line.style.linestyle.width = 5
    line.style.linestyle.color = simplekml.Color.red
    line.extrude = 1
    line.altitudemode = simplekml.AltitudeMode.absolute
    coords = []
    i = 0
    for lat, lon, height in zip(lat_log, lon_log, height_log):
        if i%10 == 0:
            coords.append([lon, lat, height])
        i = i+1
    line.coords = coords
    line.style.linestyle.colormode = simplekml.ColorMode.random
    kml.save(result_dir + '/' + name + '_trajectory.kml')

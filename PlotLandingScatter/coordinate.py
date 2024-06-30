#ref 宇宙往還機飛行シミュレーションプログラム
import numpy as np
from matplotlib.pyplot import ylim

#LLH :[latitude, longtitude, height] = [緯度,経度,高度]

def LLH2ECEF(LLH):
    lat =np.deg2rad(LLH[0]) #rad
    lon = np.deg2rad(LLH[1]) #rad
    height = LLH[2] #m

    #a : [m] radius at
    a = 6378137.0
    #f : oblateness
    f = 1.0 / 298.257223563
    #e_square : sq8are of eccentricit y
    e_square = 2.0*f - f**2
    N = a / np.sqrt(1 - e_square*(np.sin(lat)**2))

    x = (N + height) * np.cos(lat) * np.cos(lon)
    y = (N + height) * np.cos(lat) * np.sin(lon)
    z = (N*(1.0 - e_square) + height) * np.sin(lat)

    ECEF = np.array([x, y, z])

    return ECEF

def ECEF2LLH(ECEF):
    x = ECEF[0]
    y = ECEF[1]
    z = ECEF[2]

    p = np.sqrt(x**2 + y**2)
    a = 6378137.0
    f = 1.0 / 298.257223563
    b = a * (1.0 - f)
    e_square = 2.0*f - f**2
    edash_square = e_square * (a**2 / b**2)
    theta = np.arctan2(z*a, p*b)

    lat = np.arctan2(z + edash_square*b*np.power(np.sin(theta),3), p - e_square*a*np.power(np.cos(theta),3))
    lon = np.arctan2(y, x)
    N = a / np.sqrt(1 - e_square*(np.sin(lat)**2))
    height = p/np.cos(lat) - N

    LLH = np.array([np.rad2deg(lat), np.rad2deg(lon), height]) #rad

    return LLH

def ECEF2NED(launch_LLH):
    #lat, lon, height
    lat = launch_LLH[0] #rad
    lon = launch_LLH[1] #rad

    DCM_ECEF2NED = np.zeros((3,3))
    DCM_ECEF2NED[0,0:3] = [-np.cos(lon)*np.sin(lat), -np.sin(lon)*np.sin(lat), np.cos(lat)]
    DCM_ECEF2NED[1,0:3] = [-np.sin(lon), np.cos(lon), 0.0]
    DCM_ECEF2NED[2,0:3] = [-np.cos(lon)*np.cos(lat), -np.sin(lon)*np.cos(lat), -np.sin(lat)]

    return DCM_ECEF2NED

def LLH2ENU(launch_LLH,point_LLH):
    launch_ECEF = LLH2ECEF(launch_LLH)
    point_ECEF = LLH2ECEF(point_LLH)

    lat = np.deg2rad(launch_LLH[0])
    lon = np.deg2rad(launch_LLH[1])
    height = launch_LLH[2]
    LLH = np.array([lat,lon,height])

    DCM_ECEF2NED = ECEF2NED(LLH)
    Pos_NED = DCM_ECEF2NED.dot(point_ECEF - launch_ECEF)

    Pos_graph_range_ENU = np.array([Pos_NED[1], Pos_NED[0], - Pos_NED[2]])
    return Pos_graph_range_ENU

def ENU2LLH(launch_LLH, pos_NED):
    
    # east = point_ENU[0]
    # north = point_ENU[1]
    # up = point_ENU[2]

    # Pos_NED = np.array([north, east, - up])

    lat = np.deg2rad(launch_LLH[0])
    lon = np.deg2rad(launch_LLH[1])
    height = launch_LLH[2]
    LLH = np.array([lat, lon, height])#rad rad m

    launch_ECEF = LLH2ECEF(launch_LLH)
    DCM_NED2ECEF = ECEF2NED(LLH)
    point_ECEF = np.dot(pos_NED,DCM_NED2ECEF) + launch_ECEF

    point_LLH = ECEF2LLH(point_ECEF)
    return point_LLH

def ENU2LLHforKml(launch_LLH, pos_NED):
    # east = point_ENU[0]
    # north = point_ENU[1]
    # up = point_ENU[2]

    # Pos_NED = np.array([north, east, - up])

    lat = np.deg2rad(launch_LLH[0])
    lon = np.deg2rad(launch_LLH[1])
    height = launch_LLH[2]
    LLH = np.array([lat, lon, height])#rad rad m

    launch_ECEF = LLH2ECEF(launch_LLH)
    DCM_NED2ECEF = ECEF2NED(LLH).transpose()
    point_ECEF = DCM_NED2ECEF.dot(pos_NED) + launch_ECEF

    point_LLH = ECEF2LLH(point_ECEF)
    return np.array([point_LLH[1], point_LLH[0], 0.0])

def ENU2NED(pos_ENU):

    east  = pos_ENU[0]
    north = pos_ENU[1]
    up    = pos_ENU[2]

    return np.array([north, east, - up])

if __name__ == '__main__':

    import sys
    import json
    import simplekml
    from launch_site.launch_site_info import OshimaLand, OshimaSea, NoshiroLand, NoshiroSea, OtherSite
    from launch_site.launch_site_info import LaunchSiteInfo

    launch_site_json = json.load(open('./input/launch_site.json', 'r', encoding='utf-8'))

    launch_site = '4'

    launch_site_info = LaunchSiteInfo(launch_site)
    file_name = launch_site_info.site_name

    area_range_ENU = [[launch_site_info.ylim[0], launch_site_info.xlim[0], 0.],
                      [launch_site_info.ylim[1], launch_site_info.xlim[0], 0.],
                      [launch_site_info.ylim[1], launch_site_info.xlim[1], 0.],
                      [launch_site_info.ylim[0], launch_site_info.xlim[1], 0.],
                      [launch_site_info.ylim[0], launch_site_info.xlim[0], 0.]]
    area_range_LLH = []
    for pos in area_range_ENU:
        area_range_LLH.append(ENU2LLHforKml(launch_site_info.launch_LLH, pos))

    kml = simplekml.Kml()
    linestring = kml.newlinestring(name='Area Range')
    linestring.style.linestyle.color = simplekml.Color.black
    linestring.coords = area_range_LLH

    point_launch = kml.newpoint(name='Launch')
    point_launch.style.iconstyle.color = simplekml.Color.red
    point_launch.coords = [(launch_site_info.launch_LLH[1],
                           launch_site_info.launch_LLH[0])]

    if launch_site_info.type_safety == 'polygon':
        linestring_safety = kml.newlinestring(name='Safety Area')
        linestring_safety.style.linestyle.color = simplekml.Color.gold
        safety_area_LLH_kml = []
        for llh in launch_site_info.safety_area_LLH:
            safety_area_LLH_kml.append([llh[1], llh[0]])
        safety_area_LLH_kml.append(safety_area_LLH_kml[0])
        linestring_safety.coords = safety_area_LLH_kml

    else:
        point_center = kml.newpoint(name='Center of Circle')
        point_edge1 = kml.newpoint(name='Edge 1')
        point_edge2 = kml.newpoint(name='Edge 2')
        point_center.style.iconstyle.color = simplekml.Color.black
        point_edge1.style.iconstyle.color = simplekml.Color.gold
        point_edge2.style.iconstyle.color = simplekml.Color.gold
        point_edge1.coords = [(launch_site_info.center_circle_LLH[1],
                               launch_site_info.center_circle_LLH[0])]
        point_edge1.coords = [(launch_site_info.edge1_LLH[1],
                               launch_site_info.edge1_LLH[0])]
        point_edge2.coords = [(launch_site_info.edge2_LLH[1],
                               launch_site_info.edge2_LLH[0])]

    kml.save('PlotLandingScatter/launch_site/kml/' + file_name + '.kml')
    sys.exit()

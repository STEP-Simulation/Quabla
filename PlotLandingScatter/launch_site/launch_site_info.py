import numpy as np
import os
import json

class LaunchSiteInfo:
    def __init__(self, key):
        with open('input' + os.sep + 'key_launch_site.json', mode='r', encoding='utf-8') as f:
            json_site_key = json.load(f)

        self.site_name = json_site_key.get(key)
        with open('input' + os.sep + 'launch_site.json', mode='r', encoding='utf-8') as f:
            config = json.load(f).get(self.site_name)

        self.launch_LLH = np.array(config.get('launch_LLH'))
        self.type_safety = config.get('type_safety')
        # Polygon
        if self.type_safety == 'polygon':
            self.safety_area_LLH = np.array(config.get('safety_area_LLH'))
            # Additional setting
            # self.headquarters_LLH = np.zeros(3)
            # self.fire_LLH = np.zeros(3)

        # Circle
        elif self.type_safety == 'circle':
            self.center_circle_LLH = np.array(config.get('center_circle_LLH'))
            self.radius            = np.array(config.get('radius'))
            self.edge1_LLH         = np.array(config.get('edge1_LLH'))
            self.edge2_LLH         = np.array(config.get('edge2_LLH'))
        
        self.img      = config.get('img')
        self.xlim     = config.get('xlim_ENU')
        self.ylim     = config.get('ylim_ENU')
        self.x_offset = config.get('x_offset')
        self.y_offset = config.get('y_offset')

class OshimaLand(LaunchSiteInfo):
    def __init__(self, config):
        super().__init__()
        self.site_name = 'oshima_land'
        self.launch_LLH = np.array(config.get('launch_LLH'))
        self.safety_area_LLH = np.array(config.get('safety_area_LLH'))
        self.headquarters_LLH = np.array(config.get('headquarters_LLH'))
        self.fire_LLH = np.array(config.get('fire_LLH'))
        self.img = config.get('img')
        self.xlim = config.get('xlim_ENU')
        self.ylim = config.get('ylim_ENU')
        self.x_offset = config.get('x_offset')
        self.y_offset = config.get('y_offset')

class OshimaSea(LaunchSiteInfo):
    def __init__(self, config):
        super().__init__()
        self.site_name = 'oshima_sea'
        self.launch_LLH = np.array(config.get('launch_LLH'))
        self.center_circle_LLH = np.array(config.get('center_circle_LLH'))
        self.radius = np.array(config.get('radius'))
        self.edge1_LLH = np.array(config.get('edge1_LLH'))
        self.edge2_LLH = np.array(config.get('edge2_LLH'))
        self.img = config.get('img')
        self.xlim = config.get('xlim_ENU')
        self.ylim = config.get('ylim_ENU')
        self.x_offset = config.get('x_offset')
        self.y_offset = config.get('y_offset')

class NoshiroLand(LaunchSiteInfo):
    def __init__(self, config):
        super().__init__()
        self.site_name = 'noshiro_land'
        self.launch_LLH = np.array(config.get('launch_LLH'))
        self.safety_area_LLH = np.array(config.get('safety_area_LLH'))
        self.img = config.get('img')
        self.xlim = config.get('xlim_ENU')
        self.ylim = config.get('ylim_ENU')
        self.x_offset = config.get('x_offset')
        self.y_offset = config.get('y_offset')

class NoshiroSea(LaunchSiteInfo):
    def __init__(self, config):
        super().__init__()
        self.site_name = 'noshiro_sea'
        self.launch_LLH = np.array(config.get('launch_LLH'))
        self.center_circle_LLH = np.array(config.get('center_circle_LLH'))
        self.radius = np.array(config.get('radius'))
        self.edge1_LLH = np.array(config.get('edge1_LLH'))
        self.edge2_LLH = np.array(config.get('edge2_LLH'))
        self.img = config.get('img')
        self.xlim = config.get('xlim_ENU')
        self.ylim = config.get('ylim_ENU')
        self.x_offset = config.get('x_offset')
        self.y_offset = config.get('y_offset')

class OtherSite(LaunchSiteInfo):
    def __init__(self):
        super().__init__()
        self.radius = 5000
        # self.launc
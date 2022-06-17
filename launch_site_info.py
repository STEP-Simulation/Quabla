import numpy as np

class LaunchSiteInfo(object):
    def __init__(self):
        self.launch_LLH = np.zeros(3)
        self.safety_area_LLH = np.zeros(3)
        self.headquarters_LLH = np.zeros(3)
        self.fire_LLH = np.zeros(3)
        self.center_circle_LLH = np.zeros(3)
        self.radius = 0
        self.edge1_LLH = np.zeros(3)
        self.edge2_LLH = np.zeros(3)

class OshimaLand(LaunchSiteInfo):
    def __init__(self, config):
        super().__init__()
        self.launch_LLH = np.array(config.get('launch_LLH'))
        self.safety_area_LLH = np.array(config.get('safety_area_LLH'))
        self.headquarters_LLH = np.array(config.get('headquarters_LLH'))
        self.fire_LLH = np.array(config.get('fire_LLH'))

class OshimaSea(LaunchSiteInfo):
    def __init__(self, config):
        super().__init__()
        self.launch_LLH = np.array(config.get('launch_LLH'))
        self.center_circle_LLH = np.array(config.get('center_circle_LLH'))
        self.radius = np.array(config.get('radius'))
        self.edge1_LLH = np.array(config.get('edge1_LLH'))
        self.edge2_LLH = np.array(config.get('edge2_LLH'))

class NoshiroLand(LaunchSiteInfo):
    def __init__(self, config):
        super().__init__()
        self.launch_LLH = np.array(config.get('launch_LLH'))
        self.safety_area_LLH = np.array(config.get('safety_area_LLH'))

class NoshiroSea(LaunchSiteInfo):
    def __init__(self, config):
        super().__init__()
        self.launch_LLH = np.array(config.get('launch_LLH'))
        self.center_circle_LLH = np.array(config.get('center_circle_LLH'))
        self.radius = np.array(config.get('radius'))
        self.edge1_LLH = np.array(config.get('edge1_LLH'))
        self.edge2_LLH = np.array(config.get('edge2_LLH'))

class OtherSite(LaunchSiteInfo):
    def __init__(self):
        super().__init__()
        self.radius = 5000
        # self.launc
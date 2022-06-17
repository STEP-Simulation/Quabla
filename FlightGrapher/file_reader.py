import numpy as np


class FileReader:

    def __init__(self, filepath):
        # file open
        logdata = open(filepath)
        self.logdata_array = np.loadtxt(logdata, delimiter=',', skiprows=1)
        logdata.close()

    def get_logdata(self):
        return self.logdata_array

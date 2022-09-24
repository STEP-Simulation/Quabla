import numpy as np
import pandas as pd

class FileReader:

    def __init__(self, filepath):
        # file open
        logdata = open(filepath)
        self.logdata_array = np.loadtxt(logdata, delimiter=',', skiprows=1)
        self.df_logdata = pd.read_csv(filepath)
        logdata.close()

    def get_logdata(self):
        return self.logdata_array

    def get_df(self):
        return self.df_logdata

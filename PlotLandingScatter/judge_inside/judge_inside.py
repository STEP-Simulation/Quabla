from abc import ABCMeta, abstractmethod

class JudgeInside(metaclass=ABCMeta):

    @abstractmethod
    def judge_inside(self, point):
        pass
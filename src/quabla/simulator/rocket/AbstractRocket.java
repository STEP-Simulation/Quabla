package quabla.simulator.rocket;

public abstract class AbstractRocket {
    
    public abstract double getMass(double time);
    public abstract double getCdS(double time, double altitude);
}

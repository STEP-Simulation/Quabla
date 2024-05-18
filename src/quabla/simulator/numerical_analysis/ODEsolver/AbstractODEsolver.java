package quabla.simulator.numerical_analysis.ODEsolver;

import quabla.simulator.dynamics.AbstractDynamics;
import quabla.simulator.variable.AbstractVariable;

public abstract class AbstractODEsolver {

	public abstract double[] compute(AbstractVariable variable, AbstractDynamics dyn);
	public abstract double getTimeStep();
	public abstract void setTimeStep(double timeStep);
}

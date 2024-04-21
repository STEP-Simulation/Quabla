package quabla.simulator.numerical_analysis.ODEsolver;

import quabla.simulator.dynamics.AbstractDynamics;
import quabla.simulator.dynamics.AbstractDynamicsMinuteChange;
import quabla.simulator.variable.AbstractVariable;

public abstract class AbstractODEsolver {

	//public abstract DynamicsMinuteChangeTrajectory compute(VariableTrajectory variable, AbstractDynamics dyn);

	//public abstract DynamicsMinuteChangeParachute compute(VariableParachute variable, DynamicsParachute dyn);
	public abstract AbstractDynamicsMinuteChange compute(AbstractVariable variable, AbstractDynamics dyn);
	public abstract double getTimeStep();
	public abstract void setTimeStep(double timeStep);
}

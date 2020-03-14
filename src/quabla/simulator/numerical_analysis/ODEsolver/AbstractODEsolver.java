package quabla.simulator.numerical_analysis.ODEsolver;

import quabla.simulator.dynamics.AbstractDynamics;
import quabla.simulator.dynamics.DynamicsMinuteChangeParachute;
import quabla.simulator.dynamics.DynamicsMinuteChangeTrajectory;
import quabla.simulator.dynamics.DynamicsParachute;
import quabla.simulator.variable.Variable;
import quabla.simulator.variable.VariableParachute;

public abstract class AbstractODEsolver {

	public abstract DynamicsMinuteChangeTrajectory compute(Variable variable, AbstractDynamics dyn);

	public abstract DynamicsMinuteChangeParachute compute(VariableParachute variable, DynamicsParachute dyn);
}

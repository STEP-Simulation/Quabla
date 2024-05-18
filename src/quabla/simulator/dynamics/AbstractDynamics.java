package quabla.simulator.dynamics;

import quabla.simulator.variable.AbstractVariable;

/**
 * AbstractDynamics is an abstract class of {@link quabla.simulator.dynamics.DynamicsTrajectory},
 * {@link quabla.simulator.dynamics.DynamicsOnLauncher}, {@link quabla.simulator.dynamics.DynamicsParachute},
 * {@link quabla.simulator.dynamics.DynamicsTipOff} .
 * */
public abstract class AbstractDynamics {

	// public abstract AbstractDynamicsMinuteChange calculateDynamics(AbstractVariable variable);
	public abstract double[] calculateDynamics(AbstractVariable variable);

}

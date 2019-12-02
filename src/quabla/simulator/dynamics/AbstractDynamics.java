package quabla.simulator.dynamics;

import quabla.simulator.Variable;

/**
 * AbstractDynamics is an abstract class of {@link quabla.simulator.dynamics.DynamicsTrajectory},
 * {@link quabla.simulator.dynamics.DynamicsOnLauncher}, {@link quabla.simulator.dynamics.DynamicsParachute},
 * {@link quabla.simulator.dynamics.DynamicsTipOff} .
 * */
public abstract class AbstractDynamics {

	public abstract DynamicsMinuteChange calculateDynamics(Variable variable);

}
//TODO 各クラスの実装

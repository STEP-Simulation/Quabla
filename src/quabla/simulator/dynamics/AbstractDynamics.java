package quabla.simulator.dynamics;

import quabla.simulator.Variable;

/**
 * 子クラスは,
 * DynamicsTrajectory, DynamicsOnLauncher, DynamicsTipOff, DynamicsParachute
 * */
public abstract class AbstractDynamics {

	public abstract DynamicsMinuteChange calculateDynamics(Variable variable);

}
//TODO 各クラスの実装

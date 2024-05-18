package quabla.simulator.dynamics;

import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.AbstractVariable;
import quabla.simulator.variable.OtherVariableTrajectory;

/**
 * DynamicsTrajectory is an extension of {@link quabla.simulator.dynamics.AbstractDynamics}.
 * */
// public class DynamicsTrajectory extends AbstractDynamics {
public class DynamicsTrajectory {

	OtherVariableTrajectory otherVariable;

	public DynamicsTrajectory(Rocket rocket) {
		otherVariable = new OtherVariableTrajectory(rocket);
		
	}
	
	// @Override
	public double[] calculateDynamics(AbstractVariable variable) {
		
		otherVariable.setOtherVariable(variable.getTime(), variable.getPosNED().toDouble(), variable.getVelBODY().toDouble(), variable.getOmegaBODY().toDouble(), variable.getQuat().toDouble());
		
		DynamicsMinuteChangeTrajectory delta = new DynamicsMinuteChangeTrajectory();
		delta.setDeltaPosNED(otherVariable.getVelNED());
		delta.setDeltaVelBODY(otherVariable.getAccBODY());
		delta.setDeltaOmegaBODY(otherVariable.getOmegaDot());
		delta.setDeltaQuat(otherVariable.getQuatDot());

		return delta.toDouble();
	}

}

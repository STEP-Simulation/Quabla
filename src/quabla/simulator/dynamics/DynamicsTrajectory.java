package quabla.simulator.dynamics;

import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.AbstractVariable;
import quabla.simulator.variable.OtherVariableTrajectory;

/**
 * DynamicsTrajectory is an extension of {@link quabla.simulator.dynamics.AbstractDynamics}.
 * */
public class DynamicsTrajectory extends AbstractDynamics {

	OtherVariableTrajectory otherVariable;

	private DynamicsMinuteChangeTrajectory delta;
	
	public DynamicsTrajectory(Rocket rocket) {
		otherVariable = new OtherVariableTrajectory(rocket);
		
		delta = new DynamicsMinuteChangeTrajectory();
	}

	@Override
	public DynamicsMinuteChangeTrajectory calculateDynamics(AbstractVariable variable) {

		otherVariable.setOtherVariable(variable.getTime(), variable.getPosNED().toDouble(), variable.getVelBODY().toDouble(), variable.getOmegaBODY().toDouble(), variable.getQuat().toDouble());

		delta.setDeltaPosNED(otherVariable.getVelNED());
		delta.setDeltaVelNED(otherVariable.getAccBODY());
		delta.setDeltaOmegaBODY(otherVariable.getOmegaDot());
		delta.setDeltaQuat(otherVariable.getQuatDot());

		return delta;
	}

}

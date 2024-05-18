package quabla.simulator.dynamics;

import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.AbstractVariable;
import quabla.simulator.variable.OtherVariableTipOff;

public class DynamicsTipOff extends AbstractDynamics {

	private OtherVariableTipOff otherVariable;

	public DynamicsTipOff(Rocket rocket) {
		otherVariable = new OtherVariableTipOff(rocket);
	}

	@Override
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

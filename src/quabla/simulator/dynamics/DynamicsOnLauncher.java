package quabla.simulator.dynamics;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.AbstractVariable;
import quabla.simulator.variable.OtherVariableOnLauncher;


public class DynamicsOnLauncher extends AbstractDynamics {

	// private final Rocket rocket ;
	OtherVariableOnLauncher otherVariable;

	public DynamicsOnLauncher(Rocket rocket) {
		
		otherVariable = new OtherVariableOnLauncher(rocket);

	}

	@Override
	public DynamicsMinuteChangeTrajectory calculateDynamics(AbstractVariable variable) {
		
		otherVariable.setOtherVariable(variable.getTime(), variable.getPosNED().toDouble(), variable.getVelBODY().toDouble(), variable.getOmegaBODY().toDouble(), variable.getQuat().toDouble());

		MathematicalVector velNED   = new MathematicalVector(otherVariable.getVelNED());
		MathematicalVector accBODY  = new MathematicalVector(otherVariable.getAccBODY());
		MathematicalVector omegadot = new MathematicalVector(otherVariable.getOmegaDot());
		MathematicalVector quatdot  = new MathematicalVector(otherVariable.getQuatDot());


		// Store Minute Change
		DynamicsMinuteChangeTrajectory delta = new DynamicsMinuteChangeTrajectory();
		delta.setDeltaPosNED(velNED);
		delta.setDeltaVelNED(accBODY);
		delta.setDeltaOmegaBODY(omegadot);
		delta.setDeltaQuat(quatdot);

		return delta;

	}
}

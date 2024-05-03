package quabla.simulator.dynamics;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.AbstractVariable;
import quabla.simulator.variable.OtherVariableTrajectory;

/**
 * DynamicsTrajectory is an extension of {@link quabla.simulator.dynamics.AbstractDynamics}.
 * */
public class DynamicsTrajectory extends AbstractDynamics {

	OtherVariableTrajectory otherVariable;

	private MathematicalVector velNED   ;
	private MathematicalVector accBODY  ;
	private MathematicalVector omegadot ;
	private MathematicalVector quatdot  ;

	private DynamicsMinuteChangeTrajectory delta;
	
	public DynamicsTrajectory(Rocket rocket) {
		otherVariable = new OtherVariableTrajectory(rocket);
		
		velNED   = new MathematicalVector(MathematicalVector.ZERO_3D);
		accBODY  = new MathematicalVector(MathematicalVector.ZERO_3D);
		omegadot = new MathematicalVector(MathematicalVector.ZERO_3D);
		quatdot  = new MathematicalVector(MathematicalVector.ZERO_4D);

		delta = new DynamicsMinuteChangeTrajectory();
	}

	@Override
	public DynamicsMinuteChangeTrajectory calculateDynamics(AbstractVariable variable) {

		otherVariable.setOtherVariable(variable.getTime(), variable.getPosNED().toDouble(), variable.getVelBODY().toDouble(), variable.getOmegaBODY().toDouble(), variable.getQuat().toDouble());

		velNED.set(otherVariable.getVelNED());
		accBODY.set(otherVariable.getAccBODY());
		omegadot.set(otherVariable.getOmegaDot());
		quatdot.set(otherVariable.getQuatDot());

		// Store Minute Change
		delta.setDeltaPosNED(velNED);
		delta.setDeltaVelNED(accBODY);
		delta.setDeltaOmegaBODY(omegadot);
		delta.setDeltaQuat(quatdot);

		return delta;
	}

}

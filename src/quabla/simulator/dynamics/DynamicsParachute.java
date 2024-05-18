package quabla.simulator.dynamics;

import quabla.simulator.rocket.Atmosphere;
import quabla.simulator.rocket.Payload;
import quabla.simulator.rocket.Rocket;
import quabla.simulator.rocket.wind.AbstractWind;
import quabla.simulator.variable.AbstractVariable;
import quabla.simulator.variable.OtherVariableParachute;

// public class DynamicsParachute extends AbstractDynamics{
public class DynamicsParachute {

	private OtherVariableParachute otherVariable;
	
	public DynamicsParachute(Rocket rocket) {
		
		otherVariable = new OtherVariableParachute(rocket);
	}
	
	public DynamicsParachute(Payload payload, Atmosphere atm, AbstractWind wind) {
		
		otherVariable = new OtherVariableParachute(payload, atm, wind);
	}
	
	// @Override
	public double[] calculateDynamics(AbstractVariable variable) {
		
		otherVariable.calculateOtherVariable(variable.getTime(), variable.getPosNED().toDouble(), variable.getVelDescent());
		
		DynamicsMinuteChangeParachute delta = new DynamicsMinuteChangeParachute();
		delta.setDeltaPosNED(otherVariable.getVelNED());
		delta.setDeltaVelDescent(otherVariable.getAcc());

		return delta.toDouble();
	}
}

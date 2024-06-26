package quabla.simulator.dynamics;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.rocket.Atmosphere;
import quabla.simulator.rocket.Payload;
import quabla.simulator.rocket.Rocket;
import quabla.simulator.rocket.wind.AbstractWind;
import quabla.simulator.variable.AbstractVariable;
import quabla.simulator.variable.OtherVariableParachute;

public class DynamicsParachute extends AbstractDynamics{

	private MathematicalVector velNED;
	
	private DynamicsMinuteChangeParachute delta;

	private OtherVariableParachute otherVariable;
	
	public DynamicsParachute(Rocket rocket) {

		velNED = new MathematicalVector();

		delta = new DynamicsMinuteChangeParachute();

		otherVariable = new OtherVariableParachute(rocket);
	}

	public DynamicsParachute(Payload payload, Atmosphere atm, AbstractWind wind) {
		
		velNED = new MathematicalVector();
		delta  = new DynamicsMinuteChangeParachute();
		otherVariable = new OtherVariableParachute(payload, atm, wind);
	}

	public DynamicsMinuteChangeParachute calculateDynamics(AbstractVariable variable) {

		otherVariable.calculateOtherVariable(variable.getTime(), variable.getPosNED().toDouble(), variable.getVelDescent());

		velNED.set(otherVariable.getVelNED());
		double acc = otherVariable.getAcc();

		delta.setDeltaPosNED(velNED);
		delta.setDeltaVelDescent(acc);
		return delta;
	}
}

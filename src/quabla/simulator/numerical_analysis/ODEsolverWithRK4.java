package quabla.simulator.numerical_analysis;

import quabla.simulator.ConstantVariable;
import quabla.simulator.Variable;
import quabla.simulator.dynamics.AbstractDynamics;
import quabla.simulator.dynamics.DynamicsMinuteChange;

public class ODEsolverWithRK4 {

	private ConstantVariable constant;

	public ODEsolverWithRK4(ConstantVariable constant) {
		this.constant = constant;
	}

	public DynamicsMinuteChange runRK4(Variable variable, AbstractDynamics dyn) {
		DynamicsMinuteChange k1, k2, k3, k4;
		Variable variable2;
		double h = constant.getRocket().dt;

		// k1
		k1 = dyn.calculateDynamics(variable);

		// k2
		variable2 = getVariable(variable, 0.5 * h, k1);
		k2 = dyn.calculateDynamics(variable2);

		// k3
		variable2 = getVariable(variable, 0.5 * h, k2);
		k3 = dyn.calculateDynamics(variable2);

		// k4
		variable2 = getVariable(variable, h, k3);
		k4 = dyn.calculateDynamics(variable2);

		DynamicsMinuteChange delta = new DynamicsMinuteChange();
		delta.deltaPos_ENU = k1.deltaPos_ENU.add(k2.deltaPos_ENU.multiply(2.0)).add(k3.deltaPos_ENU.multiply(2.0)).add(k4.deltaPos_ENU).multiply(h / 6.0);
		delta.deltaVel_ENU = k1.deltaVel_ENU.add(k2.deltaVel_ENU.multiply(2.0)).add(k3.deltaVel_ENU.multiply(2.0)).add(k4.deltaVel_ENU).multiply(h / 6.0);
		delta.deltaOmega_Body = k1.deltaOmega_Body.add(k2.deltaOmega_Body.multiply(2.0)).add(k3.deltaOmega_Body.multiply(2.0)).add(k4.deltaOmega_Body).multiply(h / 6.0);
		delta.deltaQuat = k1.deltaQuat.add(k2.deltaQuat.multiply(2.0)).add(k3.deltaQuat.multiply(2.0)).add(k4.deltaQuat).multiply(h / 6.0);

		return delta;
	}

	private Variable getVariable(Variable variable, double timestep, DynamicsMinuteChange kn) {
		Variable variable2 = variable.setClone();
		variable2.setTime(variable.getTime() + timestep);
		variable2.setPos_ENU(variable.getPos_ENU().add(kn.deltaPos_ENU.multiply(timestep)));
		variable2.setVel_ENU(variable.getVel_ENU().add(kn.deltaVel_ENU.multiply(timestep)));
		variable2.setOmega_Body(variable.getOmega_Body().add(kn.deltaOmega_Body.multiply(timestep)));
		variable2.setQuat(variable.getQuat().add(kn.deltaQuat.multiply(timestep)));

		return variable2;
	}

}

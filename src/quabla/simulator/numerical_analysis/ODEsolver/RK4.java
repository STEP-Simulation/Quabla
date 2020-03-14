package quabla.simulator.numerical_analysis.ODEsolver;

import quabla.simulator.ConstantVariable;
import quabla.simulator.dynamics.AbstractDynamics;
import quabla.simulator.dynamics.DynamicsMinuteChangeParachute;
import quabla.simulator.dynamics.DynamicsMinuteChangeTrajectory;
import quabla.simulator.dynamics.DynamicsParachute;
import quabla.simulator.variable.Variable;
import quabla.simulator.variable.VariableParachute;

public class RK4 extends AbstractODEsolver{

	private final double h;

	//private AbstractDynamicsMinuteChange delta;


	public RK4(ConstantVariable constant) {
		h = constant.getRocket().dt;
	}

	@Override
	public DynamicsMinuteChangeTrajectory compute(Variable variable, AbstractDynamics dyn) {
		DynamicsMinuteChangeTrajectory k1, k2, k3, k4;
		Variable variable2;

		// k1 = f(t, x)
		k1 = dyn.calculateDynamics(variable);

		// k2 = f(t + h / 2, x + k1 * h / 2)
		variable2 = getVariable(variable, 0.5 * h, k1);
		k2 = dyn.calculateDynamics(variable2);

		// k3 = f(t + h / 2, x + k2 * h / 2)
		variable2 = getVariable(variable, 0.5 * h, k2);
		k3 = dyn.calculateDynamics(variable2);

		// k4 = f(t + h, x + k3 * h)
		variable2 = getVariable(variable, h, k3);
		k4 = dyn.calculateDynamics(variable2);

		DynamicsMinuteChangeTrajectory delta = new DynamicsMinuteChangeTrajectory();
		//deltaの次元の関係上，variable更新時にhをかける
		// delta = (k1 + 2 * k2 + 2 * k3 + k4) / 6
		// x_i+1 = x_i + delta * h
		delta.setDeltaPos_ENU(
				k1.getDeltaPos_ENU()
				.add(k2.getDeltaPos_ENU().multiply(2.0))
				.add(k3.getDeltaPos_ENU().multiply(2.0))
				.add(k4.getDeltaPos_ENU()).multiply(1 / 6.0));
		delta.setDeltaVelENU(
				k1.getDeltaVel_ENU()
				.add(k2.getDeltaVel_ENU().multiply(2.0))
				.add(k3.getDeltaVel_ENU().multiply(2.0))
				.add(k4.getDeltaVel_ENU()).multiply(1 / 6.0));
		delta.setDeltaOmegaBODY(
				k1.getDeltaOmega_Body()
				.add(k2.getDeltaOmega_Body().multiply(2.0))
				.add(k3.getDeltaOmega_Body().multiply(2.0))
				.add(k4.getDeltaOmega_Body()).multiply(1 / 6.0));
		delta.setDeltaQuat(
				k1.getDeltaQuat()
				.add(k2.getDeltaQuat().multiply(2.0))
				.add(k3.getDeltaQuat().multiply(2.0))
				.add(k4.getDeltaQuat()).multiply(1 / 6.0));

		return delta;
	}

	@Override
	public DynamicsMinuteChangeParachute compute(VariableParachute variable, DynamicsParachute dyn) {
		DynamicsMinuteChangeParachute k1, k2, k3, k4;
		VariableParachute variable2;

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

		//deltaの次元の関係上，variable更新時にhをかける
		DynamicsMinuteChangeParachute delta = new DynamicsMinuteChangeParachute();
		delta.setDeltaPosENU(
				k1.getDeltaPosENU()
				.add(k2.getDeltaPosENU().multiply(2.0))
				.add(k3.getDeltaPosENU().multiply(2.0))
				.add(k4.getDeltaPosENU()).multiply(1 / 6.0));
		delta.setDeltaVelDescent((
				k1.getDeltaVelDescent()
				+ 2.0 * k2.getDeltaVelDescent()
				+ 2.0 * k3.getDeltaVelDescent()
				+ k4.getDeltaVelDescent()) * (1 / 6.0));

		return delta;
	}

	private Variable getVariable(Variable variable, double timestep, DynamicsMinuteChangeTrajectory kn) {
		Variable variable2 = variable.setClone();
		variable2.setTime(variable.getTime() + timestep);
		variable2.setPos_ENU(variable.getPos_ENU().add(kn.getDeltaPos_ENU().multiply(timestep)));
		variable2.setVel_ENU(variable.getVel_ENU().add(kn.getDeltaVel_ENU().multiply(timestep)));
		variable2.setOmega_Body(variable.getOmega_Body().add(kn.getDeltaOmega_Body().multiply(timestep)));
		variable2.setQuat(variable.getQuat().add(kn.getDeltaQuat().multiply(timestep)));
		//variableにadd(knを追加)
		return variable2;
	}

	private VariableParachute getVariable(VariableParachute variable, double timestep, DynamicsMinuteChangeParachute kn) {
		VariableParachute variable2 = variable.getClone();
		variable2.setTime(variable.getTime() + timestep);
		variable2.setPosENU(variable.getPosENU().add(kn.getDeltaPosENU().multiply(timestep)));
		variable2.setVelDescent(variable.getVelDescent() + kn.getDeltaVelDescent() * timestep);
		return variable2;
	}

}
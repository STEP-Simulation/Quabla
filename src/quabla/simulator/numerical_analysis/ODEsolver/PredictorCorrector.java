package quabla.simulator.numerical_analysis.ODEsolver;

import quabla.simulator.dynamics.AbstractDynamics;
import quabla.simulator.dynamics.DynamicsMinuteChangeParachute;
import quabla.simulator.dynamics.DynamicsMinuteChangeTrajectory;
import quabla.simulator.dynamics.DynamicsParachute;
import quabla.simulator.variable.Variable;
import quabla.simulator.variable.VariableParachute;

/**
 * Predictor-Corrector法
 * */
public class PredictorCorrector extends AbstractODEsolver{

	private final double h;
	private Variable variablePred;
	private VariableParachute variablePredPar;

	private DynamicsMinuteChangeTrajectory delta1, delta2, delta3;
	private DynamicsMinuteChangeParachute delta1Par, delta2Par, delta3Par;

	public PredictorCorrector(double timeStep) {
		h = timeStep;
	}

	public void setTra(DynamicsMinuteChangeTrajectory delta_1, DynamicsMinuteChangeTrajectory delta_2, DynamicsMinuteChangeTrajectory delta_3) {
		this.delta1 = delta_1;
		this.delta2 = delta_2;
		this.delta3 = delta_3;
	}

	@Override
	public DynamicsMinuteChangeTrajectory compute(Variable variable, AbstractDynamics dyn) {
		DynamicsMinuteChangeTrajectory delta = dyn.calculateDynamics(variable);

		//--------------- Predictor ---------------
		variablePred = variable.getClone();
		variablePred.setTime(variable.getTime() + h);
		variablePred.setPos_ENU(
				variable.getPosENU()
				.add((
						(delta.getDeltaPos_ENU().multiply(55.0))
						.sub(delta1.getDeltaPos_ENU().multiply(59.0))
						.add(delta2.getDeltaPos_ENU().multiply(37.0))
						.sub(delta3.getDeltaPos_ENU().multiply(9.0))
						).multiply(h / 24.0)));
		variablePred.setVelENU(
				variable.getVelENU()
				.add((
						(delta.getDeltaVel_ENU().multiply(55.0))
						.sub(delta1.getDeltaVel_ENU().multiply(59.0))
						.add(delta2.getDeltaVel_ENU().multiply(37.0))
						.sub(delta3.getDeltaVel_ENU().multiply(9.0))
						).multiply(h / 24.0)));
		variablePred.setOmegaBODY(
				variable.getOmega_Body()
				.add((
						(delta.getDeltaOmega_Body().multiply(55.0))
						.sub(delta1.getDeltaOmega_Body().multiply(59.0))
						.add(delta2.getDeltaOmega_Body().multiply(37.0))
						.sub(delta3.getDeltaOmega_Body().multiply(9.0))
						).multiply(h / 24.0)));
		variablePred.setQuat(
				variable.getQuat()
				.add((
						(delta.getDeltaQuat().multiply(55.0))
						.sub(delta1.getDeltaQuat().multiply(59.0))
						.add(delta2.getDeltaQuat().multiply(37.0))
						.sub(delta3.getDeltaQuat().multiply(9.0))
						).multiply(h / 24.0)));

		DynamicsMinuteChangeTrajectory deltaPred = dyn.calculateDynamics(variablePred);

		//--------------- Corrector ---------------
		//deltaの次元の関係上，variable更新時にhをかける
		DynamicsMinuteChangeTrajectory deltaCorr = new DynamicsMinuteChangeTrajectory();
		deltaCorr.setDeltaPos_ENU(
				(deltaPred.getDeltaPos_ENU().multiply(9.0))
				.add(delta.getDeltaPos_ENU().multiply(19.0))
				.sub(delta1.getDeltaPos_ENU().multiply(5.0))
				.add(delta2.getDeltaPos_ENU())
				.multiply(1 / 24.0));
		deltaCorr.setDeltaVelENU(
				(deltaPred.getDeltaVel_ENU().multiply(9.0))
				.add(delta.getDeltaVel_ENU().multiply(19.0))
				.sub(delta1.getDeltaVel_ENU().multiply(5.0))
				.add(delta2.getDeltaVel_ENU())
				.multiply(1 / 24.0));
		deltaCorr.setDeltaOmegaBODY(
				(deltaPred.getDeltaOmega_Body().multiply(9.0))
				.add(delta.getDeltaOmega_Body().multiply(19.0))
				.sub(delta1.getDeltaOmega_Body().multiply(5.0))
				.add(delta2.getDeltaOmega_Body())
				.multiply(1 / 24.0));
		deltaCorr.setDeltaQuat(
				(deltaPred.getDeltaQuat().multiply(9.0))
				.add(delta.getDeltaQuat().multiply(19.0))
				.sub(delta1.getDeltaQuat().multiply(5.0))
				.add(delta2.getDeltaQuat())
				.multiply(1 / 24.0));

		// deltaの更新
		delta3 = delta2;
		delta2 = delta1;
		delta1 = delta;

		return deltaCorr;
	}

	public void setDeltaPar(DynamicsMinuteChangeParachute delta1Par, DynamicsMinuteChangeParachute delta2Par, DynamicsMinuteChangeParachute delta3Par) {
		this.delta1Par = delta1Par;
		this.delta2Par = delta2Par;
		this.delta3Par = delta3Par;
	}

	@Override
	public DynamicsMinuteChangeParachute compute(VariableParachute variable, DynamicsParachute dyn) {
		DynamicsMinuteChangeParachute delta = dyn.calculateDynamics(variable);

		//--------------- Predictor ---------------
		variablePredPar = variable.getClone();
		variablePredPar.setTime(variable.getTime() + h);
		variablePredPar.setPosENU(
				variable.getPosENU()
				.add((
						(delta.getDeltaPosENU().multiply(55.0))
						.sub(delta1Par.getDeltaPosENU().multiply(59.0))
						.add(delta2Par.getDeltaPosENU().multiply(37.0))
						.sub(delta3Par.getDeltaPosENU().multiply(9.0))
						).multiply(h / 24.0)));
		variablePredPar.setVelDescent(
				variable.getVelDescent()
				+ (
						55.0 * delta.getDeltaVelDescent()
						- 59.0 * delta1Par.getDeltaVelDescent()
						+ 37.0 * delta2Par.getDeltaVelDescent()
						- 9.0 * delta3Par.getDeltaVelDescent()
						) * (h / 24.0));

		DynamicsMinuteChangeParachute deltaPred = dyn.calculateDynamics(variablePredPar);

		//--------------- Corrector ---------------
		//deltaの次元の関係上，variable更新時にhをかける
		DynamicsMinuteChangeParachute deltaCorr = new DynamicsMinuteChangeParachute();
		deltaCorr.setDeltaPosENU(
				(deltaPred.getDeltaPosENU().multiply(9.0))
				.add(delta.getDeltaPosENU().multiply(19.0))
				.sub(delta1Par.getDeltaPosENU().multiply(5.0))
				.add(delta2Par.getDeltaPosENU())
				.multiply(1 / 24.0));
		deltaCorr.setDeltaVelDescent((
				9.0 * deltaPred.getDeltaVelDescent()
				+ 19.0 * delta.getDeltaVelDescent()
				- 5.0 * delta1Par.getDeltaVelDescent()
				+ delta2Par.getDeltaVelDescent()) * (1 / 24.0));

		// deltaの更新
		delta3Par = delta2Par;
		delta2Par = delta1Par;
		delta1Par = delta;
		return deltaCorr;
	}

}

package quabla.simulator.numerical_analysis.ODEsolver;

import quabla.simulator.dynamics.AbstractDynamics;
import quabla.simulator.dynamics.AbstractDynamicsMinuteChange;
import quabla.simulator.variable.AbstractVariable;

public class RK4 extends AbstractODEsolver{

	private double h;

	public RK4(double timeStep) {
		h = timeStep;
	}

	@Override
	public AbstractDynamicsMinuteChange compute(AbstractVariable variable, AbstractDynamics dyn) {
		AbstractDynamicsMinuteChange k1, k2, k3, k4;
		AbstractVariable variable2;

		// k1 = f(t, x)
		k1 = dyn.calculateDynamics(variable);

		// k2 = f(t + h / 2, x + k1* h / 2)
		variable2 = variable.clone();
		variable2.update(0.5 * h, k1.multiple(0.5 * h));
		k2 = dyn.calculateDynamics(variable2);

		// k3 = f(t + h / 2, x + k2 * h / 2)
		variable2 = variable.clone();
		variable2.update(0.5 * h, k2.multiple(0.5 * h));
		k3 = dyn.calculateDynamics(variable2);

		// k4 = f(t + h, x + k3 * h)
		variable2 = variable.clone();
		variable2.update(h, k3.multiple(h));
		k4 = dyn.calculateDynamics(variable2);

		// dx/dt = (k1 + 2 * k2 + 2 * k3 + k4) / 6
		// 時間変化率dx/dtで返したいので時間ステップhはかけない
		double[] k1Dou, k2Dou, k3Dou, k4Dou;
		k1Dou = k1.toDouble();
		k2Dou = k2.toDouble();
		k3Dou = k3.toDouble();
		k4Dou = k4.toDouble();
		int length = k1Dou.length;
		double[] dx = new double[length];
		for(int i = 0; i < length; i++) {
			dx[i] = (k1Dou[i] + 2.0 * k2Dou[i] + 2.0 * k3Dou[i] + k4Dou[i]) / 6.0;
		}

		return k1.generate(dx);
	}

	@Override
	public double getTimeStep(){
		return h;
	}

	@Override
	public void setTimeStep(double timeStep){
		h = timeStep;
	}
}
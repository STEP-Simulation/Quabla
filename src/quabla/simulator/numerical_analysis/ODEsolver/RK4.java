package quabla.simulator.numerical_analysis.ODEsolver;

import java.util.function.Function;

import quabla.simulator.variable.AbstractVariable;

public class RK4 extends AbstractODEsolver{

	private double h;

	public RK4(double timeStep) {
		h = timeStep;
	}

	@Override
	public double[] compute(AbstractVariable variable, Function<AbstractVariable, double[]> dynamics) {
		
		double[] k1, k2, k3, k4;
		AbstractVariable variable2;
		
		// k1 = f(t, x)
		k1 = dynamics.apply(variable);
		double[] dx2 = new double[k1.length];;

		// k2 = f(t + h / 2, x + k1* h / 2)
		for (int i = 0; i < dx2.length; i++) {
			dx2[i] = 0.5 * h * k1[i];
		}
		variable2 = variable.clone();
		variable2.update(0.5 * h, dx2);
		k2 = dynamics.apply(variable2);
		
		// k3 = f(t + h / 2, x + k2 * h / 2)
		for (int i = 0; i < dx2.length; i++) {
			dx2[i] = 0.5 * h * k2[i];
		}
		variable2 = variable.clone();
		variable2.update(0.5 * h, dx2);
		k3 = dynamics.apply(variable2);
		
		// k4 = f(t + h, x + k3 * h)
		for (int i = 0; i < dx2.length; i++) {
			dx2[i] = h * k3[i];
		}
		variable2 = variable.clone();
		variable2.update(h, dx2);
		k4 = dynamics.apply(variable2);

		// dx/dt = (k1 + 2 * k2 + 2 * k3 + k4) / 6
		// 時間変化率dx/dtで返したいので時間ステップhはかけない
		int length = k1.length;
		double[] dx = new double[length];
		for(int i = 0; i < length; i++) {
			dx[i] = (k1[i] + 2.0 * k2[i] + 2.0 * k3[i] + k4[i]) / 6.0;
		}

		return dx;
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
package quabla.simulator.numerical_analysis.ODEsolver;

import quabla.simulator.dynamics.AbstractDynamics;
import quabla.simulator.dynamics.AbstractDynamicsMinuteChange;
import quabla.simulator.variable.AbstractVariable;

/**
 * Predictor-Corrector法
 * */
public class PredictorCorrector extends AbstractODEsolver
{

	private final double h;
	private double[] dx1, dx2, dx3;
	/**
	 * dx1 : dx_n-1
	 * dx2 : dx_n-2
	 * dx3 : dx_n-3
	 * */

	public PredictorCorrector(double timeStep) {
		h = timeStep;
	}

	public void setDelta(AbstractDynamicsMinuteChange dx1, AbstractDynamicsMinuteChange dx2, AbstractDynamicsMinuteChange dx3) {
		this.dx1 = dx1.toDouble();
		this.dx2 = dx2.toDouble();
		this.dx3 = dx3.toDouble();
	}

	public AbstractDynamicsMinuteChange compute(AbstractVariable variable, AbstractDynamics dyn) {
		AbstractDynamicsMinuteChange dxDmc = dyn.calculateDynamics(variable);
		double[] dx = dxDmc.toDouble();
		int length = dx.length;
		double[] x = variable.toDouble();
		double[] xPred = new double[length];

		// Predictor (Adams-Bashforth)
		for(int i = 0; i < length; i++) {
			xPred[i] = x[i] + (55.0 * dx[i] - 59.0 * dx1[i] + 37.0 * dx2[i] - 9.0 * dx3[i]) * h / 24.0;
		}


		AbstractVariable variablePred = variable.getClone();
		variablePred.setVariable(variable.getTime() + h, xPred);
		double[] dxPred = dyn.calculateDynamics(variablePred).toDouble();
		double[] dxCorr = new double[length];

		// Corrector (Adams-Moulton)
		// 時間変化率dx/dtで返したいので時間ステップhはかけない
		for(int i = 0; i < length; i++) {
			dxCorr[i] = (9.0 * dxPred[i] + 19.0 * dx[i] - 5.0 * dx1[i] + dx2[i]) / 24.0;
		}

		// dx1, dx2, dx3の値の更新
		System.arraycopy(dx2, 0, dx3, 0, length);
		System.arraycopy(dx1, 0, dx2, 0, length);
		System.arraycopy(dx, 0, dx1, 0, length);

		return dxDmc.generate(dxCorr);// dxDmcを使ってdxCorrを作ることで，子クラスで値を返せる
	}

}

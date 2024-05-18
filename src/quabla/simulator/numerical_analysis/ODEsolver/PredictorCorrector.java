package quabla.simulator.numerical_analysis.ODEsolver;

import quabla.simulator.dynamics.AbstractDynamics;
import quabla.simulator.dynamics.AbstractDynamicsMinuteChange;
import quabla.simulator.variable.AbstractVariable;

/**
 * Predictor-Corrector Method
 * */
public class PredictorCorrector extends AbstractODEsolver
{

	private double h;
	private double h0;
	private double[] dx1, dx2, dx3;
	private final double eps = 1e-04;
	private final double safetyFactor = 0.9;
	private boolean useATS = false;
	/**
	 * dx1 : dx_n-1
	 * dx2 : dx_n-2
	 * dx3 : dx_n-3
	 * */

	public PredictorCorrector(double timeStep) {
		h0 = timeStep;
		h = h0;
	}

	public void setDelta(double[] dx1, double[] dx2, double[] dx3) {
		this.dx1 = dx1.clone();
		this.dx2 = dx2.clone();
		this.dx3 = dx3.clone();
	}

	public double[] compute(AbstractVariable variable, AbstractDynamics dyn) {
		AbstractDynamicsMinuteChange dxDmc = dyn.calculateDynamics(variable);
		double[] dx = dxDmc.toDouble();
		int length = dx.length;
		double[] x = variable.toDouble();
		double[] xPred = new double[length];

		// Predictor (Adams-Bashforth)
		for(int i = 0; i < length; i++) {
			xPred[i] = x[i] + (55.0 * dx[i] - 59.0 * dx1[i] + 37.0 * dx2[i] - 9.0 * dx3[i]) * h / 24.0;
		}

		AbstractVariable variablePred = variable.clone();
		variablePred.setVariable(variable.getTime() + h, xPred);
		double[] dxPred = dyn.calculateDynamics(variablePred).toDouble();
		double[] dxCorr = new double[length];

		// Corrector (Adams-Moulton)
		// 時間変化率dx/dtで返したいので時間ステップhはかけない
		for(int i = 0; i < length; i++) {
			dxCorr[i] = (9.0 * dxPred[i] + 19.0 * dx[i] - 5.0 * dx1[i] + dx2[i]) / 24.0;
		}
		
		// Adaptive Time Step
		if (useATS) {
			double errMax = - 1.0e+10;
			for (int i = 0; i < length; i++) {
				double err = Math.abs(9. * dxPred[i] - 36. * dx[i] + 54. * dx1[i] - 36. * dx2[i] + 9. * dx3[i]) / 24. * h;
				// double err = Math.abs(dxCorr[i] * h - (xPred[i] - x[i]));
				errMax = Math.max(err, errMax);
			} 
			
			h *= safetyFactor * Math.pow(eps / errMax, 0.2);
			h = Math.min(h, h0 * 20.);
			h = Math.max(h, h0);
		}

		// dx1, dx2, dx3の値の更新
		System.arraycopy(dx2, 0, dx3, 0, length);
		System.arraycopy(dx1, 0, dx2, 0, length);
		System.arraycopy(dx, 0, dx1, 0, length);

		AbstractDynamicsMinuteChange dxNew = dxDmc.clone();
		dxNew.set(dxCorr);

		// return dxNew;// dxDmcを使ってdxCorrを作ることで，子クラスで値を返せる
		return dxCorr;
	}
	
	@Override
	public double getTimeStep(){
		return h;
	}

	@Override
	public void setTimeStep(double timeStep){
		h0 = timeStep;
	}

	public void effectiveATS(){
		useATS = true;
	}
}

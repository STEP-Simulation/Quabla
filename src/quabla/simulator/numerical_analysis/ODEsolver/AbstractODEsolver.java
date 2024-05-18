package quabla.simulator.numerical_analysis.ODEsolver;

import java.util.function.Function;

import quabla.simulator.variable.AbstractVariable;

public abstract class AbstractODEsolver {

	public abstract double[] compute(AbstractVariable variable, Function<AbstractVariable, double[]> dynamics);
	public abstract double getTimeStep();
	public abstract void setTimeStep(double timeStep);
}

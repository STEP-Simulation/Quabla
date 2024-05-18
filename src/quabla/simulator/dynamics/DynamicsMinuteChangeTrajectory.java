package quabla.simulator.dynamics;

public class DynamicsMinuteChangeTrajectory extends AbstractDynamicsMinuteChange implements Cloneable {

	private double[] deltaPosNED    = new double[3];
	private double[] deltaVelNED    = new double[3];
	private double[] deltaOmegaBODY = new double[3];
	private double[] deltaQuat      = new double[4];

	@Override
	public void set(double[] dx) {

		int index = 0;
		System.arraycopy(dx, index, deltaPosNED, 0, deltaPosNED.length);
		index += deltaPosNED.length;
		System.arraycopy(dx, index, deltaVelNED, 0, deltaVelNED.length);
		index += deltaVelNED.length;
		System.arraycopy(dx, index, deltaOmegaBODY, 0, deltaOmegaBODY.length);
		index += deltaOmegaBODY.length;
		System.arraycopy(dx, index, deltaQuat, 0, deltaQuat.length);
	}

	public void setDeltaPosNED(double[] deltaPosENU) {
		this.deltaPosNED = deltaPosENU.clone();
	}

	public void setDeltaVelNED(double[] deltaVelNED) {
		this.deltaVelNED = deltaVelNED.clone();
	}

	public void setDeltaOmegaBODY(double[] deltaOmegaBODY) {
		this.deltaOmegaBODY = deltaOmegaBODY.clone();
	}

	public void setDeltaQuat(double[] deltaQuat) {
		this.deltaQuat = deltaQuat.clone();
	}

	@Override
	public DynamicsMinuteChangeTrajectory multiple(double a) {
		
		DynamicsMinuteChangeTrajectory dmct = this.clone();

		for (int i = 0; i < 3; i++) {
			dmct.deltaPosNED[i]    = a * this.deltaPosNED[i];
			dmct.deltaVelNED[i]    = a * this.deltaVelNED[i];
			dmct.deltaOmegaBODY[i] = a * this.deltaOmegaBODY[i];
		}
		for (int i = 0; i < 4; i++) {
			dmct.deltaQuat[i] = a * this.deltaQuat[i];
		}

		return dmct;
	}

	@Override
	public double[] toDouble() {
		
		double[] dx = new double[13];
		int index = 0;
		System.arraycopy(deltaPosNED   , 0, dx, index, deltaPosNED.length);
		index += deltaPosNED.length;
		System.arraycopy(deltaVelNED   , 0, dx, index, deltaVelNED.length);
		index += deltaVelNED.length;
		System.arraycopy(deltaOmegaBODY, 0, dx, index, deltaOmegaBODY.length);
		index += deltaOmegaBODY.length;
		System.arraycopy(deltaQuat     , 0, dx, index, deltaQuat.length);
		index += deltaQuat.length;

		return dx;
	}

	@Override
	public DynamicsMinuteChangeParachute toDeltaPara() {
		return new DynamicsMinuteChangeParachute(deltaPosNED, deltaVelNED[2]);
	}

	@Override
	public DynamicsMinuteChangeTrajectory clone() {

		DynamicsMinuteChangeTrajectory clone = (DynamicsMinuteChangeTrajectory) super.clone();
		clone.deltaPosNED    = this.deltaPosNED.clone();
		clone.deltaVelNED    = this.deltaVelNED.clone();
		clone.deltaOmegaBODY = this.deltaOmegaBODY.clone();
		clone.deltaQuat      = this.deltaQuat.clone();

		return clone;

	}

	@Override
	public double[] getDeltaPosNED() {
		return deltaPosNED;
	}

	@Override
	public double[] getDeltaVelNED() {
		return deltaVelNED;
	}

	@Override
	public double[] getDeltaOmegaBODY() {
		return deltaOmegaBODY;
	}

	@Override
	public double[] getDeltaQuat() {
		return deltaQuat;
	}

	@Override
	public double getDeltaVelDescent() {
		return deltaVelNED[2];
	}

	public DynamicsMinuteChangeParachute getDelatPar() {
		return new DynamicsMinuteChangeParachute(deltaPosNED, deltaVelNED[2]);
	}

}

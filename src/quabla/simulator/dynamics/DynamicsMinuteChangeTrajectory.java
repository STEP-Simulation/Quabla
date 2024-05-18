package quabla.simulator.dynamics;

public class DynamicsMinuteChangeTrajectory implements Cloneable {

	private double[] deltaPosNED    = new double[3];
	private double[] deltaVelBODY   = new double[3];
	private double[] deltaOmegaBODY = new double[3];
	private double[] deltaQuat      = new double[4];

	public void set(double[] dx) {

		int index = 0;
		System.arraycopy(dx, index, deltaPosNED, 0, deltaPosNED.length);
		index += deltaPosNED.length;
		System.arraycopy(dx, index, deltaVelBODY, 0, deltaVelBODY.length);
		index += deltaVelBODY.length;
		System.arraycopy(dx, index, deltaOmegaBODY, 0, deltaOmegaBODY.length);
		index += deltaOmegaBODY.length;
		System.arraycopy(dx, index, deltaQuat, 0, deltaQuat.length);
	}

	public void setDeltaPosNED(double[] deltaPosENU) {
		this.deltaPosNED = deltaPosENU.clone();
	}

	public void setDeltaVelBODY(double[] deltaVelBODY) {
		this.deltaVelBODY = deltaVelBODY.clone();
	}

	public void setDeltaOmegaBODY(double[] deltaOmegaBODY) {
		this.deltaOmegaBODY = deltaOmegaBODY.clone();
	}

	public void setDeltaQuat(double[] deltaQuat) {
		this.deltaQuat = deltaQuat.clone();
	}

	public DynamicsMinuteChangeTrajectory multiple(double a) {
		
		DynamicsMinuteChangeTrajectory dmct = this.clone();

		for (int i = 0; i < 3; i++) {
			dmct.deltaPosNED[i]    = a * this.deltaPosNED[i];
			dmct.deltaVelBODY[i]   = a * this.deltaVelBODY[i];
			dmct.deltaOmegaBODY[i] = a * this.deltaOmegaBODY[i];
		}
		for (int i = 0; i < 4; i++) {
			dmct.deltaQuat[i] = a * this.deltaQuat[i];
		}

		return dmct;
	}

	public double[] toDouble() {
		
		double[] dx = new double[13];
		int index = 0;
		System.arraycopy(deltaPosNED   , 0, dx, index, deltaPosNED.length);
		index += deltaPosNED.length;
		System.arraycopy(deltaVelBODY   , 0, dx, index, deltaVelBODY.length);
		index += deltaVelBODY.length;
		System.arraycopy(deltaOmegaBODY, 0, dx, index, deltaOmegaBODY.length);
		index += deltaOmegaBODY.length;
		System.arraycopy(deltaQuat     , 0, dx, index, deltaQuat.length);
		index += deltaQuat.length;

		return dx;
	}

	public DynamicsMinuteChangeParachute toDeltaPara() {
		return new DynamicsMinuteChangeParachute(deltaPosNED, deltaVelBODY[2]);
	}

	@Override
	public DynamicsMinuteChangeTrajectory clone() {

		try {
			
			DynamicsMinuteChangeTrajectory clone = (DynamicsMinuteChangeTrajectory) super.clone();
			clone.deltaPosNED    = this.deltaPosNED.clone();
			clone.deltaVelBODY   = this.deltaVelBODY.clone();
			clone.deltaOmegaBODY = this.deltaOmegaBODY.clone();
			clone.deltaQuat      = this.deltaQuat.clone();
	
			return clone;

		} catch (CloneNotSupportedException e) {
			// TODO: handle exception
			throw new InternalError(e);
		}

	}

	public double[] getDeltaPosNED() {
		return deltaPosNED;
	}

	public double[] getDeltaVelBODY() {
		return deltaVelBODY;
	}

	public double[] getDeltaOmegaBODY() {
		return deltaOmegaBODY;
	}

	public double[] getDeltaQuat() {
		return deltaQuat;
	}

	// public double getDeltaVelDescent() {
	// 	return deltaVelBODY[2];
	// }

	public DynamicsMinuteChangeParachute getDelatPar() {
		return new DynamicsMinuteChangeParachute(deltaPosNED, deltaVelBODY[2]);
	}

}

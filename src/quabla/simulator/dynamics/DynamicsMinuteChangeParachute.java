package quabla.simulator.dynamics;

public class DynamicsMinuteChangeParachute implements Cloneable {

	private double[] deltaPosNED = new double[3];
	private double deltaVelDescent;

	public DynamicsMinuteChangeParachute() {

	}

	public DynamicsMinuteChangeParachute(double[] deltaPosNED, double deltaVelDescent) {
		System.arraycopy(deltaPosNED, 0, this.deltaPosNED, 0, deltaPosNED.length);
		this.deltaVelDescent = deltaVelDescent;
	}

	public void set(double[] dx) {
		System.arraycopy(dx, 0, deltaPosNED, 0, deltaPosNED.length);
		this.deltaVelDescent = dx[3];
	}

	public void setDeltaPosNED(double[] deltaPosNED) {
		System.arraycopy(deltaPosNED, 0, this.deltaPosNED, 0, deltaPosNED.length);
	}

	public void setDeltaVelDescent(double deltaVelDescent) {
		this.deltaVelDescent = deltaVelDescent;
	}

	public DynamicsMinuteChangeParachute multiple(double a) {
		DynamicsMinuteChangeParachute dmcp = this.clone();
		for (int i = 0; i < 3; i++) {
			dmcp.deltaPosNED[i]    = a * this.deltaPosNED[i];
		}
		dmcp.deltaVelDescent = a * this.deltaVelDescent;

		return dmcp;

	}

	public double[] toDouble() {
		double[] dx = new double[4];
		System.arraycopy(deltaPosNED, 0, dx, 0, 3);
		dx[3] = deltaVelDescent;
		return dx;
	}

	@Override
	public DynamicsMinuteChangeParachute clone() {

		try {
			
			DynamicsMinuteChangeParachute clone = (DynamicsMinuteChangeParachute) super.clone();
			clone.deltaPosNED = this.deltaPosNED.clone();
			
			return clone;
			
		} catch (CloneNotSupportedException e) {
			// TODO: handle exception
			throw new InternalError(e);
		}
	}
		
	public DynamicsMinuteChangeParachute toDeltaPara() {
		return new DynamicsMinuteChangeParachute(deltaPosNED, deltaVelDescent);
	}

	public double[] getDeltaPosNED() {
		return deltaPosNED;
	}

	public double getDeltaVelDescent() {
		return deltaVelDescent;
	}
}

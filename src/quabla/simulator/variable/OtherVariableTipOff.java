package quabla.simulator.variable;

import quabla.simulator.Coordinate;
import quabla.simulator.rocket.Rocket;

public class OtherVariableTipOff extends OtherVariableTrajectory {

    // private final double altitude0;
    // private final double x

    public OtherVariableTipOff(Rocket rocket){
        super(rocket);
        
        // double[] posNED0 = new VariableTrajectory(rocket).getInitinalPosNED();
        // altitude0 = - posNED0[2];

    }

    public void setOtherVariable(double time, double[] posNED, double[] velBODY, double[] omegaBODY, double[] quat) {
        super.setOtherVariable(time, posNED, velBODY, omegaBODY, quat);


        super.alphaRad = 0.0;
        // super.betaRad  = 0.0;

        super.normal = 0.0;
        // super.side   = 0.0;

        // Force @ BODY-coordinate
        // for (int i = 1; i < velAirBODY.length; i++) {
        velAirBODY[2] = 0.0;
        forceBODY[2]  = 0.0;
        accBODY[2]    = 0.0;
        // }

        // if(accBODY[0] <= 0.0 && time < super.rocket.engine.timeBurnout && super.altitude <= altitude0) {
        //     for (int i = 0; i < accBODY.length; i++) {
        //         accBODY[i] = 0.0;
        //     }
		// }
        super.accENU = Coordinate.transVector(super.dcmBODY2NED, accBODY);

        for (int i = 0; i < moment.length - 1; i++) {
            momentAero[i]        = 0.0;
            momentAeroDamping[i] = 0.0;
            momentJetDamping[i]  = 0.0;
            momentGyro[i]        = 0.0;
            moment[i]            = 0.0;
            omegadot[i]          = 0.0;
        }

    }
    
}

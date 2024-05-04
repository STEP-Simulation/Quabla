package quabla.simulator.rocket;

import com.fasterxml.jackson.databind.JsonNode;

public class Payload extends AbstractRocket {
    
    private final double mass;
    private final double CdS;

    public Payload(JsonNode spec) {

        mass = spec.get("Mass [kg]").asDouble();
        CdS  = spec.get("Parachute CdS [m2]").asDouble();

    }

    @Override
    public double getMass(double time) {
        return mass;
    }

    @Override
    public double getCdS(double time, double altitude) {
        return CdS;
    }
}

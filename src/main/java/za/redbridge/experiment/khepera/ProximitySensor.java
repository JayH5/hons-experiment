package za.redbridge.experiment.khepera;

import za.redbridge.simulator.sensor.ProximityAgentSensor;

/**
 * A rough estimation of the ultrasonic sensor used in the Khepera III robot: the Vishay TCRT5000
 * Created by jamie on 2014/09/23.
 */
public class ProximitySensor extends ProximityAgentSensor {

    private static final float PROXIMITY_SENSOR_RANGE = 0.015f;
    private static final float PROXIMITY_SENSOR_FOV = 0.5f; // This is a guess

    public ProximitySensor(float bearing, float orientation) {
        this(bearing, orientation, PROXIMITY_SENSOR_RANGE, PROXIMITY_SENSOR_FOV);
    }

    public ProximitySensor(float bearing, float orientation, float range, float fieldOfView) {
        super(bearing, orientation, range, fieldOfView);
    }

}

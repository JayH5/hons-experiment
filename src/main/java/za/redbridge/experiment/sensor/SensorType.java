package za.redbridge.experiment.sensor;

import za.redbridge.simulator.khepera.ProximitySensor;
import za.redbridge.simulator.khepera.UltrasonicSensor;
import za.redbridge.simulator.sensor.AgentSensor;

/**
 * Created by jamie on 2014/09/19.
 */
public enum SensorType {

    PROXIMITY,
    ULTRASONIC;

    public AgentSensor getSensor(float bearing, float orientation) {
        switch (this) {
            case PROXIMITY:
                return new ProximitySensor(bearing, orientation);
            case ULTRASONIC:
                return new UltrasonicSensor(bearing, orientation);
        }
        return null;
    }
}

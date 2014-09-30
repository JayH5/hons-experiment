package za.redbridge.experiment.sensor;

import za.redbridge.simulator.sensor.AgentSensor;

/**
 * Created by jamie on 2014/09/19.
 */
public enum SensorType {

    COLLISION,
    RESOURCE;
    //TARGET_AREA;

    public AgentSensor getSensor(float bearing, float orientation) {
        switch (this) {
            case COLLISION:
                return new CollisionSensor(bearing, orientation);
            case RESOURCE:
                return new ResourceSensor(bearing, orientation);
            /*case TARGET_AREA:
                return new TargetAreaSensor(bearing, orientation);*/
        }
        return null;
    }
}

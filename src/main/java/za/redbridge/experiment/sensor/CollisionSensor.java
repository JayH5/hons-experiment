package za.redbridge.experiment.sensor;

import java.awt.Color;
import java.awt.Paint;

import za.redbridge.simulator.sensor.ProximityAgentSensor;

/**
 * Short-range sensor that can detect agents, resources, and walls.
 * Created by jamie on 2014/09/19.
 */
public class CollisionSensor extends ProximityAgentSensor {

    private static final float DEFAULT_RANGE = 1.0f;
    private static final float DEFAULT_FIELD_OF_VIEW = 0.2f;

    private static final Paint DEFAULT_PAINT = new Color(255, 0, 0, 128);

    public CollisionSensor(float bearing, float orientation, float range, float fieldOfView) {
        super(bearing, orientation, range, fieldOfView);
    }

    public CollisionSensor(float bearing, float orientation) {
        this(bearing, orientation, DEFAULT_RANGE, DEFAULT_FIELD_OF_VIEW);
    }

    @Override
    protected Paint getPaint() {
        return DEFAULT_PAINT;
    }

}

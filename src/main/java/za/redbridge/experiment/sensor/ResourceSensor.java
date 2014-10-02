package za.redbridge.experiment.sensor;

import java.awt.Color;
import java.awt.Paint;

import za.redbridge.simulator.physics.FilterConstants;
import za.redbridge.simulator.sensor.ProximityAgentSensor;

/**
 * A long-range sensor that only detects resources.
 * Created by jamie on 2014/09/19.
 */
public class ResourceSensor extends ProximityAgentSensor {

    private static final float DEFAULT_RANGE = 2.0f;
    private static final float DEFAULT_FIELD_OF_VIEW = 0.2f;

    private static final Paint DEFAULT_PAINT = new Color(255, 200, 0, 128);

    public ResourceSensor(float bearing, float orientation, float range, float fieldOfView) {
        super(bearing, orientation, range, fieldOfView);
    }

    public ResourceSensor(float bearing, float orientation) {
        this(bearing, orientation, DEFAULT_RANGE, DEFAULT_FIELD_OF_VIEW);
    }

    @Override
    protected Paint getPaint() {
        return DEFAULT_PAINT;
    }

    @Override
    protected int getFilterMaskBits() {
        return FilterConstants.CategoryBits.RESOURCE;
    }

}

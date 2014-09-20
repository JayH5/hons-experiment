package za.redbridge.experiment.sensor;

import org.jbox2d.dynamics.Fixture;

import java.awt.Color;
import java.awt.Paint;

import za.redbridge.simulator.object.TargetAreaObject;
import za.redbridge.simulator.sensor.ProximityAgentSensor;

/**
 * Created by jamie on 2014/09/19.
 */
public class TargetAreaSensor extends ProximityAgentSensor {

    private static final float DEFAULT_RANGE = 4.0f;
    private static final float DEFAULT_FIELD_OF_VIEW = 0.4f;

    private static final Paint DEFAULT_PAINT = new Color(0, 255, 0, 128);

    public TargetAreaSensor(float bearing, float orientation, float range, float fieldOfView) {
        super(bearing, orientation, range, fieldOfView);
    }

    public TargetAreaSensor(float bearing, float orientation) {
        this(bearing, orientation, DEFAULT_RANGE, DEFAULT_FIELD_OF_VIEW);
    }

    @Override
    protected Paint getPaint() {
        return DEFAULT_PAINT;
    }

    @Override
    public boolean isRelevantObject(Fixture otherFixture) {
        return otherFixture.getBody().getUserData() instanceof TargetAreaObject;
    }
}
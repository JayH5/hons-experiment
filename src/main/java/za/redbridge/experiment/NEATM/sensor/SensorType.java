package za.redbridge.experiment.NEATM.sensor;

import za.redbridge.experiment.NEATM.sensor.parameter.SensorParameterSpec.BearingSpec;
import za.redbridge.experiment.NEATM.sensor.parameter.SensorParameterSpec.FieldOfViewSpec;
import za.redbridge.experiment.NEATM.sensor.parameter.SensorParameterSpec.OrientationSpec;
import za.redbridge.experiment.NEATM.sensor.parameter.SensorParameterSpec.RangeSpec;
import za.redbridge.experiment.NEATM.sensor.parameter.SensorParameterSpecSet;


import static za.redbridge.experiment.NEATM.sensor.parameter.spec.Initializers.copyExistingOrRandom;
import static za.redbridge.experiment.NEATM.sensor.parameter.spec.Initializers.random;
import static za.redbridge.experiment.NEATM.sensor.parameter.spec.Limiters.clamp;
import static za.redbridge.experiment.NEATM.sensor.parameter.spec.Limiters.wrap;
import static za.redbridge.experiment.NEATM.sensor.parameter.spec.Ranges.exclusiveRange;
import static za.redbridge.experiment.NEATM.sensor.parameter.spec.Ranges.plusMinusHalfPi;
import static za.redbridge.experiment.NEATM.sensor.parameter.spec.Ranges.plusMinusPi;
import static za.redbridge.experiment.NEATM.sensor.parameter.spec.Ranges.zeroToPi;

/**
 * Contains the definitions of the valid parameters for each sensor type.
 *
 * Created by jamie on 2014/09/19.
 */
public enum SensorType {

    /**
     * new Spec(initial value, valid range, limiter)
     */

    BOTTOM_PROXIMITY(null),

    PROXIMITY(new SensorParameterSpecSet(
            new BearingSpec(random(), plusMinusPi(), wrap()),
            new OrientationSpec(random(), plusMinusHalfPi(), clamp()),
            // TODO: query environment size for range value
            new RangeSpec(copyExistingOrRandom(), exclusiveRange(0, 40), clamp()),
            new FieldOfViewSpec(copyExistingOrRandom(), zeroToPi(), clamp()))),

    ULTRASONIC(new SensorParameterSpecSet(
            new BearingSpec(random(), plusMinusPi(), wrap()),
            new OrientationSpec(random(), plusMinusHalfPi(), clamp()),
            // TODO: query environment size for range value
            new RangeSpec(copyExistingOrRandom(), exclusiveRange(0, 40), clamp()),
            new FieldOfViewSpec(copyExistingOrRandom(), zeroToPi(), clamp())));

    private final SensorParameterSpecSet defaultSpecSet;

    SensorType(SensorParameterSpecSet defaultSpecSet) {
        this.defaultSpecSet = defaultSpecSet;
    }

    public boolean isConfigurable() {
        return defaultSpecSet != null;
    }

    public SensorParameterSpecSet getDefaultSpecSet() {
        return defaultSpecSet;
    }

}
package za.redbridge.experiment.MMNEAT;

import java.io.Serializable;

/**
 * Container class for the bearing and orientation values for a sensor morphology configuration.
 * Created by jamie on 2014/09/09.
 */
public class SensorMorphology implements Serializable {

    private static final long serialVersionUID = 5166321415840834464L;

    private final int numSensors;

    private final double[] bearings;
    private final double[] orientations;

    public SensorMorphology(int numSensors) {
        if (numSensors <= 0) {
            throw new IllegalArgumentException("There must be at least one sensor");
        }

        this.numSensors = numSensors;

        bearings = new double[numSensors];
        orientations = new double[numSensors];
    }

    public int getNumSensors() {
        return numSensors;
    }

    public double getSensorBearing(int index) {
        checkValidIndex(index);
        return bearings[index];
    }

    void setSensorBearing(int index, double bearing) {
        checkValidIndex(index);
        bearings[index] = bearing;
    }

    public double getSensorOrientation(int index) {
        checkValidIndex(index);
        return orientations[index];
    }

    void setSensorOrientation(int index, double orientation) {
        checkValidIndex(index);
        orientations[index] = orientation;
    }

    private void checkValidIndex(int index) {
        if (index < 0 || index >= numSensors) {
            throw new IllegalArgumentException("Invalid sensor index: " + index);
        }
    }
}

package za.redbridge.experiment;

import za.redbridge.experiment.MMNEAT.SensorMorphology;
import za.redbridge.experiment.sensor.SensorType;
import za.redbridge.simulator.khepera.KheperaIIIPhenotype;

/**
 * A horrible adapter class for different representations of morphologies. Creates a
 * {@link SensorMorphology} for a {@link KheperaIIIPhenotype.Configuration}.
 *
 * Created by jamie on 2014/10/06.
 */
public class KheperaIIIMorphology extends SensorMorphology {

    private static final KheperaIIIPhenotype.Configuration DEFAULT_CONFIGURATION =
            new KheperaIIIPhenotype.Configuration();
    static {
        DEFAULT_CONFIGURATION.enableProximitySensors40Degrees = true;
        DEFAULT_CONFIGURATION.enableProximitySensorBottom = true;
        DEFAULT_CONFIGURATION.enableUltrasonicSensor0Degrees = true;
        DEFAULT_CONFIGURATION.enableUltrasonicSensors90Degrees = true;
    }

    public KheperaIIIMorphology() {
        this(DEFAULT_CONFIGURATION);
    }

    public KheperaIIIMorphology(KheperaIIIPhenotype.Configuration config) {
        super(config.getNumberOfSensors());

        int sensorIndex = 0;
        if (config.enableProximitySensorBottom) {
            setSensorType(sensorIndex++, SensorType.BOTTOM_PROXIMITY);
        }

        if (config.enableProximitySensors10Degrees) {
            setSensorType(sensorIndex, SensorType.PROXIMITY);
            setSensorBearing(sensorIndex, Math.toRadians(10));

            sensorIndex++;

            setSensorType(sensorIndex, SensorType.PROXIMITY);
            setSensorBearing(sensorIndex, Math.toRadians(-10));

            sensorIndex++;
        }

        if (config.enableProximitySensors40Degrees) {
            setSensorType(sensorIndex, SensorType.PROXIMITY);
            setSensorBearing(sensorIndex, Math.toRadians(40));

            sensorIndex++;

            setSensorType(sensorIndex, SensorType.PROXIMITY);
            setSensorBearing(sensorIndex, Math.toRadians(-40));

            sensorIndex++;
        }

        if (config.enableProximitySensors75Degrees) {
            setSensorType(sensorIndex, SensorType.PROXIMITY);
            setSensorBearing(sensorIndex, Math.toRadians(75));

            sensorIndex++;

            setSensorType(sensorIndex, SensorType.PROXIMITY);
            setSensorBearing(sensorIndex, Math.toRadians(-75));

            sensorIndex++;
        }

        if (config.enableProximitySensors140Degrees) {
            setSensorType(sensorIndex, SensorType.PROXIMITY);
            setSensorBearing(sensorIndex, Math.toRadians(140));

            sensorIndex++;

            setSensorType(sensorIndex, SensorType.PROXIMITY);
            setSensorBearing(sensorIndex, Math.toRadians(-140));

            sensorIndex++;
        }

        if (config.enableProximitySensor180Degrees) {
            setSensorType(sensorIndex, SensorType.PROXIMITY);
            setSensorBearing(sensorIndex, Math.toRadians(180));

            sensorIndex++;
        }

        if (config.enableUltrasonicSensor0Degrees) {
            setSensorType(sensorIndex, SensorType.ULTRASONIC);
            setSensorBearing(sensorIndex, 0);

            sensorIndex++;
        }

        if (config.enableUltrasonicSensors40Degrees) {
            setSensorType(sensorIndex, SensorType.ULTRASONIC);
            setSensorBearing(sensorIndex, Math.toRadians(40));

            sensorIndex++;

            setSensorType(sensorIndex, SensorType.ULTRASONIC);
            setSensorBearing(sensorIndex, Math.toRadians(-40));

            sensorIndex++;
        }

        if (config.enableUltrasonicSensors90Degrees) {
            setSensorType(sensorIndex, SensorType.ULTRASONIC);
            setSensorBearing(sensorIndex, Math.toRadians(90));

            sensorIndex++;

            setSensorType(sensorIndex, SensorType.ULTRASONIC);
            setSensorBearing(sensorIndex, Math.toRadians(-90));
        }
    }
}

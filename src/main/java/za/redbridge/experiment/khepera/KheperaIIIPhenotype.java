package za.redbridge.experiment.khepera;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.neat.NEATNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sim.util.Double2D;
import za.redbridge.simulator.phenotype.Phenotype;
import za.redbridge.simulator.sensor.AgentSensor;

/**
 * Phenotype designed for use with a NEAT network that mimics the morphology of a Khepera III robot.
 * Created by jamie on 2014/09/22.
 */
public class KheperaIIIPhenotype implements Phenotype {

    private static final boolean ENABLE_PROXIMITY_SENSORS_10_DEGREES = true;
    private static final boolean ENABLE_PROXIMITY_SENSORS_40_DEGREES = false;
    private static final boolean ENABLE_PROXIMITY_SENSORS_75_DEGREES = true;
    private static final boolean ENABLE_PROXIMITY_SENSORS_140_DEGREES = false;
    private static final boolean ENABLE_PROXIMITY_SENSOR_180_DEGREES = true;

    private static final boolean ENABLE_ULTRASONIC_SENSOR_0_DEGREES = true;
    private static final boolean ENABLE_ULTRASONIC_SENSORS_40_DEGREES = false;
    private static final boolean ENABLE_ULTRASONIC_SENSORS_90_DEGREES = true;

    public static final int NUM_SENSORS;
    static {
        int numSensors = 0;
        if (ENABLE_PROXIMITY_SENSORS_10_DEGREES) numSensors += 2;
        if (ENABLE_PROXIMITY_SENSORS_40_DEGREES) numSensors += 2;
        if (ENABLE_PROXIMITY_SENSORS_75_DEGREES) numSensors += 2;
        if (ENABLE_PROXIMITY_SENSORS_140_DEGREES) numSensors += 2;
        if (ENABLE_PROXIMITY_SENSOR_180_DEGREES) numSensors += 1;
        if (ENABLE_ULTRASONIC_SENSOR_0_DEGREES) numSensors += 1;
        if (ENABLE_ULTRASONIC_SENSORS_40_DEGREES) numSensors += 2;
        if (ENABLE_ULTRASONIC_SENSORS_90_DEGREES) numSensors += 2;
        NUM_SENSORS = numSensors;
    }

    private final List<AgentSensor> sensors = new ArrayList<>(NUM_SENSORS);
    private final double[] inputs = new double[NUM_SENSORS];

    private final NEATNetwork network;

    public KheperaIIIPhenotype(NEATNetwork network) {
        if (network.getInputCount() != NUM_SENSORS) {
            throw new IllegalArgumentException(
                    "Network does not support the right number of sensors");
        }
        this.network = network;
        initSensors();
    }

    private void initSensors() {
        // Proximity sensors
        if (ENABLE_PROXIMITY_SENSORS_10_DEGREES) {
            sensors.add(new ProximitySensor((float) Math.toRadians(10), 0f));
            sensors.add(new ProximitySensor((float) Math.toRadians(-10), 0f));
        }

        if (ENABLE_PROXIMITY_SENSORS_40_DEGREES) {
            sensors.add(new ProximitySensor((float) Math.toRadians(40), 0f));
            sensors.add(new ProximitySensor((float) Math.toRadians(-40), 0f));
        }

        if (ENABLE_PROXIMITY_SENSORS_75_DEGREES) {
            sensors.add(new ProximitySensor((float) Math.toRadians(75), 0f));
            sensors.add(new ProximitySensor((float) Math.toRadians(-75), 0f));
        }

        if (ENABLE_PROXIMITY_SENSORS_140_DEGREES) {
            sensors.add(new ProximitySensor((float) Math.toRadians(140), 0f));
            sensors.add(new ProximitySensor((float) Math.toRadians(-140), 0f));
        }

        if (ENABLE_PROXIMITY_SENSOR_180_DEGREES) {
            sensors.add(new ProximitySensor((float) Math.PI, 0f));
        }

        // Ultrasonic sensors
        if (ENABLE_ULTRASONIC_SENSOR_0_DEGREES) {
            sensors.add(new UltrasonicSensor(0f, 0f));
        }

        if (ENABLE_ULTRASONIC_SENSORS_40_DEGREES) {
            sensors.add(new UltrasonicSensor((float) Math.toRadians(40), 0f));
            sensors.add(new UltrasonicSensor((float) Math.toRadians(-40), 0f));
        }

        if (ENABLE_ULTRASONIC_SENSORS_90_DEGREES) {
            sensors.add(new UltrasonicSensor((float) Math.PI / 2, 0f));
            sensors.add(new UltrasonicSensor((float) -Math.PI / 2, 0f));
        }
    }

    @Override
    public List<AgentSensor> getSensors() {
        return sensors;
    }

    @Override
    public Double2D step(List<List<Double>> sensorReadings) {
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = sensorReadings.get(i).get(0);
        }

        MLData input = new BasicMLData(inputs);
        MLData output = network.compute(input);

        return new Double2D(output.getData(0) * 2.0 - 1.0, output.getData(1) * 2.0 - 1.0);
    }

    @Override
    public Phenotype clone() {
        return new KheperaIIIPhenotype(network);
    }

    @Override
    public void configure(Map<String, Object> stringObjectMap) {

    }
}

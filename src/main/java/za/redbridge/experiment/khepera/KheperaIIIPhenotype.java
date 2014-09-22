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
import za.redbridge.simulator.sensor.ProximityAgentSensor;
import za.redbridge.simulator.sensor.SensorReading;

/**
 * Phenotype designed for use with a NEAT network that mimics the morphology of a Khepera III robot.
 * Created by jamie on 2014/09/22.
 */
public class KheperaIIIPhenotype implements Phenotype {

    public static final int NUM_SENSORS = 9 + 5;

    private static final float PROXIMITY_SENSOR_RANGE = 0.015f;
    private static final float PROXIMITY_SENSOR_FOV = 0.5f;

    private static final float ULTRASONIC_SENSOR_RANGE = 2.0f;
    private static final float ULTRASONIC_SENSOR_FOV = 1.22f; // 35 degrees

    private final List<AgentSensor> sensors = new ArrayList<>(NUM_SENSORS);
    private final double[] inputs = new double[NUM_SENSORS];

    private final NEATNetwork network;

    public KheperaIIIPhenotype(NEATNetwork network) {
        this.network = network;
        initSensors();
    }

    private void initSensors() {
        // Proximity sensors
        // At +/- 10 degrees
        sensors.add(new ProximityAgentSensor((float) Math.toRadians(10), 0f,
                PROXIMITY_SENSOR_RANGE, PROXIMITY_SENSOR_FOV));
        sensors.add(new ProximityAgentSensor((float) Math.toRadians(-10), 0f,
                PROXIMITY_SENSOR_RANGE, PROXIMITY_SENSOR_FOV));

        // At +/- 40 degrees
        sensors.add(new ProximityAgentSensor((float) Math.toRadians(40), 0f,
                PROXIMITY_SENSOR_RANGE, PROXIMITY_SENSOR_FOV));
        sensors.add(new ProximityAgentSensor((float) Math.toRadians(-40), 0f,
                PROXIMITY_SENSOR_RANGE, PROXIMITY_SENSOR_FOV));

        // At +/- 75 degrees
        sensors.add(new ProximityAgentSensor((float) Math.toRadians(75), 0f,
                PROXIMITY_SENSOR_RANGE, PROXIMITY_SENSOR_FOV));
        sensors.add(new ProximityAgentSensor((float) Math.toRadians(-75), 0f,
                PROXIMITY_SENSOR_RANGE, PROXIMITY_SENSOR_FOV));

        // At +/- 140 degrees
        sensors.add(new ProximityAgentSensor((float) Math.toRadians(140), 0f,
                PROXIMITY_SENSOR_RANGE, PROXIMITY_SENSOR_FOV));
        sensors.add(new ProximityAgentSensor((float) Math.toRadians(-140), 0f,
                PROXIMITY_SENSOR_RANGE, PROXIMITY_SENSOR_FOV));

        // At 180 degrees
        sensors.add(new ProximityAgentSensor((float) Math.PI, 0f, PROXIMITY_SENSOR_RANGE,
                PROXIMITY_SENSOR_FOV));

        // Ultrasonic sensors
        // At 0 degrees
        sensors.add(new ProximityAgentSensor(0f, 0f, ULTRASONIC_SENSOR_RANGE,
                ULTRASONIC_SENSOR_FOV));

        // At +/- 40 degrees
        sensors.add(new ProximityAgentSensor((float) Math.toRadians(40), 0f,
                ULTRASONIC_SENSOR_RANGE, ULTRASONIC_SENSOR_FOV));
        sensors.add(new ProximityAgentSensor((float) Math.toRadians(-40), 0f,
                ULTRASONIC_SENSOR_RANGE, ULTRASONIC_SENSOR_FOV));

        // At +/- 90 degrees
        sensors.add(new ProximityAgentSensor((float) Math.PI / 2, 0f, ULTRASONIC_SENSOR_RANGE,
                ULTRASONIC_SENSOR_FOV));
        sensors.add(new ProximityAgentSensor((float) -Math.PI / 2, 0f, ULTRASONIC_SENSOR_RANGE,
                ULTRASONIC_SENSOR_FOV));
    }

    @Override
    public List<AgentSensor> getSensors() {
        return sensors;
    }

    @Override
    public Double2D step(List<SensorReading> sensorReadings) {
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = sensorReadings.get(i).getValues().get(0);
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

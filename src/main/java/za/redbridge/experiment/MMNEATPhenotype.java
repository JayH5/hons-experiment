package za.redbridge.experiment;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sim.util.Double2D;
import za.redbridge.experiment.MMNEAT.MMNEATNetwork;
import za.redbridge.experiment.MMNEAT.SensorMorphology;
import za.redbridge.simulator.phenotype.Phenotype;
import za.redbridge.simulator.sensor.AgentSensor;
import za.redbridge.simulator.sensor.ProximityAgentSensor;
import za.redbridge.simulator.sensor.SensorReading;

/**
 * Created by jamie on 2014/09/09.
 */
public class MMNEATPhenotype implements Phenotype {

    private static final float DEFAULT_SENSOR_RANGE = 1f;
    private static final float DEFAULT_SENSOR_FOV = 0.2f;

    private final MMNEATNetwork network;
    private final double[] inputs;

    private final List<AgentSensor> sensors;

    public MMNEATPhenotype(MMNEATNetwork network) {
        this.network = network;

        // Initialise sensors
        SensorMorphology morphology = network.getSensorMorphology();
        final int numSensors = morphology.getNumSensors();
        sensors = new ArrayList<>(numSensors);
        for (int i = 0; i < numSensors; i++) {
            sensors.add(new ProximityAgentSensor(
                    (float) morphology.getSensorBearing(i),
                    (float) morphology.getSensorOrientation(i),
                    DEFAULT_SENSOR_RANGE,
                    DEFAULT_SENSOR_FOV));
        }

        inputs = new double[numSensors];
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

        return new Double2D(output.getData(0), output.getData(1));
    }

    @Override
    public Phenotype clone() {
        return new MMNEATPhenotype(network);
    }

    @Override
    public void configure(Map<String, Object> stringObjectMap) {
        throw new UnsupportedOperationException();
    }
}

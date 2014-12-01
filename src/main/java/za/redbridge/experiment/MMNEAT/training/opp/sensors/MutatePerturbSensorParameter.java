package za.redbridge.experiment.MMNEAT.training.opp.sensors;

import org.encog.ml.ea.train.EvolutionaryAlgorithm;

import java.util.List;
import java.util.Random;

import za.redbridge.experiment.MMNEAT.sensor.SensorConfiguration;
import za.redbridge.experiment.MMNEAT.sensor.parameter.SensorParameter;
import za.redbridge.experiment.MMNEAT.sensor.parameter.spec.ParameterType;

/**
 * Created by jamie on 2014/09/08.
 */
public class MutatePerturbSensorParameter implements MutateSensor {

    private EvolutionaryAlgorithm trainer;

    private final double sigma;
    private final ParameterType parameterType;

    /**
     * Construct the perturbing mutator.
     *
     */
    public MutatePerturbSensorParameter(double sigma, ParameterType parameterType) {
        this.sigma = sigma;
        this.parameterType = parameterType;
    }

    @Override
    public EvolutionaryAlgorithm getTrainer() {
        return trainer;
    }

    @Override
    public void init(EvolutionaryAlgorithm theTrainer) {
        this.trainer = theTrainer;
    }

    @Override
    public void mutateSensor(Random rnd, SensorConfiguration sensorConfiguration) {
        // Mutate parameter
        double delta = rnd.nextGaussian() * sigma;
        SensorParameter sensorParameter =
                sensorConfiguration.getSensorParameterSet().getParameter(parameterType);

        float newValue = (float) (sensorParameter.getValue() + delta);
        sensorParameter.setValue(newValue);
    }

    @Override
    public void mutateSensorGroup(Random rnd, List<SensorConfiguration> configurations) {
        // Mutate parameter
        double delta = rnd.nextGaussian() * sigma;

        for (SensorConfiguration configuration : configurations) {
            SensorParameter sensorParameter =
                    configuration.getSensorParameterSet().getParameter(parameterType);
            float newValue = (float) (sensorParameter.getValue() + delta);
            sensorParameter.setValue(newValue);
        }
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("[")
                .append(this.getClass().getSimpleName())
                .append(":sigma=")
                .append(this.sigma)
                .append(":parameterType=")
                .append(this.parameterType)
                .append("]");
        return result.toString();
    }
}

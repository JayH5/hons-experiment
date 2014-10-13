package za.redbridge.experiment.MMNEAT.training.opp.sensors;

import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATPopulation;

import java.util.Random;

import za.redbridge.experiment.MMNEAT.training.MMNEATNeuronGene;
import za.redbridge.simulator.Utils;

/**
 * Created by jamie on 2014/09/08.
 */
public class MutatePerturbSensorPosition implements MutateSensorPosition {

    private EvolutionaryAlgorithm trainer;

    private final double bearingSigma;
    private final double orientationSigma;

    /**
     * Construct the perturbing mutator.
     *
     */
    public MutatePerturbSensorPosition(double bearingSigma, double orientationSigma) {
        this.bearingSigma = bearingSigma;
        this.orientationSigma = orientationSigma;
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
    public void mutatePosition(Random rnd, MMNEATNeuronGene neuronGene, double bearingRange,
            double orientationRange) {
        // Mutate bearing
        double delta = rnd.nextGaussian() * bearingSigma;
        double bearing = neuronGene.getInputSensorBearing() + delta;
        bearing = Utils.wrapAngle(bearing);
        neuronGene.setInputSensorBearing(bearing);

        // And orientation...
        delta = rnd.nextGaussian() * orientationSigma;
        double orientation = neuronGene.getInputSensorOrientation() + delta;
        orientation = NEATPopulation.clampWeight(orientation, orientationRange);
        neuronGene.setInputSensorOrientation(orientation);
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("[")
                .append(this.getClass().getSimpleName())
                .append(":bearingSigma=")
                .append(this.bearingSigma)
                .append(":orientationSigma=")
                .append(this.orientationSigma)
                .append("]");
        return result.toString();
    }
}

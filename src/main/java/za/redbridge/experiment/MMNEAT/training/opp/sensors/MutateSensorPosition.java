package za.redbridge.experiment.MMNEAT.training.opp.sensors;

import org.encog.ml.ea.train.EvolutionaryAlgorithm;

import java.util.Random;

import za.redbridge.experiment.MMNEAT.training.MMNEATNeuronGene;

/**
 * Created by jamie on 2014/09/08.
 */
public interface MutateSensorPosition {
    /**
     * @return The training class that this mutator is being used with.
     */
    EvolutionaryAlgorithm getTrainer();

    /**
     * Setup the link mutator.
     *
     * @param theTrainer
     *            The training class that this mutator is used with.
     */
    void init(EvolutionaryAlgorithm theTrainer);

    /**
     * Perform the weight mutation on the specified link.
     *
     * @param rnd
     *            A random number generator.
     * @param neuronGene
     *            The input neuron to mutate.
     * @param bearingRange
     *            The bearing range, bearings are between -bearingRange and +bearingRange.
     * @param orientationRange
     *            The orientation range, orientations are between -orientationRange and
     *            +orientationRange.
     */
    void mutatePosition(Random rnd, MMNEATNeuronGene neuronGene, double bearingRange,
                        double orientationRange);
}

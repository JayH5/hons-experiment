package za.redbridge.experiment.MMNEAT.opp.sensors;

import org.encog.ml.ea.train.EvolutionaryAlgorithm;

import java.util.List;
import java.util.Random;

import za.redbridge.experiment.MMNEAT.MMNEATGenome;
import za.redbridge.experiment.MMNEAT.MMNEATNeuronGene;

/**
 * Created by jamie on 2014/09/08.
 */
public interface SelectSensors {
    /**
     * @return The trainer being used.
     */
    EvolutionaryAlgorithm getTrainer();

    /**
     * Setup the selector.
     * @param theTrainer The trainer.
     */
    void init(EvolutionaryAlgorithm theTrainer);

    /**
     * Select links from the specified genome.
     * @param rnd A random number generator.
     * @param genome The genome to select from.
     * @return A List of link genomes.
     */
    List<MMNEATNeuronGene> selectLinks(Random rnd, MMNEATGenome genome);
}

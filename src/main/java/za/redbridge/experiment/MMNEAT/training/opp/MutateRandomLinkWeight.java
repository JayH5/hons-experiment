package za.redbridge.experiment.MMNEAT.training.opp;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.opp.links.MutateLinkWeight;

import java.util.Random;

/**
 * Mutate a link weight to a random value within the weight range.
 *
 * Created by jamie on 2014/10/08.
 */
public class MutateRandomLinkWeight implements MutateLinkWeight {

    private EvolutionaryAlgorithm trainer;

    @Override
    public EvolutionaryAlgorithm getTrainer() {
        return trainer;
    }

    @Override
    public void init(EvolutionaryAlgorithm theTrainer) {
        trainer = theTrainer;
    }

    @Override
    public void mutateWeight(Random rnd, NEATLinkGene linkGene, double weightRange) {
        linkGene.setWeight(RangeRandomizer.randomize(rnd, -weightRange, weightRange));
    }
}

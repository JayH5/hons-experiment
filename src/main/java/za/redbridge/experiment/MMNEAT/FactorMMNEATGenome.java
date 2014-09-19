package za.redbridge.experiment.MMNEAT;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATGenomeFactory;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

import java.util.List;
import java.util.Random;

import za.redbridge.experiment.MMNEAT.training.MMNEATGenome;

/**
 * Created by jamie on 2014/09/08.
 */
public class FactorMMNEATGenome implements NEATGenomeFactory {
    @Override
    public NEATGenome factor(List<NEATNeuronGene> neurons, List<NEATLinkGene> links, int inputCount,
            int outputCount) {
        return new MMNEATGenome(neurons, links, inputCount, outputCount);
    }

    @Override
    public NEATGenome factor(Random rnd, NEATPopulation pop, int inputCount, int outputCount,
            double connectionDensity) {
        return new MMNEATGenome(rnd, pop, outputCount, connectionDensity);
    }

    @Override
    public Genome factor() {
        return new MMNEATGenome();
    }

    @Override
    public Genome factor(Genome other) {
        return new MMNEATGenome((MMNEATGenome) other);
    }
}

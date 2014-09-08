package za.redbridge.experiment.MMNEAT;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.MLMethod;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.genetic.GeneticError;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.neat.NEATLink;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jamie on 2014/09/08.
 */
public class MMNEATCODEC implements GeneticCODEC, Serializable {

    private static final long serialVersionUID = -5767773750949848124L;

    @Override
    public MLMethod decode(Genome genome) {
        final MMNEATGenome neatGenome = (MMNEATGenome) genome;
        final NEATPopulation pop = (NEATPopulation) neatGenome.getPopulation();
        final List<NEATNeuronGene> neuronsChromosome = neatGenome.getNeuronsChromosome();
        final List<NEATLinkGene> linksChromosome = neatGenome.getLinksChromosome();

        if (neuronsChromosome.get(0).getNeuronType() != NEATNeuronType.Bias) {
            throw new NeuralNetworkError(
                    "The first neuron must be the bias neuron, this genome is invalid.");
        }

        final List<NEATLink> links = new ArrayList<NEATLink>();
        final ActivationFunction[] afs = new ActivationFunction[neuronsChromosome.size()];

        for (int i = 0; i < afs.length; i++) {
            afs[i] = neuronsChromosome.get(i).getActivationFunction();
        }

        final Map<Long, Integer> lookup = new HashMap<>();
        for (int i = 0; i < neuronsChromosome.size(); i++) {
            final NEATNeuronGene neuronGene = neuronsChromosome.get(i);
            lookup.put(neuronGene.getId(), i);
        }

        // loop over connections
        for (int i = 0; i < linksChromosome.size(); i++) {
            final NEATLinkGene linkGene = linksChromosome.get(i);
            if (linkGene.isEnabled()) {
                links.add(new NEATLink(lookup.get(linkGene.getFromNeuronID()),
                        lookup.get(linkGene.getToNeuronID()), linkGene
                        .getWeight()));
            }

        }

        Collections.sort(links);

        MMNEATNetwork network = new MMNEATNetwork(neatGenome.getInputCount(),
                neatGenome.getOutputCount(), links, afs);

        network.setActivationCycles(pop.getActivationCycles());
        return network;
    }

    @Override
    public Genome encode(MLMethod phenotype) {
        throw new GeneticError("Encoding of a NEAT network is not supported.");
    }
}

package za.redbridge.experiment.MMNEAT.training.opp;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovation;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.opp.NEATMutation;

import java.util.Random;

import za.redbridge.experiment.MMNEAT.training.MMNEATNeuronGene;

/**
 * Created by jamie on 2014/09/20.
 */
public class MMNEATMutateAddNode extends NEATMutation {

    @Override
    public void performOperation(Random rnd, Genome[] parents, int parentIndex, Genome[] offspring,
            int offspringIndex) {
        final NEATGenome target = obtainGenome(parents, parentIndex, offspring,
                offspringIndex);
        int countTrysToFindOldLink = getOwner().getMaxTries();

        final NEATPopulation pop = ((NEATPopulation) target.getPopulation());

        // the link to split
        NEATLinkGene splitLink = null;

        final int sizeBias = ((NEATGenome)parents[0]).getInputCount()
                + ((NEATGenome)parents[0]).getOutputCount() + 10;

        // if there are not at least
        int upperLimit;
        if (target.getLinksChromosome().size() < sizeBias) {
            upperLimit = target.getNumGenes() - 1
                    - (int) Math.sqrt(target.getNumGenes());
        } else {
            upperLimit = target.getNumGenes() - 1;
        }

        while ((countTrysToFindOldLink--) > 0) {
            // choose a link, use the square root to prefer the older links
            final int i = RangeRandomizer.randomInt(0, upperLimit);
            final NEATLinkGene link = target.getLinksChromosome().get(i);

            // get the from neuron
            final long fromNeuron = link.getFromNeuronID();

            if ((link.isEnabled())
                    && (target.getNeuronsChromosome()
                    .get(getElementPos(target, fromNeuron))
                    .getNeuronType() != NEATNeuronType.Bias)) {
                splitLink = link;
                break;
            }
        }

        if (splitLink == null) {
            return;
        }

        splitLink.setEnabled(false);

        final long from = splitLink.getFromNeuronID();
        final long to = splitLink.getToNeuronID();

        final NEATInnovation innovation = ((NEATPopulation)getOwner().getPopulation()).getInnovations()
                .findInnovationSplit(from, to);

        // add the splitting neuron
        final ActivationFunction af = ((NEATPopulation)getOwner().getPopulation())
                .getActivationFunctions().pick(new Random());

        target.getNeuronsChromosome().add(
                new MMNEATNeuronGene(NEATNeuronType.Hidden, af, innovation
                        .getNeuronID(), innovation.getInnovationID()));

        // add the other two sides of the link
        createLink(target, from, innovation.getNeuronID(),
                splitLink.getWeight());
        createLink(target, innovation.getNeuronID(), to, pop.getWeightRange());

        target.sortGenes();
    }

}
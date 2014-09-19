package za.redbridge.experiment.MMNEAT.training.opp;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.training.NEATInnovation;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.neural.neat.training.opp.NEATMutation;

import java.util.List;
import java.util.Random;

import za.redbridge.experiment.MMNEAT.MMNEATPopulation;
import za.redbridge.experiment.MMNEAT.training.MMNEATGenome;
import za.redbridge.experiment.MMNEAT.training.MMNEATNeuronGene;
import za.redbridge.experiment.sensor.SensorType;

/**
 * Mutation to add a single new input node to the network.
 *
 * Created by jamie on 2014/09/16.
 */
public class MMNEATMutateAddSensor extends NEATMutation {
    @Override
    public void performOperation(Random rnd, Genome[] parents, int parentIndex, Genome[] offspring,
            int offspringIndex) {
        final MMNEATGenome target =
                (MMNEATGenome) obtainGenome(parents, parentIndex, offspring, offspringIndex);

        final MMNEATPopulation pop = ((MMNEATPopulation) target.getPopulation());

        final NEATInnovation innovation = pop.getInnovations().findInnovation(pop.assignGeneID());

        // NOTE: This will not work for HyperNEAT
        final ActivationFunction af = ((MMNEATPopulation) getOwner().getPopulation())
                .getActivationFunctions().pickFirst();

        // Create the input node with random sensor configuration
        MMNEATNeuronGene inputNeuron = new MMNEATNeuronGene(NEATNeuronType.Input, af,
                innovation.getNeuronID(), innovation.getInnovationID());
        inputNeuron.setInputSensorBearing(RangeRandomizer.randomize(rnd,
                -pop.getSensorBearingRange(), pop.getSensorBearingRange()));
        inputNeuron.setInputSensorOrientation(RangeRandomizer.randomize(rnd,
                -pop.getSensorOrientationRange(), pop.getSensorOrientationRange()));
        inputNeuron.setInputSensorType(pickRandomSensorType(rnd));
        target.addInputNeuron(inputNeuron);

        // Create the links to the new input in the same manner as links are added when the
        // population is initialized. TODO: Make sure this makes sense
        List<NEATLinkGene> links = target.getLinksChromosome();
        List<NEATNeuronGene> neurons = target.getNeuronsChromosome();
        boolean haveLinkToInput = false;
        for (int i = 0; i < target.getOutputCount(); i++) {
            // make sure we have at least one connection
            if (!haveLinkToInput || rnd.nextDouble() < pop.getInitialConnectionDensity()) {
                long toID = neurons.get(target.getInputCount() + i + 1).getId();
                double weight = RangeRandomizer.randomize(rnd, -pop.getWeightRange(),
                        pop.getWeightRange());
                NEATLinkGene gene = new NEATLinkGene(innovation.getNeuronID(), toID, true,
                        pop.assignInnovationID(), weight);
                links.add(gene);
                haveLinkToInput = true;
            }
        }

        target.addInputNeuron(inputNeuron);
        target.sortGenes();
    }

    private static SensorType pickRandomSensorType(Random rnd) {
        double random = rnd.nextDouble();
        if (random < 0.4) {
            return SensorType.COLLISION;
        } else if (random < 0.8) {
            return SensorType.RESOURCE;
        } else {
            return SensorType.TARGET_AREA;
        }
    }

}

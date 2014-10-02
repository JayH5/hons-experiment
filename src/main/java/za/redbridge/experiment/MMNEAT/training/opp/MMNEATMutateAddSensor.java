package za.redbridge.experiment.MMNEAT.training.opp;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.training.NEATInnovation;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.neural.neat.training.opp.NEATMutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import za.redbridge.experiment.MMNEAT.MMNEATPopulation;
import za.redbridge.experiment.MMNEAT.training.MMNEATGenome;
import za.redbridge.experiment.MMNEAT.training.MMNEATNeuronGene;
import za.redbridge.experiment.sensor.SensorType;

/**
 * Mutation to add a single new input node to the network. This mutation adds a new sensor in a
 * random position and with a random type. The sensor is connected to other (non-input) nodes in
 * the network randomly but with the same connection density as the original network was created
 * with. The weights of the connections are set to 0 (and so the new sensor will not have any effect
 * until one of the weights is mutated).
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

        // Get the list of all neurons that aren't input nodes or the bias node
        List<NEATNeuronGene> neurons = new ArrayList<>(target.getNeuronsChromosome());
        neurons.removeAll(target.getInputNeuronsChromosome());
        neurons.remove(target.getBiasGene());

        // Iterate through list of those neurons, pick neurons to link to randomly but with same
        // connection density as when network was initialized
        boolean haveLinkToInput = false;
        long fromID = inputNeuron.getId();
        for (NEATNeuronGene neuron : neurons) {
            // Connect with same connection density as the network was initialized with
            if (rnd.nextDouble() < pop.getInitialConnectionDensity()) {
                long toID = neuron.getId();
                createLink(target, fromID, toID, 0.0);
                haveLinkToInput = true;
            }
        }

        // Ensure there is at least one link
        if (!haveLinkToInput) {
            // Pick a random neuron and link it up
            int index = (int) (rnd.nextDouble() * neurons.size());
            long toID = neurons.get(index).getId();
            createLink(target, fromID, toID, 0.0);
        }

    }

    /**
     * Add sensors with a preference for simple proximity sensors. Don't add bottom proximity as a
     * single sensor is enough to detect the target area.
     */
    private static SensorType pickRandomSensorType(Random rnd) {
        double random = rnd.nextDouble();
        if (random < 0.3) {
            return SensorType.ULTRASONIC;
        } else {
            return SensorType.PROXIMITY;
        }
    }

}

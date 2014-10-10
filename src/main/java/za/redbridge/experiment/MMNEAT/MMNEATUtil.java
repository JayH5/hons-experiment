package za.redbridge.experiment.MMNEAT;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.opp.CompoundOperator;
import org.encog.ml.ea.opp.selection.TruncationSelection;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.opp.NEATMutateAddLink;
import org.encog.neural.neat.training.opp.NEATMutateRemoveLink;
import org.encog.neural.neat.training.opp.NEATMutateWeights;
import org.encog.neural.neat.training.opp.links.MutatePerturbLinkWeight;
import org.encog.neural.neat.training.opp.links.MutateResetLinkWeight;
import org.encog.neural.neat.training.opp.links.SelectFixed;
import org.encog.neural.neat.training.opp.links.SelectProportion;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;

import za.redbridge.experiment.MMNEAT.training.opp.MMNEATCrossover;
import za.redbridge.experiment.MMNEAT.training.opp.MMNEATMutateAddNode;
import za.redbridge.experiment.MMNEAT.training.opp.MMNEATMutateAddSensor;
import za.redbridge.experiment.MMNEAT.training.opp.MMNEATMutatePositions;
import za.redbridge.experiment.MMNEAT.training.opp.sensors.MutatePerturbSensorPosition;
import za.redbridge.experiment.MMNEAT.training.opp.sensors.SelectSensorsFixed;
import za.redbridge.experiment.sensor.SensorType;

/**
 * Created by jamie on 2014/09/08.
 */
public final class MMNEATUtil {

    public static TrainEA constructNEATTrainer(CalculateScore calculateScore, int outputCount,
            int populationSize) {
        final MMNEATPopulation pop = new MMNEATPopulation(outputCount, populationSize);
        pop.reset();
        return constructNEATTrainer(pop, calculateScore);
    }

    /**
     * Construct a NEAT trainer.
     * @param population The population.
     * @param calculateScore The score function.
     * @return The NEAT EA trainer.
     */
    public static TrainEA constructNEATTrainer(NEATPopulation population,
            CalculateScore calculateScore) {
        final TrainEA result = new TrainEA(population, calculateScore);

        // Speciation
        result.setSpeciation(new OriginalNEATSpeciation());

        // Selection
        result.setSelection(new TruncationSelection(result, 0.3));

        // Create compound operator for weight mutation
        CompoundOperator weightMutation = new CompoundOperator();
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectFixed(1), new MutatePerturbLinkWeight(0.004)));
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectFixed(2), new MutatePerturbLinkWeight(0.004)));
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectFixed(3), new MutatePerturbLinkWeight(0.004)));
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectProportion(0.004),
                        new MutatePerturbLinkWeight(0.004)));
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectFixed(1), new MutatePerturbLinkWeight(0.2)));
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectFixed(2), new MutatePerturbLinkWeight(0.2)));
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectFixed(3), new MutatePerturbLinkWeight(0.2)));
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectProportion(0.02),
                        new MutatePerturbLinkWeight(0.2)));
        weightMutation.getComponents().add(0.03,
                new NEATMutateWeights(new SelectFixed(1), new MutateResetLinkWeight()));
        weightMutation.getComponents().add(0.03,
                new NEATMutateWeights(new SelectFixed(2), new MutateResetLinkWeight()));
        weightMutation.getComponents().add(0.03,
                new NEATMutateWeights(new SelectFixed(3), new MutateResetLinkWeight()));
        weightMutation.getComponents().add(0.01,
                new NEATMutateWeights(new SelectProportion(0.02), new MutateResetLinkWeight()));
        weightMutation.getComponents().finalizeStructure();

        // Champ operator limits mutation operator on best genome (seems unused for now)
        result.setChampMutation(weightMutation);

        // Add all the operators, probability should sum to 1
        result.addOperation(0.5, new MMNEATCrossover());
        result.addOperation(0.443, weightMutation);
        result.addOperation(0.001, new MMNEATMutateAddNode());
        result.addOperation(0.005, new NEATMutateAddLink());
        result.addOperation(0.001, new NEATMutateRemoveLink());

        // Add the sensor position mutator
        result.addOperation(0.049, new MMNEATMutatePositions(
                new SelectSensorsFixed(1), new MutatePerturbSensorPosition(1, 1)));

        // Add sensor mutation
        double connectionDensity = population.getInitialConnectionDensity();
        // Proximity sensors "cheaper" prefer to evolve them
        // they are also short range and produce 0 most of the time so perturb the weight more
        result.addOperation(0.0006, new MMNEATMutateAddSensor(SensorType.PROXIMITY,
                connectionDensity, new MutateResetLinkWeight()));
        result.addOperation(0.0004, new MMNEATMutateAddSensor(SensorType.ULTRASONIC,
                connectionDensity, new MutatePerturbLinkWeight(0.2)));


        result.getOperators().finalizeStructure();

        result.setCODEC(new MMNEATCODEC());

        return result;
    }
}

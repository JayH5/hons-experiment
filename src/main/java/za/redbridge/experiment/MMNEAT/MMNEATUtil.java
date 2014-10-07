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
                new NEATMutateWeights(new SelectFixed(1), new MutatePerturbLinkWeight(0.02)));
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectFixed(2), new MutatePerturbLinkWeight(0.02)));
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectFixed(3), new MutatePerturbLinkWeight(0.02)));
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectProportion(0.02),
                        new MutatePerturbLinkWeight(0.02)));
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectFixed(1), new MutatePerturbLinkWeight(1)));
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectFixed(2), new MutatePerturbLinkWeight(1)));
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectFixed(3), new MutatePerturbLinkWeight(1)));
        weightMutation.getComponents().add(0.1125,
                new NEATMutateWeights(new SelectProportion(0.02), new MutatePerturbLinkWeight(1)));
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
        result.addOperation(0.444, weightMutation);
        result.addOperation(0.0005, new MMNEATMutateAddNode());
        result.addOperation(0.005, new NEATMutateAddLink());
        result.addOperation(0.0005, new NEATMutateRemoveLink());

        // Add the sensor position mutator
        result.addOperation(0.045, new MMNEATMutatePositions(
                new SelectSensorsFixed(1), new MutatePerturbSensorPosition(1, 1)));

        // Add sensor mutation - use the smallest link perturb operation
        result.addOperation(0.005,
                new MMNEATMutateAddSensor(0.5, new MutatePerturbLinkWeight(0.02)));


        result.getOperators().finalizeStructure();

        result.setCODEC(new MMNEATCODEC());

        return result;
    }
}

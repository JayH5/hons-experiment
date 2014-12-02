package za.redbridge.experiment.MMNEAT;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.opp.CompoundOperator;
import org.encog.ml.ea.opp.OperationList;
import org.encog.ml.ea.opp.selection.TruncationSelection;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.opp.NEATMutateAddLink;
import org.encog.neural.neat.training.opp.NEATMutateWeights;
import org.encog.neural.neat.training.opp.links.MutatePerturbLinkWeight;
import org.encog.neural.neat.training.opp.links.MutateResetLinkWeight;
import org.encog.neural.neat.training.opp.links.SelectFixed;
import org.encog.neural.neat.training.opp.links.SelectProportion;

import za.redbridge.experiment.MMNEAT.sensor.SensorType;
import za.redbridge.experiment.MMNEAT.training.opp.MMNEATCrossover;
import za.redbridge.experiment.MMNEAT.training.opp.MMNEATMutateAddNode;
import za.redbridge.experiment.MMNEAT.training.opp.MMNEATMutateAddSensor;
import za.redbridge.experiment.MMNEAT.training.opp.MMNEATMutateIndividualSensor;
import za.redbridge.experiment.MMNEAT.training.opp.MMNEATMutateRemoveLink;
import za.redbridge.experiment.MMNEAT.training.opp.MMNEATMutateSensorGroup;
import za.redbridge.experiment.MMNEAT.training.opp.sensors.MutatePerturbSensorParameter;
import za.redbridge.experiment.MMNEAT.training.opp.sensors.SelectSensorsFixed;
import za.redbridge.experiment.MMNEAT.training.opp.sensors.SelectSensorsType;
import za.redbridge.experiment.MMNEAT.training.species.MMNEATSpeciation;


import static za.redbridge.experiment.MMNEAT.sensor.SensorType.PROXIMITY;
import static za.redbridge.experiment.MMNEAT.sensor.SensorType.ULTRASONIC;
import static za.redbridge.experiment.MMNEAT.sensor.parameter.spec.ParameterType.BEARING;
import static za.redbridge.experiment.MMNEAT.sensor.parameter.spec.ParameterType.FIELD_OF_VIEW;
import static za.redbridge.experiment.MMNEAT.sensor.parameter.spec.ParameterType.ORIENTATION;
import static za.redbridge.experiment.MMNEAT.sensor.parameter.spec.ParameterType.RANGE;

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
        result.setSpeciation(new MMNEATSpeciation());

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
        result.addOperation(0.32, new MMNEATCrossover());
        result.addOperation(0.375, weightMutation);
        result.addOperation(0.05, new MMNEATMutateAddNode());
        result.addOperation(0.05, new NEATMutateAddLink());
        result.addOperation(0.005, new MMNEATMutateRemoveLink());

        // Add the sensor position mutators
        CompoundOperator positionMutation = new CompoundOperator();
        OperationList positionMutationComponents = positionMutation.getComponents();
        positionMutationComponents.add(0.25, new MMNEATMutateIndividualSensor(
                new SelectSensorsFixed(1), new MutatePerturbSensorParameter(5.0f, BEARING)));
        positionMutationComponents.add(0.25, new MMNEATMutateIndividualSensor(
                new SelectSensorsFixed(2), new MutatePerturbSensorParameter(5.0f, BEARING)));
        positionMutationComponents.add(0.25, new MMNEATMutateIndividualSensor(
                new SelectSensorsFixed(1), new MutatePerturbSensorParameter(5.0f, ORIENTATION)));
        positionMutationComponents.add(0.25, new MMNEATMutateIndividualSensor(
                new SelectSensorsFixed(2), new MutatePerturbSensorParameter(5.0f, ORIENTATION)));
        positionMutationComponents.finalizeStructure();

        result.addOperation(0.1, positionMutation);

        // Add the sensor field mutators
        CompoundOperator fieldMutation = new CompoundOperator();
        OperationList fieldMutationComponents = fieldMutation.getComponents();
        fieldMutationComponents.add(0.5, new MMNEATMutateSensorGroup(
                new SelectSensorsType(ULTRASONIC), new MutatePerturbSensorParameter(5.0f, RANGE)));
        fieldMutationComponents.add(0.5, new MMNEATMutateSensorGroup(
                new SelectSensorsType(PROXIMITY), new MutatePerturbSensorParameter(5.0f, RANGE)));
        fieldMutationComponents.add(0.5, new MMNEATMutateSensorGroup(
                new SelectSensorsType(ULTRASONIC),
                new MutatePerturbSensorParameter(5.0f, FIELD_OF_VIEW)));
        fieldMutationComponents.add(0.5, new MMNEATMutateSensorGroup(
                new SelectSensorsType(PROXIMITY),
                new MutatePerturbSensorParameter(5.0f, FIELD_OF_VIEW)));
        fieldMutationComponents.finalizeStructure();

        result.addOperation(0.05, fieldMutation);

        // Add sensor mutation
        double connectionDensity = 0.1;
        CompoundOperator addSensorMutation = new CompoundOperator();
        OperationList addSensorComponents = addSensorMutation.getComponents();
        addSensorComponents.add(0.5, new MMNEATMutateAddSensor(SensorType.PROXIMITY,
                connectionDensity, new MutateResetLinkWeight()));
        addSensorComponents.add(0.5, new MMNEATMutateAddSensor(SensorType.ULTRASONIC,
                connectionDensity, new MutatePerturbLinkWeight(0.2)));
        addSensorComponents.finalizeStructure();

        result.addOperation(0.05, addSensorMutation);


        result.getOperators().finalizeStructure();

        result.setCODEC(new MMNEATCODEC());

        return result;
    }
}

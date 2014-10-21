package za.redbridge.experiment;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import org.encog.Encog;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import za.redbridge.experiment.MMNEAT.MMNEATNetwork;
import za.redbridge.experiment.MMNEAT.MMNEATPopulation;
import za.redbridge.experiment.MMNEAT.MMNEATUtil;
import za.redbridge.experiment.MMNEAT.SensorMorphology;
import za.redbridge.experiment.NEAT.NEATPopulation;
import za.redbridge.experiment.NEAT.NEATUtil;
import za.redbridge.simulator.config.SimConfig;

/**
 * Entry point for the experiment platform.
 *
 * Created by jamie on 2014/09/09.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        Args options = new Args();
        new JCommander(options, args);

        log.info(options.toString());

        SimConfig simConfig;
        if (options.configFile != null && !options.configFile.isEmpty()) {
            simConfig = new SimConfig(options.configFile);
        } else {
            simConfig = new SimConfig();
        }

        SensorMorphology morphology = null;
        if (options.control) {
            if (options.morphologyPath != null && !options.morphologyPath.isEmpty()) {
                MMNEATNetwork network = (MMNEATNetwork) loadNetwork(options.morphologyPath);
                morphology = network.getSensorMorphology();
            } else {
                morphology = new KheperaIIIMorphology();
            }
        }

        ScoreCalculator calculateScore =
                new ScoreCalculator(simConfig, options.simulationRuns, morphology);

        if (options.genomePath != null && !options.genomePath.isEmpty()) {
            NEATNetwork network = loadNetwork(options.genomePath);
            calculateScore.demo(network);
            return;
        }

        final NEATPopulation population;
        if (!options.control) {
            population = new MMNEATPopulation(2, options.populationSize);
        } else {
            population = new NEATPopulation(morphology.getNumSensors(), 2, options.populationSize);
        }
        population.setInitialConnectionDensity(options.connectionDensity);
        population.reset();

        log.debug("Population initialized");

        final EvolutionaryAlgorithm train;
        if (!options.control) {
            train = MMNEATUtil.constructNEATTrainer(population, calculateScore);
        } else {
            train = NEATUtil.constructNEATTrainer(population, calculateScore);
        }

        final StatsRecorder statsRecorder = new StatsRecorder(train, calculateScore);
        for (int i = 0; i < options.numIterations; i++) {
            train.iteration();
            statsRecorder.recordIterationStats();
        }

        log.debug("Training complete");
        Encog.getInstance().shutdown();
    }

    static NEATNetwork loadNetwork(String filepath) {
        NEATNetwork network = null;
        Path path = Paths.get(filepath);
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            network = (NEATNetwork) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("Unable to load network from file", e);
        }

        return network;
    }

    private static class Args {
        @Parameter(names = "-c", description = "Simulation config file to load")
        private String configFile = "config/mediumSimConfig.yml";

        @Parameter(names = "-i", description = "Number of simulation iterations to train for")
        private int numIterations = 250;

        @Parameter(names = "-p", description = "Initial population size")
        private int populationSize = 100;

        @Parameter(names = "--sim-runs", description = "Number of simulation runs per iteration")
        private int simulationRuns = 5;

        @Parameter(names = "--conn-density", description = "Adjust the initial connection density"
                + " for the population")
        private double connectionDensity = 0.5;

        @Parameter(names = "--demo", description = "Show a GUI demo of a given genome")
        private String genomePath = null;

        @Parameter(names = "--control", description = "Run with the control case")
        private boolean control = false;

        @Parameter(names = "--morphology", description = "For use with the control case, provide"
                + " the path to a serialized MMNEATNetwork to have its morphology used for the"
                + " control case")
        private String morphologyPath = null;

        @Override
        public String toString() {
            return "Options: \n"
                    + "\tConfig file path: " + configFile + "\n"
                    + "\tNumber of simulation steps: " + numIterations + "\n"
                    + "\tPopulation size: " + populationSize + "\n"
                    + "\tNumber of simulation tests per iteration: " + simulationRuns + "\n"
                    + "\tInitial connection density: " + connectionDensity + "\n"
                    + "\tDemo network config path: " + genomePath + "\n"
                    + "\tRunning with the control case: " + control + "\n"
                    + "\tMorphology path: " + morphologyPath;
        }
    }
}

package za.redbridge.experiment;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import org.encog.Encog;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import za.redbridge.experiment.MMNEAT.MMNEATNetwork;
import za.redbridge.experiment.MMNEAT.MMNEATPopulation;
import za.redbridge.experiment.MMNEAT.MMNEATUtil;
import za.redbridge.simulator.config.SimConfig;
import za.redbridge.simulator.khepera.KheperaIIIPhenotype;

/**
 * Created by jamie on 2014/09/09.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Args options = new Args();
        new JCommander(options, args);

        SimConfig simConfig;
        if (options.configFile != null && !options.configFile.isEmpty()) {
            simConfig = new SimConfig(options.configFile);
        } else {
            simConfig = new SimConfig();
        }

        ScoreCalculator calculateScore = new ScoreCalculator(simConfig, options.simulationRuns);

        if (options.genomePath != null && !options.genomePath.isEmpty()) {
            MMNEATNetwork network = loadNetwork(options.genomePath);
            calculateScore.demo(network);
            return;
        }

        final NEATPopulation population;
        if (!options.control) {
            population = new MMNEATPopulation(2, options.populationSize);
        } else {
            population =
                    new NEATPopulation(KheperaIIIPhenotype.NUM_SENSORS, 2, options.populationSize);
        }
        population.reset();

        System.out.println("Population initialized");

        final EvolutionaryAlgorithm train;
        if (!options.control) {
            train = MMNEATUtil.constructNEATTrainer(population, calculateScore);
        } else {
            train = NEATUtil.constructNEATTrainer(population, calculateScore);
        }

        String date = getDateFolderName();

        for (int i = 0; i < options.numIterations; i++) {
            train.iteration();

            double averageScore = calculateScore.getEpochAverageScore();
            double bestScore = calculateScore.getEpochBestScore();
            System.out.println("Epoch #" + train.getIteration() + ", average score: "
                    + averageScore + ", best score: " + bestScore);

            calculateScore.resetScoreCounters();

            // Save the network
            NEATNetwork network = (NEATNetwork) train.getCODEC().decode(train.getBestGenome());
            saveNetwork(network, "epoch " + train.getIteration(), date);
        }

        System.out.println("Training complete");
        Encog.getInstance().shutdown();
    }

    static String getDateFolderName() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        return df.format(new Date());
    }

    static void saveNetwork(NEATNetwork network, String name, String folder)
            throws IOException {
        File dir = new File("networks/" + folder);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create directory structure for networks");
        }

        Path path = Paths.get(new File(dir, name).toURI());
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path))) {
            out.writeObject(network);
        }
    }

    static MMNEATNetwork loadNetwork(String filepath) {
        MMNEATNetwork network = null;
        Path path = Paths.get(filepath);
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            network = (MMNEATNetwork) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found!");
            e.printStackTrace();
        }

        return network;
    }

    private static class Args {
        @Parameter(names = "-c", description = "Simulation config file to load")
        private String configFile = "config/simulation.yml";

        @Parameter(names = "-i", description = "Number of simulation iterations to train for")
        private int numIterations = 250;

        @Parameter(names = "-p", description = "Initial population size")
        private int populationSize = 50;

        @Parameter(names = "--sim-runs", description = "Number of simulation runs per iteration")
        private int simulationRuns = 3;

        @Parameter(names = "--demo", description = "Show a GUI demo of a given genome")
        private String genomePath = null;

        @Parameter(names = "--control", description = "Run with the control case")
        private boolean control = false;
    }
}

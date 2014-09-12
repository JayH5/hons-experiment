package za.redbridge.experiment;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import org.encog.Encog;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

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

        MMNEATPopulation population =
                new MMNEATPopulation(options.numSensors, 2, options.populationSize);
        population.reset();

        System.out.println("Population initialized");

        ScoreCalculator calculateScore = new ScoreCalculator(simConfig);

        EvolutionaryAlgorithm train = MMNEATUtil.constructNEATTrainer(population, calculateScore);

        String date = getDateFolderName();

        for (int i = 0; i < options.numIterations; i++) {
            train.iteration();
            System.out.println("Epoch #" + train.getIteration() + " Score: "
                    + calculateScore.getLastScore());

            // Save the network
            MMNEATNetwork network = (MMNEATNetwork) train.getCODEC().decode(train.getBestGenome());
            saveNetwork(network, "epoch " + train.getIteration(), date);
        }

        System.out.println("Training complete");
        Encog.getInstance().shutdown();
    }

    private static String getDateFolderName() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        return df.format(new Date());
    }

    private static void saveNetwork(MMNEATNetwork network, String name, String folder) {
        File dir = new File("networks/" + folder);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Path path = Paths.get(new File(dir, name).toURI());
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path))) {
            out.writeObject(network);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MMNEATNetwork loadNetwork(String name) {
        MMNEATNetwork network = null;
        Path path = Paths.get("networks", name);
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
        private String configFile = null;

        @Parameter(names = "-i", description = "Number of simulation iterations to train for")
        private int numIterations = 500;

        @Parameter(names = "-s", description = "Number of sensors")
        private int numSensors = 4;

        @Parameter(names = "--ui", description = "Display GUI")
        private boolean ui = false;

        @Parameter(names = "-p", description = "Initial population size")
        private int populationSize = 50;
    }
}

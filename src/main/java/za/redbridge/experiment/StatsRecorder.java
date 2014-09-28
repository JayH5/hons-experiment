package za.redbridge.experiment;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class for recording stats each epoch.
 *
 * Created by jamie on 2014/09/28.
 */
public class StatsRecorder {

    private static final Logger log = LoggerFactory.getLogger(StatsRecorder.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmm");

    private final EvolutionaryAlgorithm trainer;
    private final DescriptiveStatistics statistics;

    private final ExecutorService executor;

    private Genome currentBestGenome;

    private Path directory;
    private Path statsFile;

    public StatsRecorder(EvolutionaryAlgorithm trainer, ScoreCalculator calculator) {
        this.trainer = trainer;
        this.statistics = calculator.getStatistics();

        executor = Executors.newSingleThreadExecutor();

        initDirectories();
        initStatsFile();
    }

    private void initDirectories() {
        directory = Paths.get("networks", DATE_FORMAT.format(new Date()));
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            log.error("Unable to create directories", e);
        }
    }

    private void initStatsFile() {
        statsFile = directory.resolve("stats.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(statsFile)) {
            writer.write("epoch, max, min, mean, standev\n");
        } catch (IOException e) {
            log.error("Unable to initialize stats file", e);
        }
    }

    public void recordTrainingIteration() {
        int epoch = trainer.getIteration();
        double max = statistics.getMax();
        double min = statistics.getMin();
        double mean = statistics.getMean();
        double sd = statistics.getStandardDeviation();

        // Log and save stats
        log.info("Epoch " + epoch + " complete");
        log.info("Best score: " + max + ", average score: " + mean);
        saveStatsAsync(epoch, max, min, mean, sd);

        // Check if new best network and save it if so
        Genome newBestGenome = trainer.getBestGenome();
        if (newBestGenome != currentBestGenome) {
            log.info("New best genome! Epoch: " + epoch + ", score: " + newBestGenome.getScore());

            saveNetworkAsync(decodeGenome(newBestGenome), "epoch" + epoch);
            currentBestGenome = newBestGenome;
        }
        statistics.clear();
    }

    private NEATNetwork decodeGenome(Genome genome) {
        return (NEATNetwork) trainer.getCODEC().decode(genome);
    }

    private void saveStatsAsync(final int epoch, final double max, final double min,
            final double mean, final double sd) {
        executor.submit(() -> saveStats(epoch, max, min, mean, sd));
    }

    private void saveStats(int epoch, double max, double min, double mean, double sd) {
        String line = String.format("%d, %f, %f, %f, %f\n", epoch, max, min, mean, sd);

        final OpenOption[] options = {
                StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE
        };
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(statsFile, options))) {
            writer.append(line);
        } catch (IOException e) {
            log.error("Failed to append to log file", e);
        }
    }

    private void saveNetworkAsync(final NEATNetwork network, final String name) {
        executor.submit(() -> saveNetwork(network, name));
    }

    private void saveNetwork(NEATNetwork network, String name) {
        Path path = directory.resolve(name + ".ser");
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path))) {
            out.writeObject(network);
        } catch (IOException e) {
            log.error("Failed to save network", e);
        }
    }
}

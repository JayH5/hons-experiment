package za.redbridge.experiment;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.training.NEATGenome;
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

    private static final String PERFORMANCE_STATS_FILENAME = "performance.csv";
    private static final String SCORE_STATS_FILENAME = "scores.csv";
    private static final String SENSOR_STATS_FILENAME = "sensors.csv";

    private final EvolutionaryAlgorithm trainer;
    private final ScoreCalculator calculator;
    private final boolean evolvingMorphology;

    private final ExecutorService executor;

    private Genome currentBestGenome;

    private Path directory;
    private Path networksDir;

    private Path performanceStatsFile;
    private Path scoreStatsFile;
    private Path sensorStatsFile;

    public StatsRecorder(EvolutionaryAlgorithm trainer, ScoreCalculator calculator) {
        this.trainer = trainer;
        this.calculator = calculator;
        this.evolvingMorphology = calculator.isEvolvingMorphology();

        executor = Executors.newSingleThreadExecutor();

        initDirectories();
        initStatsFiles();
    }

    private void initDirectories() {
        String dateString = DATE_FORMAT.format(new Date());

        directory = Paths.get("results", dateString);
        networksDir = directory.resolve("networks");
        initDirectory(networksDir);
    }

    private static void initDirectory(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            log.error("Unable to create directories", e);
        }
    }

    private void initStatsFiles() {
        performanceStatsFile = directory.resolve(PERFORMANCE_STATS_FILENAME);
        initStatsFile(performanceStatsFile);

        scoreStatsFile = directory.resolve(SCORE_STATS_FILENAME);
        initStatsFile(scoreStatsFile);

        if (evolvingMorphology) {
            sensorStatsFile = directory.resolve(SENSOR_STATS_FILENAME);
            initStatsFile(sensorStatsFile);
        }
    }

    private static void initStatsFile(Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("epoch, max, min, mean, standev\n");
        } catch (IOException e) {
            log.error("Unable to initialize stats file", e);
        }
    }

    public void recordIterationStats() {
        int epoch = trainer.getIteration();
        log.info("Epoch " + epoch + " complete");

        recordStats(calculator.getPerformanceStatistics(), epoch, performanceStatsFile);

        recordStats(calculator.getScoreStatistics(), epoch, scoreStatsFile);

        if (evolvingMorphology) {
            recordStats(calculator.getSensorStatistics(), epoch, sensorStatsFile);
        }

        // Check if new best network and save it if so
        NEATGenome newBestGenome = (NEATGenome) trainer.getBestGenome();
        if (newBestGenome != currentBestGenome) {
            if (evolvingMorphology) {
                log.info("New best genome! Epoch: " + epoch + ", score: "
                        + newBestGenome.getScore() + ", num sensors: "
                        + newBestGenome.getInputCount());
            } else {
                log.info("New best genome! Epoch: " + epoch + ", score: "
                        + newBestGenome.getScore());
            }

            saveGenomeAsync(newBestGenome, "epoch" + epoch);
            currentBestGenome = newBestGenome;
        }
    }

    private void recordStats(DescriptiveStatistics stats, int epoch, Path filepath) {
        double max = stats.getMax();
        double min = stats.getMin();
        double mean = stats.getMean();
        double sd = stats.getStandardDeviation();
        stats.clear();

        log.debug("Recording stats - max: " + max + ", mean: " + mean);
        saveStatsAsync(filepath, epoch, max, min, mean, sd);
    }

    private NEATNetwork decodeGenome(Genome genome) {
        return (NEATNetwork) trainer.getCODEC().decode(genome);
    }

    private void saveStatsAsync(final Path path, final int epoch, final double max, final double min,
            final double mean, final double sd) {
        executor.submit(() -> saveStats(path, epoch, max, min, mean, sd));
    }

    private static void saveStats(Path path, int epoch, double max, double min, double mean,
            double sd) {
        String line = String.format("%d, %f, %f, %f, %f\n", epoch, max, min, mean, sd);

        final OpenOption[] options = {
                StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE
        };
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path, options))) {
            writer.append(line);
        } catch (IOException e) {
            log.error("Failed to append to log file", e);
        }
    }

    private void saveGenomeAsync(final NEATGenome genome, final String name) {
        executor.submit(() -> {
            GraphvizEngine.saveGenome(genome, networksDir.resolve(name + ".dot"));
            saveNetwork(decodeGenome(genome), networksDir.resolve(name + ".ser"));
        });
    }

    private static void saveNetwork(NEATNetwork network, Path path) {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path))) {
            out.writeObject(network);
        } catch (IOException e) {
            log.error("Failed to save network", e);
        }
    }
}

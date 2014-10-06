package za.redbridge.experiment;

import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import za.redbridge.experiment.MMNEAT.training.MMNEATNeuronGene;

/**
 * Basic methods to save a genome as a Graphviz ".dot" file so that it can be visualized.
 *
 * Created by jamie on 2014/10/06.
 */
public class GraphvizEngine {

    private static final Logger log = LoggerFactory.getLogger(GraphvizEngine.class);

    public static void saveGenome(NEATGenome genome, Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("digraph G {");
            writer.newLine();

            writeNodes(writer, genome);

            writer.write("}");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            log.error("Failed to save graphviz representation of network", e);
        }
    }

    private static void writeNodes(BufferedWriter writer, NEATGenome genome) throws IOException {
        List<NEATNeuronGene> neurons = genome.getNeuronsChromosome();
        for (NEATNeuronGene neuron : neurons) {
            writer.write("  ");
            writer.write(String.valueOf(neuron.getId()));
            if (neuron.getNeuronType() == NEATNeuronType.Input
                    && neuron instanceof MMNEATNeuronGene) {
                MMNEATNeuronGene mmneatNeuronGene = (MMNEATNeuronGene) neuron;
                writer.write(" [ label=\"" + neuron.getNeuronType()
                        + "\\n" + mmneatNeuronGene.getInputSensorType() + "\" ];");
            } else {
                writer.write(" [ label=\"" + neuron.getNeuronType() + "\" ];");
            }
            writer.newLine();
        }

        List<NEATLinkGene> links = genome.getLinksChromosome();
        for (NEATLinkGene link : links) {
            writer.write("  ");
            writer.write(link.getFromNeuronID() + " -> " + link.getToNeuronID());
            if (link.isEnabled()) {
                writer.write(" [ label=\"" + String.format("%.3f", link.getWeight()) + "\" ];");
            } else {
                writer.write(" [ style=\"dashed\" ];");
            }
            writer.newLine();
        }
    }
}

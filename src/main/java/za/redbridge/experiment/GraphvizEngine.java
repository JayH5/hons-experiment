package za.redbridge.experiment;

import org.encog.neural.neat.NEATLink;
import org.encog.neural.neat.NEATNetwork;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            writeNeuronGenes(writer, genome.getNeuronsChromosome());
            writeLinkGenes(writer, genome.getLinksChromosome());

            writer.write("}");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            log.error("Failed to save graphviz representation of network", e);
        }
    }

    private static void writeNeuronGenes(BufferedWriter writer, List<NEATNeuronGene> neurons)
            throws IOException {
        for (NEATNeuronGene neuron : neurons) {
            writer.write("  ");
            writer.write(String.valueOf(neuron.getId()));
            if (neuron.getNeuronType() == NEATNeuronType.Input
                    && neuron instanceof MMNEATNeuronGene) {
                MMNEATNeuronGene mmneatNeuronGene = (MMNEATNeuronGene) neuron;
                writer.write(" [ label=\"" + neuron.getNeuronType()
                        + " (" + neuron.getId() + ")"
                        + "\\n" + mmneatNeuronGene.getInputSensorType() + "\" ];");
            } else {
                writer.write(" [ label=\"" + neuron.getNeuronType()
                        + " (" + neuron.getId() + ")\" ];");
            }
            writer.newLine();
        }
    }

    private static void writeLinkGenes(BufferedWriter writer, List<NEATLinkGene> links)
            throws IOException {
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

    public static void saveNetwork(NEATNetwork network, Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("digraph G {");
            writer.newLine();

            writeNodes(writer, network);
            writeLinks(writer, network.getLinks());

            writer.write("}");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            log.error("Failed to save graphviz representation of network", e);
        }
    }

    private static void writeNodes(BufferedWriter writer, NEATNetwork network)
        throws IOException {
        // Reconstruct node information from links (only works for constant inputs/outputs)
        Map<Integer, String> nodes = new HashMap<>();
        NEATLink[] links = network.getLinks();
        int inputCount = network.getInputCount();
        int outputCount = network.getOutputCount();
        for (NEATLink link : links) {
            int fromNeuron = link.getFromNeuron();
            if (!nodes.containsKey(fromNeuron)) {
                nodes.put(fromNeuron, labelForNode(fromNeuron, inputCount, outputCount));
            }

            int toNeuron = link.getToNeuron();
            if (!nodes.containsKey(toNeuron)) {
                nodes.put(toNeuron, labelForNode(toNeuron, inputCount, outputCount));
            }
        }

        // Write to file
        for (Map.Entry<Integer, String> entry : nodes.entrySet()) {
            writer.write("  ");
            writer.write(entry.getKey().toString());
            writer.write(" [ label=\"" + entry.getValue() + " (" + entry.getKey() + ")\" ]");
            writer.newLine();
        }
    }

    private static String labelForNode(int node, int inputCount, int outputCount) {
        if (node == 0) {
            return NEATNeuronType.Bias.toString();
        }

        if (node < inputCount + 1) {
            return NEATNeuronType.Input.toString();
        }

        if (node < inputCount + outputCount + 1) {
            return NEATNeuronType.Output.toString();
        }

        return NEATNeuronType.Hidden.toString();
    }

    private static void writeLinks(BufferedWriter writer, NEATLink[] links)
            throws IOException {
        for (NEATLink link : links) {
            writer.write("  ");
            writer.write(link.getFromNeuron() + " -> " + link.getToNeuron());
            writer.write(" [ label=\"" + String.format("%.3f", link.getWeight()) + "\" ];");
            writer.newLine();
        }
    }
}

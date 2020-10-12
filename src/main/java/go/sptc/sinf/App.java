package go.sptc.sinf;

import java.io.IOException;
import go.sptc.sinf.config.Config;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class App {

    public static void main(String[] args) throws IOException, ClassNotFoundException {


        ArgumentParser parser = ArgumentParsers.newFor("IpedExport").build().defaultHelp(true)
                .description("Exports files by category from iped processing results.");
        parser.addArgument("-d", "--dest").help("Destination folder");
        parser.addArgument("-c", "--case").help("Case folder.");
        parser.addArgument("-q", "--query").help("Path to the file that contains the query");
        parser.addArgument("-f", "--logsfolder").setDefault("./ipedexport_logs").help("Folder to save logs");
        parser.addArgument("--hash").help("Hash algorithm").choices("NULL", "SHA-512", "SHA1", "MD5").setDefault("NULL").help("Hash type");
        parser.addArgument("-l", "--limit").type(Integer.class).setDefault(-1).help(
                "Limit. Max number of files to export. Case negative all files that pass the filter specified at the query file will be exported");
        parser.addArgument("-v", "--verbose").type(Boolean.class).help("Print debug information.").action(Arguments.storeTrue());

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        Config.init(ns);
        Copier copier = new Copier();
        copier.run();
    }
}

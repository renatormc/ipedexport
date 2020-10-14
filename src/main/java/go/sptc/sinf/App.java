package go.sptc.sinf;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import go.sptc.sinf.config.Config;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class App {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        ArgumentParser parser = ArgumentParsers.newFor("IpedExport").build().defaultHelp(true)
                .description("Exports files from IPED indexing.");
        parser.addArgument("action").choices("init", "run").help("Action to be executed");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        switch (ns.getString("action")) {
            case "init":
                Config.initDir();
                System.out.println(
                        "Edite os arquivos \".ipedexport/config.ini\" e \".ipedexport/query.txt\" antes de executar a exportação.");
                break;
            case "run":
        
                Config.load();
                Copier copier = new Copier();
                copier.run();
                break;

        }

    }

  
}

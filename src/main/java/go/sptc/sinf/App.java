package go.sptc.sinf;

import java.io.IOException;
import go.sptc.sinf.config.Config;

public class App {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        // Namespace ns = null;
        // try {
        // ns = parser.parseArgs(args);
        // } catch (ArgumentParserException e) {
        // parser.handleError(e);
        // System.exit(1);
        // }

        switch (args[0]) {
            case "init":
                Config.initDir();
                break;
            case "run":
                Config.load();
                Copier copier = new Copier();
                copier.run();
                break;

        }

    }
}

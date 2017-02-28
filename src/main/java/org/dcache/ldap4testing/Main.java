package org.dcache.ldap4testing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Main {

    @Option(name = "-p", usage = "port number to bind", metaVar = "<port>")
    private int port = 1636;

    @Option(name = "-f", usage = "inilial ldif file", metaVar = "<file>")
    private String ldif;

    public void doMain(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);

            EmbeddedServer srv;
            if (ldif != null) {
                srv = new EmbeddedServer(port, new FileInputStream(ldif));
            } else {
                srv = new EmbeddedServer(port);
            }

            srv.start();

            System.out.println("Press ENTER to stop the server ...");
            System.in.read();

            srv.stop();
            System.out.println();
            System.out.println("Stopped ...");
            System.out.println();
            System.exit(0);

        } catch (CmdLineException e) {
            System.err.println();
            System.err.println("Error: " + e.getMessage());
            System.err.println();
            System.err.println("Usage: java " + Main.class.getName() + " [options] ");
            System.err.println();
            parser.printUsage(System.err);
            System.err.println();
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println();
            System.err.println("Can't read ldif file: " + e.getMessage());
            System.err.println();
            System.exit(1);
        } catch (IOException e) {
            System.err.println();
            System.err.println("Failed to start server: " + e.getMessage());
            System.exit(2);
            System.err.println();
        }

    }

    public static void main(String[] args) {
        new Main().doMain(args);
    }
}

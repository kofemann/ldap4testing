package org.dcache.ldap4testing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractService;
import java.io.IOException;

import org.forgerock.opendj.ldap.*;
import org.forgerock.opendj.ldif.LDIFEntryReader;

/**
 *
 */
public class EmbeddedServer extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedServer.class);

    private final int port;
    private LDAPListener listener;

    public EmbeddedServer(int port) {
        this.port = port;
    }

    @Override
    protected void doStart() {
        LOGGER.debug("Starting new embedded LDAP server on port; {}", port);
        final RequestHandler<RequestContext> requestHandler = new MemoryBackend();

        final ServerConnectionFactory<LDAPClientContext, Integer> connectionFactory = Connections.newServerConnectionFactory(requestHandler);
        try {
            listener = new LDAPListener(port, connectionFactory);
            notifyStarted();
        } catch (IOException e) {
            notifyFailed(e);
        }

    }

    @Override
    protected void doStop() {
        LOGGER.debug("Starting new embedded LDAP server.");
        listener.close();
        notifyStopped();
    }

}

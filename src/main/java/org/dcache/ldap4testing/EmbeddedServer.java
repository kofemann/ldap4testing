package org.dcache.ldap4testing;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractService;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

import org.forgerock.opendj.ldap.*;
import org.forgerock.opendj.ldif.LDIFEntryReader;

/**
 *
 */
public class EmbeddedServer extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedServer.class);

    /**
     * TCP port number to listen.
     */
    private final int port;

    /**
     * LDAP server listener.
     */
    private LDAPListener listener;

    /**
     * Reader which provides initial data in LDIF format.
     */
    private final InputStream ldifSource;

    public EmbeddedServer(int port) {
        this(port, new EmptyStream());
    }

    public EmbeddedServer(int port, InputStream ldifSource) {
        this.port = port;
        this.ldifSource = ldifSource;
    }

    @Override
    protected void doStart() {
        LOGGER.debug("Starting new embedded LDAP server on port; {}", port);
        try {
            final LDIFEntryReader entryReader = new LDIFEntryReader(ldifSource);
            final RequestHandler<RequestContext> requestHandler = new MemoryBackend(entryReader);
            final ServerConnectionFactory<LDAPClientContext, Integer> connectionFactory = Connections.newServerConnectionFactory(requestHandler);

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

    /**
     * An implementation of {@link InputStream}, which is always empty.
     */
    private static class EmptyStream extends InputStream {

        @Override
        public void close() throws IOException {
            // NOP;
        }

        @Override
        public int read() throws IOException {
            return -1;
        }
    }

    public void start() throws IOException {
        this.startAsync().awaitRunning();
        if (state() == State.FAILED) {
            Throwable t = this.failureCause();
            Throwables.throwIfInstanceOf(t, IOException.class);
            Throwables.throwIfUnchecked(t);
        }
    }

    public void stop() {
        this.stopAsync().awaitTerminated();
    }

    public InetSocketAddress getSocketAddress() {
        return (InetSocketAddress)listener.getSocketAddress();
    }

    /**
     * Get {@link InitialDirContext} to connected to this LDAP server
     * @param dn LDAP user used to connect to the server
     * @param pass user's password
     * @return directory context
     * @throws NamingException
     */
    public InitialDirContext getDirContext(String dn, String pass) throws NamingException {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, String.format("ldap://localhost:%d/",
                getSocketAddress().getPort()));
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, dn);
        env.put(Context.SECURITY_CREDENTIALS, pass);
        return new InitialDirContext(env);
    }
}

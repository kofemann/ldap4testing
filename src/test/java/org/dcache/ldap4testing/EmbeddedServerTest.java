package org.dcache.ldap4testing;

import com.google.common.util.concurrent.Service;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class EmbeddedServerTest {

    EmbeddedServer server;

    @After
    public void shutdown() {
        if (server != null && server.state() != Service.State.TERMINATED) {
            server.stopAsync().awaitTerminated();
        }
    }

    @Test
    public void testStaringAndStopServer() {
        server = new EmbeddedServer(0);

        server.startAsync().awaitRunning();
        assertEquals("Embedded LDAP server failed to start", Service.State.RUNNING, server.state());

        server.stopAsync().awaitTerminated();
        assertEquals("Embedded LDAP server failed to stop", Service.State.TERMINATED, server.state());

    }

}

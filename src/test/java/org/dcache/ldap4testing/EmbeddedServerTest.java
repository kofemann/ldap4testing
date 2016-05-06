package org.dcache.ldap4testing;

import com.google.common.util.concurrent.Service;
import java.io.InputStream;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class EmbeddedServerTest {

    EmbeddedServer server;

    @After
    public void shutdown() {
        if (server != null && server.state() != Service.State.TERMINATED) {
            server.stopAsync().awaitTerminated();
            assertNotFailed(server);
        }
    }

    @Test
    public void testStaringAndStopServer() {
        server = new EmbeddedServer(0);

        server.startAsync().awaitRunning();
        assertNotFailed(server);
        assertEquals("Embedded LDAP server failed to start", Service.State.RUNNING, server.state());

        server.stopAsync().awaitTerminated();
        assertNotFailed(server);
        assertEquals("Embedded LDAP server failed to stop", Service.State.TERMINATED, server.state());
    }


    @Test
    public void testServerWithLdiffFile() {
        InputStream initialLdif = ClassLoader.getSystemResourceAsStream("org/dcache/ldap4testing/init.ldif");
        server = new EmbeddedServer(0, initialLdif);

        server.startAsync().awaitRunning();
        assertNotFailed(server);
        assertEquals("Embedded LDAP server failed to start", Service.State.RUNNING, server.state());
    }

    private void assertNotFailed(EmbeddedServer server) {
        if (server.state() == Service.State.FAILED) {
            fail(server.failureCause().toString());
        }
    }
}

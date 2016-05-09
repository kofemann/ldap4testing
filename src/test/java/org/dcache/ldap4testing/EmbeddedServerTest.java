package org.dcache.ldap4testing;

import com.google.common.util.concurrent.Service;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.naming.Context;
import org.junit.After;
import org.junit.Test;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.forgerock.opendj.ldap.ErrorResultException;

import static org.junit.Assert.*;

public class EmbeddedServerTest {

    EmbeddedServer server;

    @After
    public void shutdown() {
        if (server != null && server.state() != Service.State.TERMINATED) {
            server.stop();
        }
    }

    @Test
    public void testStaringAndStopServer() throws IOException {
        server = new EmbeddedServer(0);

        server.start();
        assertEquals("Embedded LDAP server failed to start", Service.State.RUNNING, server.state());

        server.stop();
        assertEquals("Embedded LDAP server failed to stop", Service.State.TERMINATED, server.state());
    }

    @Test
    public void testServerWithLdiffFile() throws IOException {
        InputStream initialLdif = ClassLoader.getSystemResourceAsStream("org/dcache/ldap4testing/init.ldif");
        server = new EmbeddedServer(0, initialLdif);

        server.start();
        assertEquals("Embedded LDAP server failed to start", Service.State.RUNNING, server.state());
    }

    @Test
    public void testQueryAfterInit() throws NamingException, ErrorResultException, IOException {
        InputStream initialLdif = ClassLoader.getSystemResourceAsStream("org/dcache/ldap4testing/init.ldif");
        server = new EmbeddedServer(0, initialLdif);
        server.start();

        LdapClient c = new LdapClient(server.getSocketAddress().getPort());
        String v = c.search("ou=people,o=dcache,c=org", "(uid=kermit)", "uidNumber");
        assertEquals("Unexpected result:", "1000", v);
    }

    private static class LdapClient {

        private final InitialDirContext _ctx;

        LdapClient(int port) throws NamingException {
            Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, String.format("ldap://localhost:%d/", port));
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "uid=kermit,ou=people,o=dcache,c=org");
            env.put(Context.SECURITY_CREDENTIALS, "kermitTheFrog");
            _ctx = new InitialDirContext(env);
        }

        String search(String tree, String filter, String attr) throws NamingException {
            NamingEnumeration<SearchResult> ne = _ctx.search(tree, filter,
                    getSimplSearchControls(attr));
            return String.valueOf(ne.next().getAttributes().get(attr).get());
        }

        private SearchControls getSimplSearchControls(String... attr) {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String attrList[] = attr;
            constraints.setReturningAttributes(attrList);
            return constraints;
        }

    }

}

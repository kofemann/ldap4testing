Embedded LDAP server for unit testing
======================================

Notice
-------
Due to limitation of backend server, client must always bind with username and
password.

Example
-------
```java
import org.dcache.ldap4testing.EmbeddedServer;

import org.junit.After;
import org.junit.Test;

public class TestWithLdap {

    private EmbeddedServer server;
    private InitialDirContext ctx;

   @Before
    public void setUp() {
        InputStream initialLdif = ClassLoader.getSystemResourceAsStream("org/dcache/ldap4testing/init.ldif");
        server = new EmbeddedServer(1369, initialLdif);
        server.startAsync().awaitRunning();

            Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, String.format("ldap://localhost:%d/", port));
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "uid=kermit,ou=people,o=dcache,c=org");
            env.put(Context.SECURITY_CREDENTIALS, "kermitTheFrog");
            ctx = new InitialDirContext(env);
    }

    @After
    public void tearDown() {
         server.stopAsync().awaitTerminated();
    }

    @Test
    public void testWithLdapAccess() {

        // run your tests
       ctx.search(.....)

    }

}
```

Check the [example ldif file](src/test/resources/org/dcache/ldap4testing/init.ldif) for details
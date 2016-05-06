Embedded LDAP server for unit testing
======================================


Example
-------
```java
import org.dcache.ldap4testing.EmbeddedServer;

import org.junit.After;
import org.junit.Test;

public class TestWithLdap {

   @Before
   public void setUp() {
        InputStream initialLdif = ClassLoader.getSystemResourceAsStream("org/dcache/ldap4testing/init.ldif");
        server = new EmbeddedServer(1369, initialLdif);

        server.startAsync().awaitRunning();
   }

    @After
    public void tearDown() {
         server.stopAsync().awaitTerminated();
    }

    @Test
    public void testWithLdapAccess() {

        // run your tests

    }

}
```
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
        InputStream initialLdif =
                ClassLoader.getSystemResourceAsStream("org/dcache/ldap4testing/init.ldif");
        server = new EmbeddedServer(1369, initialLdif);
        server.start();

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
         server.stop();
    }

    @Test
    public void testWithLdapAccess() {

        // run your tests
       ctx.search(.....)

    }

}
```

Check the [example ldif file](src/test/resources/org/dcache/ldap4testing/init.ldif) for details.

How to contribute
=================

**ldap4testing** uses the linux kernel model of using git not only a source
repository, but also as a way to track contributions and copyrights.

Each submitted patch must have a "Signed-off-by" line.  Patches without
this line will not be accepted.

The sign-off is a simple line at the end of the explanation for the
patch, which certifies that you wrote it or otherwise have the right to
pass it on as an open-source patch.  The rules are pretty simple: if you
can certify the below:
```

    Developer's Certificate of Origin 1.1

    By making a contribution to this project, I certify that:

    (a) The contribution was created in whole or in part by me and I
        have the right to submit it under the open source license
        indicated in the file; or

    (b) The contribution is based upon previous work that, to the best
        of my knowledge, is covered under an appropriate open source
        license and I have the right under that license to submit that
        work with modifications, whether created in whole or in part
        by me, under the same open source license (unless I am
        permitted to submit under a different license), as indicated
        in the file; or

    (c) The contribution was provided directly to me by some other
        person who certified (a), (b) or (c) and I have not modified
        it.

    (d) I understand and agree that this project and the contribution
        are public and that a record of the contribution (including all
        personal information I submit with it, including my sign-off) is
        maintained indefinitely and may be redistributed consistent with
        this project or the open source license(s) involved.

```
then you just add a line saying ( git commit -s )

	Signed-off-by: Random J Developer <random@developer.example.org>

using your real name (sorry, no pseudonyms or anonymous contributions.)

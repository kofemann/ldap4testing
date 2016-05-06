Embedded LDAP server for unit testing
======================================


Example
-------
```java
    @Test
    public void testServerWithLdiffFile() {
        InputStream initialLdif = ClassLoader.getSystemResourceAsStream("org/dcache/ldap4testing/init.ldif");
        server = new EmbeddedServer(1369, initialLdif);

        server.startAsync().awaitRunning();

        // run your tests

    }
```
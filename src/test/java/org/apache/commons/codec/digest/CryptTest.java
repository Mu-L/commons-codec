/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.codec.digest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CryptTest {

    // Allow CLI testing
    // CLASSPATH=target/classes:target/test-classes/ java org.apache.commons.codec.digest.CryptTest
    public static void main(final String[] args) {
        final String hash;
        switch (args.length) {
            case 1:
                hash = Crypt.crypt(args[0]);
                System.out.println(hash.length() + ": " + hash);
                break;
            case 2:
                hash = Crypt.crypt(args[0], args[1]);
                System.out.println(hash.length() + "; " + hash);
                break;
            default:
                System.out.println("Enter key [salt (remember to quote this!)]");
                break;
        }
    }

    // Helper method
    private void startsWith(final String string, final String prefix) {
        assertTrue(string.startsWith(prefix), string + " should start with " + prefix);
    }

    @Test
    void testBadSalt() {
        // No salt
        assertThrowsExactly(IllegalArgumentException.class, () -> Crypt.crypt("secret", "$1$"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Crypt.crypt("secret", "$5$"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Crypt.crypt("secret", "$6$"));
        // wrong char
        assertThrowsExactly(IllegalArgumentException.class, () -> Crypt.crypt("secret", "$1$%"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Crypt.crypt("secret", "$5$!"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Crypt.crypt("secret", "$6$_"));
    }

    @Test
    void testBadType() {
        assertThrowsExactly(IllegalArgumentException.class, () -> Crypt.crypt("secret", "$2$xxxx"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Crypt.crypt("secret", "$3$xxxx"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Crypt.crypt("secret", "$4$"));
    }

    @Test
    void testCrypt() {
        assertNotNull(new Crypt()); // just for Test Coverage
    }
    @Test
    void testCryptWithBytes() {
        final byte[] keyBytes = { 'b', 'y', 't', 'e' };
        final String hash = Crypt.crypt(keyBytes);
        assertEquals(hash, Crypt.crypt("byte", hash));
    }

    /**
     * An empty string as salt is invalid.
     *
     * The C and Perl implementations return an empty string, PHP threads it
     * as NULL. Our implementation should throw an Exception as any resulting
     * hash would not be verifiable with other implementations of crypt().
     */
    @Test
    void testCryptWithEmptySalt() {
        assertThrowsExactly(IllegalArgumentException.class, () -> Crypt.crypt("secret", ""));
    }
    @Test
    void testDefaultCryptVariant() {
        // If salt is null or completely omitted, a random "$6$" is used.
        assertTrue(Crypt.crypt("secret").startsWith("$6$"));
        assertTrue(Crypt.crypt("secret", null).startsWith("$6$"));
    }
    @Test
    void testSamples() { // From Javadoc
        assertEquals("$1$xxxx$aMkevjfEIpa35Bh3G4bAc.", Crypt.crypt("secret", "$1$xxxx"));
        assertEquals("xxWAum7tHdIUw", Crypt.crypt("secret", "xx"));
    }
    @Test
    void testStored() { // From Javadoc
        assertEquals("$1$xxxx$aMkevjfEIpa35Bh3G4bAc.", Crypt.crypt("secret", "$1$xxxx$aMkevjfEIpa35Bh3G4bAc."));
        assertEquals("xxWAum7tHdIUw", Crypt.crypt("secret", "xxWAum7tHdIUw"));
    }

    @Test
    void testType() {
        startsWith(Crypt.crypt("secret", "xxxx"), "xx");
        startsWith(Crypt.crypt("secret", "$1$xxxx"), "$1$xxxx$");
        startsWith(Crypt.crypt("secret", "$5$xxxx"), "$5$xxxx$");
        startsWith(Crypt.crypt("secret", "$6$xxxx"), "$6$xxxx$");
    }
}

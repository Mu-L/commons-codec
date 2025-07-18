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

package org.apache.commons.codec.binary;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.commons.codec.CodecPolicy;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Base64OutputStream}.
 */
class Base64OutputStreamTest {

    private static final byte[] CR_LF = {(byte) '\r', (byte) '\n'};

    private static final byte[] LF = {(byte) '\n'};

    private static final String STRING_FIXTURE = "Hello World";

    private void testBase64EmptyOutputStream(final int chunkSize) throws Exception {
        final byte[] emptyEncoded = {};
        final byte[] emptyDecoded = {};
        testByteByByte(emptyEncoded, emptyDecoded, chunkSize, CR_LF);
        testByChunk(emptyEncoded, emptyDecoded, chunkSize, CR_LF);
    }

    /**
     * Test the Base64OutputStream implementation against empty input.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    void testBase64EmptyOutputStreamMimeChunkSize() throws Exception {
        testBase64EmptyOutputStream(BaseNCodec.MIME_CHUNK_SIZE);
    }

    /**
     * Test the Base64OutputStream implementation against empty input.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    void testBase64EmptyOutputStreamPemChunkSize() throws Exception {
        testBase64EmptyOutputStream(BaseNCodec.PEM_CHUNK_SIZE);
    }

    /**
     * Test the Base64OutputStream implementation
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    void testBase64OutputStreamByChunk() throws Exception {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8("SGVsbG8gV29ybGQ=\r\n");
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        testByChunk(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CR_LF);

        // Single Byte test.
        encoded = StringUtils.getBytesUtf8("AA==\r\n");
        decoded = new byte[]{(byte) 0};
        testByChunk(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CR_LF);

        // OpenSSL interop test.
        encoded = StringUtils.getBytesUtf8(Base64TestData.ENCODED_64_CHARS_PER_LINE);
        decoded = BaseNTestData.DECODED;
        testByChunk(encoded, decoded, BaseNCodec.PEM_CHUNK_SIZE, LF);

        // Single Line test.
        final String singleLine = Base64TestData.ENCODED_64_CHARS_PER_LINE.replace("\n", "");
        encoded = StringUtils.getBytesUtf8(singleLine);
        decoded = BaseNTestData.DECODED;
        testByChunk(encoded, decoded, 0, LF);

        // test random data of sizes 0 through 150
        final BaseNCodec codec = new Base64(0, null, false);
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByChunk(encoded, decoded, 0, LF);
        }
    }

    /**
     * Test the Base64OutputStream implementation
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    void testBase64OutputStreamByteByByte() throws Exception {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8("SGVsbG8gV29ybGQ=\r\n");
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        testByteByByte(encoded, decoded, 76, CR_LF);

        // Single Byte test.
        encoded = StringUtils.getBytesUtf8("AA==\r\n");
        decoded = new byte[]{(byte) 0};
        testByteByByte(encoded, decoded, 76, CR_LF);

        // OpenSSL interop test.
        encoded = StringUtils.getBytesUtf8(Base64TestData.ENCODED_64_CHARS_PER_LINE);
        decoded = BaseNTestData.DECODED;
        testByteByByte(encoded, decoded, 64, LF);

        // Single Line test.
        final String singleLine = Base64TestData.ENCODED_64_CHARS_PER_LINE.replace("\n", "");
        encoded = StringUtils.getBytesUtf8(singleLine);
        decoded = BaseNTestData.DECODED;
        testByteByByte(encoded, decoded, 0, LF);

        // test random data of sizes 0 through 150
        final BaseNCodec codec = new Base64(0, null, false);
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByteByByte(encoded, decoded, 0, LF);
        }
    }

    /**
     * Test method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]-->
     * encoded 3. decoded ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base64OutputStream wraps itself in encode and decode
     * mode over and over again.
     *
     * @param encoded
     *            base64 encoded data
     * @param decoded
     *            the data from above, but decoded
     * @param chunkSize
     *            chunk size (line-length) of the base64 encoded data.
     * @param separator
     *            Line separator in the base64 encoded data.
     * @throws Exception
     *             Usually signifies a bug in the Base64 commons-codec implementation.
     */
    private void testByChunk(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {

        // Start with encode.
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (OutputStream out = new Base64OutputStream(byteOut, true, chunkSize, separator)) {
            out.write(decoded);
        }
        byte[] output = byteOut.toByteArray();
        assertArrayEquals(encoded, output, "Streaming chunked base64 encode");

        // Now let's try to decode.
        byteOut = new ByteArrayOutputStream();
        try (OutputStream out = new Base64OutputStream(byteOut, false)) {
            out.write(encoded);
        }
        output = byteOut.toByteArray();
        assertArrayEquals(decoded, output, "Streaming chunked base64 decode");

        // I always wanted to do this! (wrap encoder with decoder etc.).
        byteOut = new ByteArrayOutputStream();
        OutputStream out = byteOut;
        for (int i = 0; i < 10; i++) {
            out = new Base64OutputStream(out, false);
            out = new Base64OutputStream(out, true, chunkSize, separator);
        }
        out.write(decoded);
        out.close();
        output = byteOut.toByteArray();

        assertArrayEquals(decoded, output, "Streaming chunked base64 wrap-wrap-wrap!");
    }

    /**
     * Test method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]-->
     * encoded 3. decoded ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base64OutputStream wraps itself in encode and decode
     * mode over and over again.
     *
     * @param encoded
     *            base64 encoded data
     * @param decoded
     *            the data from above, but decoded
     * @param chunkSize
     *            chunk size (line-length) of the base64 encoded data.
     * @param separator
     *            Line separator in the base64 encoded data.
     * @throws Exception
     *             Usually signifies a bug in the Base64 commons-codec implementation.
     */
    private void testByteByByte(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {

        // Start with encode.
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (OutputStream out = new Base64OutputStream(byteOut, true, chunkSize, separator)) {
            for (final byte element : decoded) {
                out.write(element);
            }
        }
        byte[] output = byteOut.toByteArray();
        assertArrayEquals(encoded, output, "Streaming byte-by-byte base64 encode");

        // Now let's try to decode.
        byteOut = new ByteArrayOutputStream();
        try (OutputStream out = new Base64OutputStream(byteOut, false)) {
            for (final byte element : encoded) {
                out.write(element);
            }
        }
        output = byteOut.toByteArray();
        assertArrayEquals(decoded, output, "Streaming byte-by-byte base64 decode");

        // Now let's try to decode with tonnes of flushes.
        byteOut = new ByteArrayOutputStream();
        try (OutputStream out = new Base64OutputStream(byteOut, false)) {
            for (final byte element : encoded) {
                out.write(element);
                out.flush();
            }
        }
        output = byteOut.toByteArray();
        assertArrayEquals(decoded, output, "Streaming byte-by-byte flush() base64 decode");

        // I always wanted to do this! (wrap encoder with decoder etc.).
        byteOut = new ByteArrayOutputStream();
        OutputStream out = byteOut;
        for (int i = 0; i < 10; i++) {
            out = new Base64OutputStream(out, false);
            out = new Base64OutputStream(out, true, chunkSize, separator);
        }
        for (final byte element : decoded) {
            out.write(element);
        }
        out.close();
        output = byteOut.toByteArray();

        assertArrayEquals(decoded, output, "Streaming byte-by-byte base64 wrap-wrap-wrap!");
    }

    /**
     * Test the Base64OutputStream implementation against the special NPE inducing input
     * identified in the CODEC-98 bug.
     *
     * @throws Exception for some failure scenarios.
     */
    @Test
    void testCodec98NPE() throws Exception {
        final byte[] codec98 = StringUtils.getBytesUtf8(Base64TestData.CODEC_98_NPE);
        final byte[] codec98_1024 = new byte[1024];
        System.arraycopy(codec98, 0, codec98_1024, 0, codec98.length);
        final ByteArrayOutputStream data = new ByteArrayOutputStream(1024);
        try (Base64OutputStream stream = new Base64OutputStream(data, false)) {
            stream.write(codec98_1024, 0, 1024);
        }

        final byte[] decodedBytes = data.toByteArray();
        final String decoded = StringUtils.newStringUtf8(decodedBytes);
        assertEquals(Base64TestData.CODEC_98_NPE_DECODED, decoded, "codec-98 NPE Base64OutputStream");
    }

    /**
     * Test strict decoding.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    void testStrictDecoding() throws Exception {
        for (final String impossibleStr : Base64Test.BASE64_IMPOSSIBLE_CASES) {
            final byte[] impossibleEncoded = StringUtils.getBytesUtf8(impossibleStr);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try (Base64OutputStream out = new Base64OutputStream(bout, false)) {
                // Default is lenient decoding; it should not throw
                assertFalse(out.isStrictDecoding());
                out.write(impossibleEncoded);
            }
            assertTrue(bout.size() > 0);

            // Strict decoding should throw
            bout = new ByteArrayOutputStream();
            final Base64OutputStream out = new Base64OutputStream(bout, false, 0, null, CodecPolicy.STRICT);
            // May throw on write or on close depending on the position of the
            // impossible last character in the output block size
            assertThrows(IllegalArgumentException.class, () -> {
                out.write(impossibleEncoded);
                out.close();
            });
        }
    }

    /**
     * Tests Base64OutputStream.write for expected IndexOutOfBoundsException conditions.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    void testWriteOutOfBounds() throws Exception {
        final byte[] buf = new byte[1024];
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (Base64OutputStream out = new Base64OutputStream(bout)) {
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, -1, 1), "Base64OutputStream.write(buf, -1, 1)");
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, 1, -1), "Base64OutputStream.write(buf, 1, -1)");
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, buf.length + 1, 0), "Base64OutputStream.write(buf, buf.length + 1, 0)");
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, buf.length - 1, 2), "Base64OutputStream.write(buf, buf.length - 1, 2)");
        }
    }

    /**
     * Tests Base64OutputStream.write(null).
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    void testWriteToNullCoverage() throws Exception {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (Base64OutputStream out = new Base64OutputStream(bout)) {
            assertThrows(NullPointerException.class, () -> out.write(null, 0, 0));
        }
    }
}

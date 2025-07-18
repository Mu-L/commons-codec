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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.CharEncoding;

/**
 * Converts String to and from bytes using the encodings required by the Java specification. These encodings are
 * specified in standard {@link Charset}.
 *
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @see CharEncoding
 * @see Charset
 * @see StandardCharsets
 * @since 1.4
 */
public class StringUtils {

    /**
     * <p>
     * Compares two CharSequences, returning {@code true} if they represent equal sequences of characters.
     * </p>
     *
     * <p>
     * {@code null}s are handled without exceptions. Two {@code null} references are considered to be equal.
     * The comparison is case sensitive.
     * </p>
     *
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     *
     * <p>
     * Copied from Apache Commons Lang r1583482 on April 10, 2014 (day of 3.3.2 release).
     * </p>
     *
     * @see Object#equals(Object)
     * @param cs1
     *            the first CharSequence, may be {@code null}
     * @param cs2
     *            the second CharSequence, may be {@code null}
     * @return {@code true} if the CharSequences are equal (case-sensitive), or both {@code null}
     * @since 1.10
     */
    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
        }
        return cs1.length() == cs2.length() && CharSequenceUtils.regionMatches(cs1, false, 0, cs2, 0, cs1.length());
    }

    /**
     * Calls {@link String#getBytes(Charset)}
     *
     * @param string
     *            The string to encode (if null, return null).
     * @param charset
     *            The {@link Charset} to encode the {@code String}
     * @return the encoded bytes
     */
    private static ByteBuffer getByteBuffer(final String string, final Charset charset) {
        if (string == null) {
            return null;
        }
        return ByteBuffer.wrap(string.getBytes(charset));
    }

    /**
     * Encodes the given string into a byte buffer using the UTF-8 charset, storing the result into a new byte
     * array.
     *
     * @param string
     *            the String to encode, may be {@code null}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws NullPointerException
     *             Thrown if {@link StandardCharsets#UTF_8} is not initialized, which should never happen since it is
     *             required by the Java platform specification.
     * @see Charset
     * @see #getBytesUnchecked(String, String)
     * @since 1.11
     */
    public static ByteBuffer getByteBufferUtf8(final String string) {
        return getByteBuffer(string, StandardCharsets.UTF_8);
    }

    /**
     * Calls {@link String#getBytes(Charset)}
     *
     * @param string
     *            The string to encode (if null, return null).
     * @param charset
     *            The {@link Charset} to encode the {@code String}
     * @return the encoded bytes
     */
    private static byte[] getBytes(final String string, final Charset charset) {
        return string == null ? null : string.getBytes(charset);
    }

    /**
     * Encodes the given string into a sequence of bytes using the ISO-8859-1 charset, storing the result into a new
     * byte array.
     *
     * @param string
     *            the String to encode, may be {@code null}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws NullPointerException
     *             Thrown if {@link StandardCharsets#ISO_8859_1} is not initialized, which should never happen
     *             since it is required by the Java platform specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     * @see Charset
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesIso8859_1(final String string) {
        return getBytes(string, StandardCharsets.ISO_8859_1);
    }

    /**
     * Encodes the given string into a sequence of bytes using the named charset, storing the result into a new byte
     * array.
     * <p>
     * This method catches {@link UnsupportedEncodingException} and rethrows it as {@link IllegalStateException}, which
     * should never happen for a required charset name. Use this method when the encoding is required to be in the JRE.
     * </p>
     *
     * @param string
     *            the String to encode, may be {@code null}
     * @param charsetName
     *            The name of a required {@link java.nio.charset.Charset}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen for a
     *             required charset name.
     * @see CharEncoding
     * @see String#getBytes(String)
     */
    public static byte[] getBytesUnchecked(final String string, final String charsetName) {
        if (string == null) {
            return null;
        }
        try {
            return string.getBytes(charsetName);
        } catch (final UnsupportedEncodingException e) {
            throw newIllegalStateException(charsetName, e);
        }
    }

    /**
     * Encodes the given string into a sequence of bytes using the US-ASCII charset, storing the result into a new byte
     * array.
     *
     * @param string
     *            the String to encode, may be {@code null}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws NullPointerException
     *             Thrown if {@link StandardCharsets#US_ASCII} is not initialized, which should never happen since it is
     *             required by the Java platform specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     * @see Charset
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUsAscii(final String string) {
        return getBytes(string, StandardCharsets.US_ASCII);
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-16 charset, storing the result into a new byte
     * array.
     *
     * @param string
     *            the String to encode, may be {@code null}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws NullPointerException
     *             Thrown if {@link StandardCharsets#UTF_16} is not initialized, which should never happen since it is
     *             required by the Java platform specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     * @see Charset
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUtf16(final String string) {
        return getBytes(string, StandardCharsets.UTF_16);
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-16BE charset, storing the result into a new byte
     * array.
     *
     * @param string
     *            the String to encode, may be {@code null}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws NullPointerException
     *             Thrown if {@link StandardCharsets#UTF_16BE} is not initialized, which should never happen since it is
     *             required by the Java platform specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     * @see Charset
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUtf16Be(final String string) {
        return getBytes(string, StandardCharsets.UTF_16BE);
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-16LE charset, storing the result into a new byte
     * array.
     *
     * @param string
     *            the String to encode, may be {@code null}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws NullPointerException
     *             Thrown if {@link StandardCharsets#UTF_16LE} is not initialized, which should never happen since it is
     *             required by the Java platform specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     * @see Charset
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUtf16Le(final String string) {
        return getBytes(string, StandardCharsets.UTF_16LE);
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-8 charset, storing the result into a new byte
     * array.
     *
     * @param string
     *            the String to encode, may be {@code null}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws NullPointerException
     *             Thrown if {@link StandardCharsets#UTF_8} is not initialized, which should never happen since it is
     *             required by the Java platform specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     * @see Charset
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUtf8(final String string) {
        return getBytes(string, StandardCharsets.UTF_8);
    }

    private static IllegalStateException newIllegalStateException(final String charsetName, final UnsupportedEncodingException e) {
        return new IllegalStateException(charsetName + ": " + e);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the given charset.
     *
     * @param bytes
     *            The bytes to be decoded into characters
     * @param charset
     *            The {@link Charset} to encode the {@code String}; not {@code null}
     * @return A new {@code String} decoded from the specified array of bytes using the given charset,
     *         or {@code null} if the input byte array was {@code null}.
     * @throws NullPointerException
     *             Thrown if charset is {@code null}
     */
    private static String newString(final byte[] bytes, final Charset charset) {
        return bytes == null ? null : new String(bytes, charset);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the given charset.
     * <p>
     * This method catches {@link UnsupportedEncodingException} and re-throws it as {@link IllegalStateException}, which
     * should never happen for a required charset name. Use this method when the encoding is required to be in the JRE.
     * </p>
     *
     * @param bytes
     *            The bytes to be decoded into characters, may be {@code null}
     * @param charsetName
     *            The name of a required {@link java.nio.charset.Charset}
     * @return A new {@code String} decoded from the specified array of bytes using the given charset,
     *         or {@code null} if the input byte array was {@code null}.
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen for a
     *             required charset name.
     * @see CharEncoding
     * @see String#String(byte[], String)
     */
    public static String newString(final byte[] bytes, final String charsetName) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, charsetName);
        } catch (final UnsupportedEncodingException e) {
            throw newIllegalStateException(charsetName, e);
        }
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the ISO-8859-1 charset.
     *
     * @param bytes
     *            The bytes to be decoded into characters, may be {@code null}
     * @return A new {@code String} decoded from the specified array of bytes using the ISO-8859-1 charset, or
     *         {@code null} if the input byte array was {@code null}.
     * @throws NullPointerException
     *             Thrown if {@link StandardCharsets#ISO_8859_1} is not initialized, which should never happen
     *             since it is required by the Java platform specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static String newStringIso8859_1(final byte[] bytes) {
        return newString(bytes, StandardCharsets.ISO_8859_1);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the US-ASCII charset.
     *
     * @param bytes
     *            The bytes to be decoded into characters
     * @return A new {@code String} decoded from the specified array of bytes using the US-ASCII charset,
     *         or {@code null} if the input byte array was {@code null}.
     * @throws NullPointerException
     *             Thrown if {@link StandardCharsets#US_ASCII} is not initialized, which should never happen since it is
     *             required by the Java platform specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static String newStringUsAscii(final byte[] bytes) {
        return newString(bytes, StandardCharsets.US_ASCII);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the UTF-16 charset.
     *
     * @param bytes
     *            The bytes to be decoded into characters
     * @return A new {@code String} decoded from the specified array of bytes using the UTF-16 charset
     *         or {@code null} if the input byte array was {@code null}.
     * @throws NullPointerException
     *             Thrown if {@link StandardCharsets#UTF_16} is not initialized, which should never happen since it is
     *             required by the Java platform specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static String newStringUtf16(final byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_16);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the UTF-16BE charset.
     *
     * @param bytes
     *            The bytes to be decoded into characters
     * @return A new {@code String} decoded from the specified array of bytes using the UTF-16BE charset,
     *         or {@code null} if the input byte array was {@code null}.
     * @throws NullPointerException
     *             Thrown if {@link StandardCharsets#UTF_16BE} is not initialized, which should never happen since it is
     *             required by the Java platform specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static String newStringUtf16Be(final byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_16BE);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the UTF-16LE charset.
     *
     * @param bytes
     *            The bytes to be decoded into characters
     * @return A new {@code String} decoded from the specified array of bytes using the UTF-16LE charset,
     *         or {@code null} if the input byte array was {@code null}.
     * @throws NullPointerException
     *             Thrown if {@link StandardCharsets#UTF_16LE} is not initialized, which should never happen since it is
     *             required by the Java platform specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static String newStringUtf16Le(final byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_16LE);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the UTF-8 charset.
     *
     * @param bytes
     *            The bytes to be decoded into characters
     * @return A new {@code String} decoded from the specified array of bytes using the UTF-8 charset,
     *         or {@code null} if the input byte array was {@code null}.
     * @throws NullPointerException
     *             Thrown if {@link StandardCharsets#UTF_8} is not initialized, which should never happen since it is
     *             required by the Java platform specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static String newStringUtf8(final byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_8);
    }

    /**
     * TODO Make private in 2.0.
     *
     * @deprecated TODO Make private in 2.0.
     */
    @Deprecated
    public StringUtils() {
        // empty
    }
}

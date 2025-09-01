/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package me.pruivo;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 3)
@Measurement(iterations = 10)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class UUIDGeneratorBenchmark {

    private static final ThreadLocal<Random> ORIGINAL_THREAD_LOCAL = ThreadLocal.withInitial(SecureRandom::new);
    // UUID takes 16 bytes and 36 chars
    private static final ThreadLocal<ThreadLocalData> THREAD_LOCAL = ThreadLocal.withInitial(() -> new ThreadLocalData(new SecureRandom(), new byte[16], new StringBuilder(36)));
    private static final VarHandle LONG = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);

    @Benchmark
    public String stringOriginal() {
        return generateSecureID();
    }

    public static String generateSecureID() {
        StringBuilder builder = new StringBuilder(randomBytesHex(16));
        builder.insert(8, '-');
        builder.insert(13, '-');
        builder.insert(18, '-');
        builder.insert(23, '-');
        return builder.toString();
    }

    public static byte[] randomBytes(int length) {
        if (length < 1) {
            throw new IllegalArgumentException();
        }

        byte[] buf = new byte[length];
        ORIGINAL_THREAD_LOCAL.get().nextBytes(buf);
        return buf;
    }

    public static String randomBytesHex(int length) {
        final StringBuilder sb = new StringBuilder();
        for (byte b : randomBytes(length)) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit((b & 0xF), 16));
        }
        return sb.toString();
    }

    // alternative

    @Benchmark
    public String stringAlternative() {
        var data = THREAD_LOCAL.get();
        var bytes = data.uuidBytes;
        var builder = data.uuidStringBuilder;
        data.random.nextBytes(bytes);

        // reset builder
        builder.setLength(0);
        builder.setLength(36);

        append4(builder, bytes, 0);
        append4(builder, bytes, 2);
        builder.append('-');
        append4(builder, bytes, 4);
        builder.append('-');
        append4(builder, bytes, 6);
        builder.append('-');
        append4(builder, bytes, 8);
        builder.append('-');
        append4(builder, bytes, 10);
        append4(builder, bytes, 12);
        append4(builder, bytes, 14);

        return builder.toString();
    }

    private static void append4(StringBuilder builder, byte[] bytes, int index) {
        builder.append(Character.forDigit((bytes[index] >> 4) & 0xF, 16));
        builder.append(Character.forDigit((bytes[index] & 0xF), 16));
        builder.append(Character.forDigit((bytes[index + 1] >> 4) & 0xF, 16));
        builder.append(Character.forDigit((bytes[index + 1] & 0xF), 16));
    }

    @Benchmark
    public String stringAlternative2() {
        var data = THREAD_LOCAL.get();
        var bytes = data.uuidBytes;
        data.random.nextBytes(bytes);

        long msb = (long) LONG.get(bytes, 0);
        long lsb = (long) LONG.get(bytes, 1);
        return new UUID(msb, lsb).toString();
    }

    @Benchmark
    public UUID uuidOriginal() {
        return UUID.fromString(stringOriginal());
    }

    @Benchmark
    public UUID uuidAlternative() {
        var data = THREAD_LOCAL.get();
        var bytes = data.uuidBytes;
        data.random.nextBytes(bytes);

        long msb = (long) LONG.get(bytes, 0);
        long lsb = (long) LONG.get(bytes, 1);
        return new UUID(msb, lsb);
    }

    // cache UUID secure random temporary objects
    private record ThreadLocalData(SecureRandom random, byte[] uuidBytes, StringBuilder uuidStringBuilder) {};
}

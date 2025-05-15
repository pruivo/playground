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

import org.jgroups.util.ByteArrayDataInputStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Fork(value = 1, jvmArgs = {
        "-Xmx1G",
        "-Xms1G",
        "-XX:+HeapDumpOnOutOfMemoryError",
        "-Xss512k",
})
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JGroupsDataBenchmark {

    @Benchmark
    public byte[] testWriteShort(BenchmarkState state) {
        var out = state.out;
        out.position(0);
        out.writeShort(state.shortValue);
        return out.buffer();
    }

    @Benchmark
    public byte[] testWriteInt(BenchmarkState state) {
        var out = state.out;
        out.position(0);
        out.writeInt(state.intValue);
        return out.buffer();
    }

    @Benchmark
    public byte[] testWriteLong(BenchmarkState state) {
        var out = state.out;
        out.position(0);
        out.writeLong(state.longValue);
        return out.buffer();
    }

    @SuppressWarnings("resource")
    @Benchmark
    public short testReadShort(BenchmarkState state) throws IOException {
        var in = new ByteArrayDataInputStream(state.shortValueBytes);
        return in.readShort();
    }

    @SuppressWarnings("resource")
    @Benchmark
    public int testReadInt(BenchmarkState state) throws IOException {
        var in = new ByteArrayDataInputStream(state.intValueBytes);
        return in.readInt();
    }

    @SuppressWarnings("resource")
    @Benchmark
    public long testReadLong(BenchmarkState state) throws IOException {
        var in = new ByteArrayDataInputStream(state.longValueBytes);
        return in.readLong();
    }

}

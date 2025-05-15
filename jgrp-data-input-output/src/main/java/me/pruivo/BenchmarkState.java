package me.pruivo;

import org.jgroups.util.ByteArrayDataOutputStream;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.Arrays;
import java.util.Random;

@State(Scope.Thread)
public class BenchmarkState {

    @Param("8096")
    long seed;

    // data
    short shortValue;
    int intValue;
    long longValue;

    byte[] shortValueBytes;
    byte[] intValueBytes;
    byte[] longValueBytes;

    final ByteArrayDataOutputStream out = new ByteArrayDataOutputStream(16);

    @Setup
    public void setup() {
        var random = new Random(seed);
        shortValue = (short) random.nextInt(Short.MAX_VALUE);
        intValue = random.nextInt();
        longValue = random.nextLong();

        var out = new ByteArrayDataOutputStream();

        out.writeShort(shortValue);
        shortValueBytes = Arrays.copyOf(out.buffer(), 2);
        out.position(0);

        out.writeInt(intValue);
        intValueBytes = Arrays.copyOf(out.buffer(), 4);
        out.position(0);

        out.writeLong(longValue);
        longValueBytes = Arrays.copyOf(out.buffer(), 8);
        out.position(0);
    }

}

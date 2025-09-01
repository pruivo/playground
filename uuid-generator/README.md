# "Secure" UUID Generator

To compile: `mvn clean package`

To run: `java -jar target/benchmarks.jar -prof gc`

List available profilers: `java -jar target/benchmarks.jar -lprof`

Local Result (AMD Ryzen 5 5600X 6-Core Processor)
```
Benchmark                                                     Mode  Cnt     Score    Error   Units
UUIDGeneratorBenchmark.stringAlternative                      avgt   10   315.654 ± 15.890   ns/op
UUIDGeneratorBenchmark.stringAlternative:gc.alloc.rate        avgt   10   532.270 ± 27.113  MB/sec
UUIDGeneratorBenchmark.stringAlternative:gc.alloc.rate.norm   avgt   10   176.000 ±  0.001    B/op
UUIDGeneratorBenchmark.stringAlternative:gc.count             avgt   10    90.000           counts
UUIDGeneratorBenchmark.stringAlternative:gc.time              avgt   10    50.000               ms
UUIDGeneratorBenchmark.stringAlternative2                     avgt   10   186.370 ±  9.619   ns/op
UUIDGeneratorBenchmark.stringAlternative2:gc.alloc.rate       avgt   10   737.627 ± 38.210  MB/sec
UUIDGeneratorBenchmark.stringAlternative2:gc.alloc.rate.norm  avgt   10   144.000 ±  0.001    B/op
UUIDGeneratorBenchmark.stringAlternative2:gc.count            avgt   10   124.000           counts
UUIDGeneratorBenchmark.stringAlternative2:gc.time             avgt   10    57.000               ms
UUIDGeneratorBenchmark.stringOriginal                         avgt   10   328.219 ± 11.239   ns/op
UUIDGeneratorBenchmark.stringOriginal:gc.alloc.rate           avgt   10  1092.995 ± 37.688  MB/sec
UUIDGeneratorBenchmark.stringOriginal:gc.alloc.rate.norm      avgt   10   376.000 ±  0.001    B/op
UUIDGeneratorBenchmark.stringOriginal:gc.count                avgt   10   185.000           counts
UUIDGeneratorBenchmark.stringOriginal:gc.time                 avgt   10    83.000               ms
UUIDGeneratorBenchmark.uuidAlternative                        avgt   10   152.187 ±  3.638   ns/op
UUIDGeneratorBenchmark.uuidAlternative:gc.alloc.rate          avgt   10   601.704 ± 14.283  MB/sec
UUIDGeneratorBenchmark.uuidAlternative:gc.alloc.rate.norm     avgt   10    96.000 ±  0.001    B/op
UUIDGeneratorBenchmark.uuidAlternative:gc.count               avgt   10   102.000           counts
UUIDGeneratorBenchmark.uuidAlternative:gc.time                avgt   10    45.000               ms
UUIDGeneratorBenchmark.uuidOriginal                           avgt   10   347.017 ± 11.873   ns/op
UUIDGeneratorBenchmark.uuidOriginal:gc.alloc.rate             avgt   10  1121.765 ± 38.408  MB/sec
UUIDGeneratorBenchmark.uuidOriginal:gc.alloc.rate.norm        avgt   10   408.000 ±  0.001    B/op
UUIDGeneratorBenchmark.uuidOriginal:gc.count                  avgt   10   189.000           counts
UUIDGeneratorBenchmark.uuidOriginal:gc.time                   avgt   10    82.000               ms
```
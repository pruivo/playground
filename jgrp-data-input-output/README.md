# Micro benchmark for ByteArrayDataOutputStream and ByteArrayDataOutputStream

1. Compile it with

```bash
mvn clean package -Djgroups.version=<version>
```

The option `-Djgroups.version` is optional and it uses `5.4.7.Final` by default.

2. Running the benchmark

```bash
java -jar target/benchmarks.jar JGroupsDataBenchmark
```

Or

```bash
java -jar target/benchmarks.jar JGroupsDataBenchmark.<regex or method name>
```
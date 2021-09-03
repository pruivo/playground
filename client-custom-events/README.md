# Infinispan - Hot Rod client custom events

Shows how converters can be used with Hot Rod client.

## Instructions

### Download and extract Infinispan server

Download Infinispan server and extract it.
Let's assume the server root folder is `ISPN_HOME`

### Create user

Create the following user with admin permissions, using the following CLI command

`${ISPN_HOME}/bin/cli.sh user create user -p pass -g admin`

### Compile this project

Run maven install to compile this project and generate the jars

`mvn install`

### Copy JAR to the Infinispan server

From the root of this project, do

`cp data/target/custom-data-1.0-SNAPSHOT.jar ${ISPN_HOME}/server/lib/`

### Run the application

Start the server

`${ISPN_HOME}/bin/server.sh`

And run this application.
From the root of this project, execute

`mvn -pl client/ exec:java`

If everything works as expects, you should see an output similar to:

```
2021-09-03 18:33:54,354 INFO  (me.pruivo.Application.main()) [org.infinispan.HOTROD] ISPN004021: Infinispan version: Infinispan 'Taedonggang' 12.1.7.Final
2021-09-03 18:33:54,515 INFO  (HotRod-client-async-pool-1-1) [org.infinispan.HOTROD] ISPN004006: Server sent new topology view (id=1, age=0) containing 1 addresses: [127.0.0.1:11222]
Person One Person has ID db4d0e8f-8fe2-43d6-9a0e-b5aad3fdbb11
Person Another Person has ID b301265a-4bfb-47d5-9b94-eca30ef358f1
Person John Doe has ID 1321bd71-1dd1-4f46-81f9-015f37a35b1e
```
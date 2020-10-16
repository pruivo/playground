# Infinispan - avoid using Enum in keys

## Background

Infinispan uses the `hashCode()` to compute in which segment the key belongs to. 
Unfortunately, Enums do not have a "stable" (lack of better word) `hashCode()`.
It means, Infinispan will compute different segments for the same key.

If a key belongs to 2 or more segments, because of the wrong hash code, it can cause all sort of replication issues in
your cluster.

Be careful when choosing your keys!

## Running the example

In 2 terminals, run the following command:

``` bash
mvn package exec:java
```

Make sure the Infinispan instances see each other.
Lookup for the following log message:

```
INFO: ISPN000094: Received new cluster view for channel ISPN: [node_1|1] (2) [node_1, node_2]
```

Press `ENTER` in the terminal 1

It will print the events with the keys created. 
I got the following in terminal 1:

```
Created: key=MyKey{class=class java.util.concurrent.TimeUnit$4, hashCode=282279096, value=SECONDS}, value=f06462cc-28de-43fb-bf6a-87fe4739ff84, segment=63
Created: key=MyKey{class=class java.lang.String, hashCode=-1606887841, value=SECONDS}, value=f06462cc-28de-43fb-bf6a-87fe4739ff84, segment=189
```

And this in terminal 2:

```
Created: key=MyKey{class=class java.util.concurrent.TimeUnit$4, hashCode=1394353133, value=SECONDS}, value=f06462cc-28de-43fb-bf6a-87fe4739ff84, segment=70
Created: key=MyKey{class=class java.lang.String, hashCode=-1606887841, value=SECONDS}, value=f06462cc-28de-43fb-bf6a-87fe4739ff84, segment=189
```

As you can see from the events logs, the `MyKey` using an Enum belongs to different segments:
segment 63 in terminal 1 and segment 70 in terminal 2.

On other hand, String works just fine and belongs to the same segment in both terminals.

Press `ENTER` in terminal 2. 
It will insert the same keys and generate similar events.

Press `ENTER` in terminal 1 and 2; it will print the current keys and values stored in the cache.

From the logs, you can see the `MyKey` with Enum will have different values:

```
# Terminal 1
Enum MyKey{class=class java.util.concurrent.TimeUnit$4, hashCode=282279096, value=SECONDS}=f06462cc-28de-43fb-bf6a-87fe4739ff84
String MyKey{class=class java.lang.String, hashCode=-1606887841, value=SECONDS}=05bb6ed0-eaf4-4a26-ab8a-7933844dfcc9

# Terminal 2
Enum MyKey{class=class java.util.concurrent.TimeUnit$4, hashCode=1394353133, value=SECONDS}=05bb6ed0-eaf4-4a26-ab8a-7933844dfcc9
String MyKey{class=class java.lang.String, hashCode=-1606887841, value=SECONDS}=05bb6ed0-eaf4-4a26-ab8a-7933844dfcc9
``` 

Finally, press `ENTER` again, in both terminals, to stop the application.
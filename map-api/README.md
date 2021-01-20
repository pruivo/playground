# Infinispan - Compute example

Note: Example tested with the following: 

* Maven 3.6.3
* JDK 8

This example show how JDK `compute()` related methods can be used with Infinispan in a clustered environment.
The example starts 2 Infinispan nodes. 
Infinispan uses JGroups under the hood which use IP multicast to find the nodes.
If IP multicast isn't available in your environment, this example may not work.

## Running the example

In a terminal, run

``` bash
mvn package exec:java
```

When the application starts, it should two nodes in the view:

```
INFO: ISPN000094: Received new cluster view for channel ISPN: [node-0|1] (2) [node-0, node-1]
```

Then, you will see the menu: 

```
Using Infinispan node node-0
0: Exit
1: Switch to other Infinispan node
2: Create user
3: List users
4: Create order
5: List orders
6: Update order to IN_PROGRESS
7: Update order to COMPLETE
8: Remove order
```

## Infinispan Functional API (experimental)

Also, it is included another implementation using Infinispan's Functional API.
To run it, enable the `functionalApi` profile by executing the following command:

``` bash
mvn package exec:java -PfunctionalApi
```

The Functional API has some advantage over the JDKs `compute()`:

* Non blocking.
* It only replicates to the backups if the value is changed (i.e. when `set()` is invoked in the function).
* It can return anything to caller while the JDK approach returns the full object stored.
* Update multiple keys by using `evalMany()` or `evalAll()`.

### Usage example

```
Select option:2
Name=Pedro
User created with id 1

Select option:4
User id=1
Order description=Item1
Order abbf99e2-2b84-4984-80cc-8c8659b6f04d created

Select option:4
User id=1
Order description=Item2
Order 09cf65c3-57ae-4ade-b0d4-c5bd1569d4b8 created

Select option:4
User id=1
Order description=Item3
Order 37b48181-56b2-4dd1-a52e-1de78be50d19 created

Select option:5
User id=1
User Pedro (3 orders)
- abbf99e2-2b84-4984-80cc-8c8659b6f04d (status=NEW)
  Item1
- 09cf65c3-57ae-4ade-b0d4-c5bd1569d4b8 (status=NEW)
  Item2
- 37b48181-56b2-4dd1-a52e-1de78be50d19 (status=NEW)
  Item3

Select option:6
User id=1
Order id=abbf99e2-2b84-4984-80cc-8c8659b6f04d
Order status IN_PROGRESS

Select option:7
User id=1
Order id=09cf65c3-57ae-4ade-b0d4-c5bd1569d4b8
Order status COMPLETE

Select option:5
User id=1
User Pedro (3 orders)
- abbf99e2-2b84-4984-80cc-8c8659b6f04d (status=IN_PROGRESS)
  Item1
- 09cf65c3-57ae-4ade-b0d4-c5bd1569d4b8 (status=COMPLETE)
  Item2
- 37b48181-56b2-4dd1-a52e-1de78be50d19 (status=NEW)
  Item3

Select option:8
User id=1
Order id=09cf65c3-57ae-4ade-b0d4-c5bd1569d4b8

Select option:5
User id=1
User Pedro (2 orders)
- abbf99e2-2b84-4984-80cc-8c8659b6f04d (status=IN_PROGRESS)
  Item1
- 37b48181-56b2-4dd1-a52e-1de78be50d19 (status=NEW)
  Item3

## switching to node-1
Select option:1
Node id [0-1]=1
===
Using Infinispan node node-1

Select option:5
User id=1
User Pedro (2 orders)
- abbf99e2-2b84-4984-80cc-8c8659b6f04d (status=IN_PROGRESS)
  Item1
- 37b48181-56b2-4dd1-a52e-1de78be50d19 (status=NEW)
  Item3

```
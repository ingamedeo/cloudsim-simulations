# Amedeo Baragiola - HW1 CS441
#### Politecnico di Milano
#### University of Illinois at Chicago

## Introduction

This homework consists in creating different simulations on multiple datacenters and analyze the results obtained.

The simulation code has been written in Scala and can be compiled using SBT.

## Installation instructions
This section contains the instructions on how to run the simulations implemented as part of this homework, the recommended procedure is to use IntellJ IDEA with the Scala plugin installed.

1. Open IntellJ IDEA, a welcome screen will be shown, select “Check out from Version Control” and then “Git”.
2. Enter the following URL and click “Clone”: https://bitbucket.org/abarag4/amedeo_baragiola_hw1.git
3. When prompted confirm with “Yes”
4. The SBT import screen will appear, proceed with the default options and confirm with “OK”
5. Confirm overwriting with “Yes”
6. You may now go to src/main/scala/com.abarag4/ and run the examples from there. A run configuration is automatically created when you click the green arrow next to the main method of the example you wish to run.

Note: The cloudsim framework has been compiled in jar files and added in the lib/ folder of the project. Please note that, although IntellJ IDEA shall recognise the dependencies automatically, this may sometimes fail. When this happens go to “File -> Project Structure”, select “Libraries” on the right-hand side and add the provided jars manually.

#### Alternative: SBT from CLI

If you don’t want to use an IDE, you may run this project from the command line (CLI), proceed as follows:

1. Type: git clone https://bitbucket.org/abarag4/amedeo_baragiola_hw1.git
2. Before running the actual code, you may wish to run tests with “sbt clean compile test”
3. Run the code: sbt clean compile run

Note: When you run either of the "sbt clean compile *" commands, you will be prompted to make a choice, please read the following section for more details.

## Project structure

In this section the project structure is described.

#### Simulations

The following Simulations are provided:

- Simulation1
- Simulation2

Further details below.

#### Tests

The following test classes are provided:

- Simulation1Test
- MyBrokerTest

###### Simulation1Test
The following methods are tested:

- createDatacenter: the test checks that the created Datacenter is not null and that it contains the correct number of hosts.
- createDatacenters: the test checks that upon a recursive call to this method the right number of datacenters is created.
- createHostImpl: the test checks that upon a recursive call to this method the right number of hosts is created.
- createVM: the test checks that upon a recursive call to this method the right number of VMs is created.
- createCloudlet: the test checks that upon a recursive call to this method, the cloudlets created are of the right type and number.

###### MyBrokerTest
The following methods are tested:

- submitMapperList: the test checks that all mappers are actually submitted to the broker and that the CloudLet type (Mapper) is still correct when retrieved.
- submitReducerList: the test checks that all reducers are actually submitted to the broker - and therefore queued for execution - and that the CloudLet type (Reducer) is still correct when retrieved.
- processEvent: the test runs a mock simulation with 3 mappers and 1 reducer and tests for the behaviour of the implemented submission policy.
In particular it is tested that once 3 mappers finish execution, a new reducer is started and ready for execution.

## Configuration parameters
The use of hardcoded values is limited in the source code as they limit code reuse and readability; instead a configuration file contains all the configuration parameters for the simulations.
The configuration file is "application.conf".

Configuration blocks are identified by curly braces and they can be nested if some configuration options are valid only within a certain context.

Configuration options for the following entities are provided:

- Datacenter
- Host
- VM
- Cloudlet

Those are valid in the context of a certain simulation.

## Implemented policy
In this section the implemented policy is described in detail.

Firstly, it is important to note that a new parameter was added to the host entity, the disk speed. This allows us to model the delay in loading data from disk when starting, for example, a map/reduce job.

Upon submission we have that data chunks need to be transferred to the hosts in order to be processed. This data transfer is modeled through a delay that is added to the cloudlet startup time.

**The implemented policy tried to reduce the delay caused by a limited disk speed by avoiding excessive data transfers between hosts**; let's consider a scenario to better understand how this is implemented.

Let's say we have a map/reduce job running on the cloud, data is initially split in chunks and sent to the various mappers. (Mappers are a special type of cloudlets in this context)
This causes a certain delay, indeed data needs to be transferred and the transfer speed is bounded by the disk speed.

After some mappers finish executing, we can now send their output to a reducer, however, two possible scenario may now occur:

1. **The reducer is started on the same host as the mappers**: In this case no data needs to be transferred (as it's already available on the local disk).
2. **The reducer is started on a different host than at least one of the mappers**: In this case data needs to be transferred among the hosts and the disk speed comes into play.

Therefore, **the policy will try to allocate a reducer on the same host of the mappers in order to minimize the total execution time.**

#### Implementation details
Delving deeper, the proposed implementation waits for at least two mappers to finish executing and then checks whether they run on the same host, if so, allocates a new reducer on that host on any available VM.
Otherwise, these mappers are queued and a new iteration is performed; when the next two mappers finish running, it will now check if at least 2 among the 4 mappers have run on the same host.

*The policy in ineffective if all mappers are run on different hosts*, furthermore, once all mappers have finished executing the remaining ones must be scheduled anyways, **irrespective of their host assignment**, this is suboptimal and therefore the delay is not avoided in this case.

Please see classes MyBroker.java and MyCloudlet.java.
Note: Please note that the number of reducers specified in the configuration file is only a maximum number and does not imply that all reducers will be scheduled. This is due to the fact that is multiple mappers all run on the same host a fewer number of reducers will be run. *This is expected behaviour*.
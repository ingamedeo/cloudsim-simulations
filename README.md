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

<b>Note:</b> When you run either of the "sbt clean compile *" commands, you will be prompted to make a choice, please read the following section for more details.

## Project structure

In this section the project structure is described.

#### Simulations

The following Simulations are provided:
- Simulation1: 

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
The use of hard-coded values is limited in the source code as they limit code re-use and readability; instead a configuration file contains all the configuration parameters for the simulations.
The configuration file is "application.conf".

Configuration blocks are identified by curly braces and they can be nested if some configuration options are valid only within a certain context.

Configuration options for the following entities are provided:
- Datacenter
- Host
- VM
- Cloudlet

Those are valid in the context of a certain simulation.

## Simulated infrastructure


## FINISH, remove everything below this line

As part of  homework assignment you will gain experience with creating and managing your Git repository, obtaining an open-source cloud simulation infrastructure Java project from a public Git repo, creating JUnit tests, and creating your SBT build and run scripts for your simulation application. Doing this homework is essential for successful completion of the rest of this course, since all other homeworks and the course project will share the same features of this homework: branching, merging, committing, pushing your code into your Git repo, creating test cases and build scripts, and using various tools for diagnosing problems with virtual machines and your applications.

First things first, you must create your account at [BitBucket](https://bitbucket.org/), a Git repo management system. It is imperative that you use your UIC email account that has the extension @uic.edu. Once you create an account with your UIC address, BibBucket will assign you an academic status that allows you to create private repos. Bitbucket users with free accounts cannot create private repos, which are essential for submitting your homeworks and the course project. Your instructor created a team for this class named [cs441_Fall2019](https://bitbucket.org/cs441_fall2019/). Please contact your TA, [Mr. Mohammed Siddiq](msiddi56@uic.edu) using your UIC.EDU email account and he will add you to the team repo as developers, since Mr.Siddiq already has the admin privileges. Please use your emails from the class registration roster to add you to the team and you will receive an invitation from BitBucket to join the team. Since it is a large class, please use your UIC email address for communications or Piazza and avoid emails from other accounts like funnybunny1992@gmail.com. If you don't receive a response within 12 hours, please contact us via Piazza, it may be a case that your direct emails went to the spam folder.

Next, if you haven't done so, you will install [IntelliJ](https://www.jetbrains.com/student/) with your academic license, the JDK, the Scala runtime and the IntelliJ Scala plugin, the [Simple Build Toolkit (SBT)](https://www.scala-sbt.org/1.x/docs/index.html) or the [Gradle build tool](https://gradle.org/) and make sure that you can create, compile, and run Java and Scala programs. Please make sure that you can run [various Java tools from your chosen JDK](https://docs.oracle.com/en/java/javase/index.html).

In this and all consecutive homeworks and in the course project you will use logging and configuration management frameworks. You will comment your code extensively and supply logging statements at different logging levels (e.g., TRACE, INFO, ERROR) to record information at some salient points in the executions of your programs. All input and configuration variables must be supplied through configuration files -- hardcoding these values in the source code is prohibited and will be punished by taking a large percentage of points from your total grade! You are expected to use [Logback](https://logback.qos.ch/) and [SLFL4J](https://www.slf4j.org/) for logging and [Typesafe Conguration Library](https://github.com/lightbend/config) for managing configuration files. These and other libraries should be imported into your project using your script [build.sbt](https://www.scala-sbt.org/1.0/docs/Basic-Def-Examples.html) or [gradle script](https://docs.gradle.org/current/userguide/writing_build_scripts.html). These libraries and frameworks are widely used in the industry, so learning them is the time well spent to improve your resumes.

Even though Cloud2Sim is written in Java, you can create your simulations using Scala, for which you will receive an additional bonus of up to 3% for fully pure functional (not imperative) implementation. Since being proficient in Java is a prerequisite for this course, you will be expected to learn Scala as you go. As you see from the StackOverflow survey, knowledge of Scala is highly paid and in great demand, and it is expected that you pick it relatively fast, especially since it is tightly integrated with Java. I recommend using the book on Programming in Scala: Updated for Scala 2.12 published on May 10, 2016 by Martin Odersky and Lex Spoon. You can obtain this book using the academic subscription on Safari Books Online. There are many other books and resources available on the Internet to learn Scala. Those who know more about functional programming can use the book on Functional Programming in Scala published on Sep 14, 2014
by Paul Chiusano and Runar Bjarnason.

To receive your bonus for writing your cloud simulation program code in Scala, you should avoid using **var**s and while/for loops that iterate over collections using [induction variables](https://en.wikipedia.org/wiki/Induction_variable). Instead, you should learn to use collection methods **map**, **flatMap**, **foreach**, **filter** and many others with lambda functions, which make your code linear and easy to understand. Also, avoid mutable variables at all cost. Points will be deducted for having many **var**s and inductive variable loops without explanation why mutation is needed in your code - you can always do without it.

## Overview
In this homework, you will experiment with creading cloud computing datacenters and running jobs on them. Of course, creating real cloud computing datacenters takes hundreds of millions of dollars and acres of land and a lot of complicated equipment, and you don't want to spend your money and resources creating physical cloud datacenters for this homework ;-). Instead, we have a cloud simulator, a software package that models the cloud environments and operates different cloud models that we study in the lectures. We will use *Cloud2Sim*, a simulation framework that is available from [Sourceforge](https://sourceforge.net/projects/cloud2sim/). It is an extension of *CloudSim*, a framework and a set of libraries for modeling and simulating cloud computing infrastructure and services. It is a [publically available project in Github](https://github.com/Cloudslab/cloudsim).

[CloudSim website](http://www.cloudbus.org/cloudsim/) contains a wealth of information and it is your starting point. It is recommended that you learn more about *CloudSim* -- you will find an [online course on CloudSim](https://www.superwits.com/library/cloudsim-simulation-framework) and [a new resource on CloudSim](https://www.cloudsimtutorials.online/) and your starting point is to [download and configure CloudSim](https://www.superwits.com/library/cloudsim-simulation-framework/course-content-cloudsim/downloadconfigurecloudsim) and to run examples that are provided in the Github repo. Those examples you will find under the section Documentation on the main CloudSim website. You will notice that tutorials are a bit dated referring to as older versions of Eclipse. You should be able to adjust accordingly to using IntelliJ. Those who want to read more about modeling physical systems and creating simulations can find ample resources on the Internet - I recommend the following paper by [Any Maria on Introduction to Modeling and Simulation](http://acqnotes.com/Attachments/White%20Paper%20Introduction%20to%20Modeling%20and%20Simulation%20by%20Anu%20Maria.pdf). 

This homework script is written using a retroscripting technique, in which the homework outlines are generally and loosely drawn, and the individual students improvise to create the implementation that fits their refined objectives. In doing so, students are expected to stay within the basic requirements of the homework and they are free to experiments. Asking questions is important, so please ask away at Piazza!

## Functionality
Once you installed and configured Cloud2Sim (and/or *CloudSim*), your job is to run examples supplied with the frameworks to perform two or more simulations where you will evaluate two or more datacenters with different characteristics (e.g., operating systems, costs, devices) and policies. Imagine that you are a cloud computing broker and you purchase computing time in bulk from different cloud providers and you sell this time to your customers, so that they can execute their jobs, i.e., cloudlets on the infrastructure of these cloud providers that have different policies and constraints. As a broker, your job is to buy the computing time cheaply and sell it at a good markup. One way to achieve it is to take cloudlets from your customers and estimate how long they will execute. Then you charge for executing cloudlets some fixed fee that represent your cost of resources summarily. Some cloudlets may execute longer than you expected, the other execute faster. If your revenue exceeds your expenses for buying the cloud computing time in bulk, you are in business, otherwise, you will go bankrupt!

There are different policies that datacenters can use for allocating Virtual Machines (VMs) to hosts, scheduling them for executions on those hosts, determining how network bandwidth is provisioned, and for scheduling cloudlets to execute on different VMs. Randomly assigning these cloudlets to different datacenters may result in situation where the executions of these cloudlets are inefficient and they takes a long time. As a result, you exhaust your supply of the purchased cloud time and you may have to refund the money to your customers, since you cannot fulfil the agreement, and you will go bankrupt. Modeling and simulating the executions of cloudlets in your clouds may help you chose a proper model for your business.

Once you installed and configured Cloud2Sim and ran its examples, your next job will be to create simulations where you will evaluate a large cloud provider with many datacenters with different characteristics (e.g., operating systems, costs, devices) and policies. You will form a stream of jobs, dynamically, and feed them into your simulation. You will design your own datacenter with your own network switches and network links. You can organize cloudlets into tasks to accomplish the same job (e.g., a map reduce job where some cloudlets represent mappers and the other cloudlets represent reducers). There are different policies that datacenters can use for allocating Virtual Machines (VMs) to hosts, scheduling them for executions on those hosts, determining how network bandwidth is provisioned, and for scheduling cloudlets to execute on different VMs. Randomly assigning these cloudlets to different datacenters may result in situation where the execution is inefficient and takes a long time. Using a cleverer algorithm like assigning tasks to specific clusters where the data is located may lead to more efficient cloud provider services.

Consider a snippet of the code below from one of the examples of using Cloud2Sim. In it, a network cloud datacenter is created with network hardware that is used to organize hosts in a connected network. VMs can exchange packets/messages using a chosen network topology. Depending on your simulation construct, you may view different levels of performances.
```java
protected final NetworkDatacenter createDatacenter() {
  final int numberOfHosts = EdgeSwitch.PORTS * AggregateSwitch.PORTS * RootSwitch.PORTS;
  List<Host> hostList = new ArrayList<>(numberOfHosts);
  for (int i = 0; i < numberOfHosts; i++) {
      List<Pe> peList = createPEs(HOST_PES, HOST_MIPS);
      Host host = new NetworkHost(HOST_RAM, HOST_BW, HOST_STORAGE, peList)
                    .setRamProvisioner(new ResourceProvisionerSimple())
                    .setBwProvisioner(new ResourceProvisionerSimple())
                    .setVmScheduler(new VmSchedulerTimeShared());
      hostList.add(host);
  }

  NetworkDatacenter dc =
          new NetworkDatacenter(
                  simulation, hostList, new VmAllocationPolicySimple());
  dc.setSchedulingInterval(SCHEDULING_INTERVAL);
  dc.getCharacteristics()
        .setCostPerSecond(COST)
        .setCostPerMem(COST_PER_MEM)
        .setCostPerStorage(COST_PER_STORAGE)
        .setCostPerBw(COST_PER_BW);
  createNetwork(dc);
  return dc;
}
```

Your homework can be divided roughly into five steps. First, you learn how Cloud2Sim organized and what your building blocks are. I suggest that you load the source code of Cloud2Sim Plus into IntelliJ and explore its classes, interfaces, and dependencies. Second, you design your own cloud provider organization down to rack/cluster organization as we will study in the lecture on cloud infrastructure. You will add various policies and load balancing heuristics like randomly allocating tasks to machines or using data locality to guide the task allocation. Next, you will create an implementation of the simulation(s) of your cloud provider using Cloud2Sim. Fourth, you will run multiple simulations with different parameters, statistically analyze the results and report them in your documentation with explanations why some cloud architectures are more efficient than the others in your simulations. 

### For the students who use the main textbook, in the final fifth step
you will describe your design of the map/reduce implementation of the simulation.

### For the students who use the alternative textbooks, in the final fifth step
you will create a simulation that shows how broadcast storm is created in the cloud.

## Baseline
Your absolute minimum gradeable baseline project can be based on the examples that come from the repo Cloud2Sim. To be considered for grading, your project should include at least one of your simulation programs written in Java, your project should be buildable using the SBT or the Gradle, and your documentation must specify how you create and evaluate your simulated clouds based on the cloud models that we learn in the class. Your documentation must include the results of your simulation, the measurement of the runtime parameters of the simulator (e.g., CPU and RAM utilization) and your explanations of how these results help you with your simulation objectives (e.g., choose the right cloud model and configuration). Simply copying Java programs from examples and modifying them a bit (e.g., rename some variables) will result in desk-rejecting your submission.

## Piazza collaboration
You can post questions and replies, statements, comments, discussion, etc. on Piazza. For this homework, feel free to share your ideas, mistakes, code fragments, commands from scripts, and some of your technical solutions with the rest of the class, and you can ask and advise others using Piazza on where resources and sample programs can be found on the internet, how to resolve dependencies and configuration issues. When posting question and answers on Piazza, please select the appropriate folder, i.e., hw1 to ensure that all discussion threads can be easily located. Active participants and problem solvers will receive bonuses from the big brother :-) who is watching your exchanges on Piazza (i.e., your class instructor and your TA). However, *you must not describe your simulation or specific details related how your construct your models!*

## Git logistics
**This is an individual homework.** Separate repositories will be created for each of your homeworks and for the course project. You will find a corresponding entry for this homework at  https://bitbucket.org/cs441_fall2019/homework1.git. You will fork this repository and your fork will be private, no one else besides you, the TA and your course instructor will have access to your fork. Please remember to grant a read access to your repository to your TA and your instructor. In future, for the team homeworks and the course project, you should grant the write access to your forkmates, but NOT for this homework. You can commit and push your code as many times as you want. Your code will not be visible and it should not be visible to other students (except for your forkmates for a team project, but not for this homework). When you push the code into the remote repo, your instructor and the TAs will see your code in your separate private fork. Making your fork public or inviting other students to join your fork for an individual homework will result in losing your grade. For grading, only the latest push timed before the deadline will be considered. **If you push after the deadline, your grade for the homework will be zero**. For more information about using the Git and Bitbucket specifically, please use this [link as the starting point](https://confluence.atlassian.com/bitbucket/bitbucket-cloud-documentation-home-221448814.html). For those of you who struggle with the Git, I recommend a book by Ryan Hodson on Ry's Git Tutorial. The other book called Pro Git is written by Scott Chacon and Ben Straub and published by Apress and it is [freely available](https://git-scm.com/book/en/v2/). There are multiple videos on youtube that go into details of the Git organization and use.

Please follow this naming convention while submitting your work : "Firstname_Lastname_hw1" without quotes, where you specify your first and last names **exactly as you are registered with the University system**, so that we can easily recognize your submission. I repeat, make sure that you will give both your TA and the course instructor the read/write access to your *private forked repository* so that we can leave the file feedback.txt with the explanation of the grade assigned to your homework.

## Discussions and submission
As it is mentioned above, you can post questions and replies, statements, comments, discussion, etc. on Piazza. Remember that you cannot share your code and your solutions privately, but you can ask and advise others using Piazza and StackOverflow or some other developer networks where resources and sample programs can be found on the Internet, how to resolve dependencies and configuration issues. Yet, your implementation should be your own and you cannot share it. Alternatively, you cannot copy and paste someone else's implementation and put your name on it. Your submissions will be checked for plagiarism. **Copying code from your classmates or from some sites on the Internet will result in severe academic penalties up to the termination of your enrollment in the University**. When posting question and answers on Piazza, please select the appropriate folder, i.e., hw1 to ensure that all discussion threads can be easily located.


## Submission deadline and logistics
Monday, September 30 at 3AM CST via the bitbucket repository. Your submission will include the code for the simulator program, your documentation with instructions and detailed explanations on how to assemble and deploye your cloud simulation along with the results of your simulation and a document that explains these results based on the characteristics and the parameters of your simulations, and what the limitations of your implementation are. Again, do not forget, please make sure that you will give both your TAs and your instructor the read access to your private forked repository. Your name should be shown in your README.md file and other documents. Your code should compile and run from the command line using the commands **sbt clean compile test** and **sbt clean compile run** or the corresponding commands for Gradle. Also, you project should be IntelliJ friendly, i.e., your graders should be able to import your code into IntelliJ and run from there. Use .gitignore to exlude files that should not be pushed into the repo.


## Evaluation criteria
- the maximum grade for this homework is 7% with the bonus up to 3%. Points are subtracted from this maximum grade: for example, saying that 2% is lost if some requirement is not completed means that the resulting grade will be 7%-2% => 5%; if the core homework functionality does not work, no bonus points will be given;
- only some basic cloud simulation examples from the cloudsim repo are given and nothing else is done: up to 7% lost;
- having less than five unit and/or integration tests: up to 5% lost;
- missing comments and explanations from the simulation program: up to 5% lost;
- logging is not used in the simulation programs: up to 3% lost;
- hardcoding the input values in the source code instead of using the suggested configuration libraries: up to 4% lost;
- no instructions in README.md on how to install and run your simulator: up to 5% lost;
- the program crashes without completing the core functionality: up to 3% lost;
- the documentation exists but it is insufficient to understand how you assembled and deployed all components of the cloud: up to 5% lost;
- the minimum grade for this homework cannot be less than zero.

That's it, folks!
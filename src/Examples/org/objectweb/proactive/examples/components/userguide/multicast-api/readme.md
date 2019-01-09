﻿# About
This folder contains source code example that will help you start-up with coding some simple GCM applications. You can use any IDE to run these example programs, just copy and paste the source files to your project, and start playing with it. You just need some to do following configurations. 



# Schema maintained in all examples

There is a **composite** component that holds the **master** and three **slave** components. The composite component is bound to master, and master is further bound to three slaves. We maintain this schema in all the examples, unless specified.  

# What is inside this folder.
This folder contains following examples

1. **Broadcast**  :The master broadcasts it's input to all the connected slaves. 
2. **Round-robin** :The master breaks it's input list of elements, and distributes it to slaves in round-robin fashion to slaves. 
3. **Unicast** : The master sends input to only one of the connected slaves.
4. **Random**: The master sends the items in the list of input to the connected slaves in random manner.
5. **One-to-One** The master sends **i-th** item in the list to **i-th** connected slave. The number of items in the list should be equal to number of connected slaves. 



### Configuring your project
Before you start running example code , you need to add policy file and JVM parameters to configure your environment to your java project.

###Creating policy file
Using any text editor, create a file with the name "proactive.java.policy" and paste the following into this file. 

```
grant 
{
  permission java.security.AllPermission;

  // Reflect access private Members
  // Used by:
  //         -Unicore Process
  permission java.lang.RuntimePermission "accessDeclaredMembers";
  permission java.lang.reflect.ReflectPermission "suppressAccessChecks";
};

```

 Once you create this security file, place it in the root folder of your java project.  Next step is making JVM aware of this policy file using below JVM parameter. 

```
-Djava.security.policy="proactive.java.policy" 
```
Second JVM parameter you need to add is 

```
-Dfractal.provider=org.objectweb.proactive.core.component.Fractive
```
That's all for the configuration. Have fun with GCM.

# JAR files to add
There are 4 Jar files that you must add to run these program.

1. _Proactive.jar_ from folder _scale-proactive-master/dist/lib_ 
2.  _Proactive_Annotations_CTree.jar" from folder _scale-proactive-master/dist/lib_ 
3. _fractal-gcm-api-1.1.1.jar_ from folder _scale-proactive-master/lib_
4. _fractal-api-2.0.3-SNAPSHOT.jar_ from folder _scale-proactive-master/lib_
 

 



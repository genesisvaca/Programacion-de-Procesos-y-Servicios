# 🧠 Programming of Processes and Services
### 📚 Subject Overview

Programming of Processes and Services focuses on how computer programs interact with the operating system, run concurrently, and communicate through services and networks.
The course introduces key concepts of process management, concurrent programming, client-server applications, network services, and security mechanisms such as encryption.

### 🧩 Key Concepts
### 🔸 Process

A process is a running instance of a program.
Examples: a web browser, a Java application, or any executable running in the operating system.

### 🔸 Service

A service is a special type of process that runs in the background and waits for requests.
Examples: SSH, FTP, or a web server that handles incoming connections.

### 🔸 Concurrent Programming

Concurrent programming allows multiple tasks (processes or threads) to be executed at the same time, sharing system resources to improve performance and efficiency.

# 🧱 Unit 1 – Concurrent Multiprocess Programming

In this unit, we learn how to create and manage processes in Java using the ProcessBuilder and Runtime classes, redirect input/output streams, and execute multiple tasks simultaneously.

### 🔹 Topics covered:

* Launching and controlling external processes

* Communicating between parent and child processes

* Redirecting process output and error streams

* Running multiple processes in parallel

### 🔹 Practical exercises:
#### 🧾 U1P01 – Execute Process

Create a Java program that runs another application (e.g., Notepad) using ProcessBuilder.

#### 🧾 U1P02 – Execute Java Process

Execute a Java Virtual Machine from within another Java program and capture its output using BufferedReader or redirect it to files (salida.txt, error.txt).

#### 🧾 U1P03 – ExecSumador / Sumador

Run five concurrent processes, each receiving two random numbers and calculating the sum of all integers between them.

#### 🧾 U1P04 – ContadorVocal

Create multiple processes, each counting occurrences of a different vowel in a text file, and store the results separately.

#⚙️ Unit 2 – Concurrent Programming with Threads (Multithreaded)

This unit focuses on threads, which are lightweight sub-processes sharing the same memory space.
You will learn how to create, start, and synchronize threads in Java.

### 🔹 Expected learning:

* Creating and starting threads (Thread and Runnable)

* Managing shared resources

* Avoiding race conditions

* Thread synchronization (synchronized, wait(), notify())

* Using thread pools and executors

# 🌐 Unit 3 – Client–Server Applications

Here you’ll build applications that communicate over a network, using sockets to exchange data between a client and a server.

### 🔹 Main topics:

* TCP and UDP communication

* Java Socket and ServerSocket classes

* Handling multiple clients concurrently

* Implementing simple chat or file-transfer applications

#🧭 Unit 4 – Network Services

This unit extends the client–server model into real services that can handle multiple requests efficiently.
It focuses on protocols, scalability, and resource management.

### 🔹 Topics:

* Service architecture (daemon processes)

* HTTP, FTP, SMTP, SSH basics

* REST APIs and lightweight services

* Monitoring and managing background services

# 🔐 Unit 5 – Security and Encryption

The final unit introduces security in network communication and data protection.

### 🔹 Topics covered:

* Symmetric and asymmetric encryption

* Public and private keys

* Digital certificates and signatures

* SSL/TLS and secure connections

* Hashing algorithms (SHA, MD5, etc.)

### 🧰 Tools and Technologies

* Java 17+

* ProcessBuilder / Runtime API

* Threads & Executors

* Sockets & Streams

* Encryption APIs (javax.crypto, java.security)

### 💡 Example: Executing a Process in Java
```java
ProcessBuilder pb = new ProcessBuilder("notepad.exe");
try {
    pb.start();
} catch (IOException e) {
    System.err.println("Error starting process");
    e.printStackTrace();
} 
```
🧠 Learning Goals

* By the end of the course, students will be able to:

* Understand how processes and threads work in an operating system.

* Develop concurrent and parallel programs in Java.

* Build network-based client–server applications.

* Implement and manage background services.

* Apply basic cryptography concepts to ensure secure communication.

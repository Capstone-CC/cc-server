# How To Run
How to run server on local (Ubuntu Environment)

## Install
```
you need jdk version 11 [Download](https://www.oracle.com/kr/java/technologies/javase-jdk11-downloads.html)
or
$ sudo apt install openjdk-11-jdk
```
```
$ java -version
> openjdk version "11.0.11" 2021-04-20
> openjdk version "11.0.11" 2021-04-20
> OpenJDK Runtime Environment (build 11.0.11+9-Ubuntu-0ubuntu2.18.04)
> OpenJDK 64-Bit Server VM (build 11.0.11+9-Ubuntu-0ubuntu2.18.04, mixed mode, sharing)

$ javac -version
> javac 11.0.11
```
## Run On the repository location
```
MacOS:
$ chmod +x gradlew
$ sudo ./gradlew clean bootjar
$ java -jar build/libs/cc-0.0.1-SNAPSHOT.jar

Window:
$ chmod +x gradlew
$ ./gradlew clean bootjar
$ java -jar build/libs/cc-0.0.1-SNAPSHOT.jar
```
Now you can check the api below url.

**[http://localhost:8080/api/swaager](localhost:8080/api/swaager)**

# Comparison of Java Web frameworks with Go
## Abstract
This project should show the differences between various Java
Web frameworks and Go. The project implements a little Rest API
with a POST endpoint to get a JWT for authentication purposes. This
is really a very simple example, but it's enough to demonstrate the 
differences between them. 

I was also eager to know, if Java compiled to native
executables is any better than letting the JVM doing it's work.

My interest was mainly, if Go is much better than Java ;-).
The aspects are

* requests per second handled
* memory consumption
* threads used
* Docker image size
* differences between the Java frameworks
* comparison between JVM and native compilation (Quarkus)

### Versions of Language and Docker base image used
All Java frameworks and the Kotlin/Ktor project use Java 11 as compiler.
The Docker images are mostly build with the `openjdk:11-jre-slim-buster` base image.

Quarkus is using `registry.access.redhat.com/ubi8/openjdk-11` for the JVM version and 
`registry.access.redhat.com/ubi8/ubi-minimal:8.5` for the native version.

The Go implementation uses go 1.18 as compiler and use the `scratch` base image which is an empty image.

All frameworks used the latest available stable version (march 31, 2022).

## Create artifacts
There is a `build-all-images.sh` script file in the root of the project that compiles the source code 
and creates the docker images for each framework and for go. The quarkus native image
build uses the GraalVM docker image and that needs a lot of RAM, so you might see out of memory
errors while building depending on your environment.

After running the script, you can either run the benchmark against the Docker container or directly against the
artifacts. The benchmark below use the latter one.

If you want to run them separate, you can proceed as follows:

#### Dropwizard
    cd dropwizard
    mvn clean package
    docker build -t web-dropwizard:1.0 .
    docker run --rm -i -p 8080:8080 web-dropwizard:1.0

#### Quarkus Java JVM
    cd ../quarkus
    ./mvnw clean package
    docker build -f src/main/docker/Dockerfile.jvm -t web-quarkus-jvm:1.0 .
    docker run --rm -i -p 8080:8080 web-quarkus-jvm:1.0

#### Quarkus Java native
    docker build -f src/main/docker/Dockerfile.native -t web-quarkus-native:1.0 .
    docker run --rm -i -p 8080:8080 web-quarkus-native:1.0

#### Quarkus Kotlin JVM
    cd ../quarkus-kotlin
    ./mvnw clean package
    docker build -f src/main/docker/Dockerfile.jvm -t web-quarkus-kotlin-jvm:1.0 .
    docker run --rm -i -p 8080:8080 web-quarkus-kotlin-jvm:1.0

#### Quarkus Kotlin native
    ./mvnw package -Pnative -Dquarkus.native.container-build=true
    docker build -f src/main/docker/Dockerfile.native -t web-quarkus-kotlin-native:1.0 .
    docker run --rm -i -p 8080:8080 web-quarkus-kotlin-native:1.0

#### Kotlin Ktor
    cd ../kotlin-ktor
    mvn clean package
    docker build -t web-kotlin-ktor:1.0 .
    docker run --rm -i -p 8080:8080 web-kotlin-ktor:1.0

#### Spring Boot
    cd ../spring-boot
    mvn clean package
    docker build -t web-spring-boot:1.0 .
    docker run --rm -i -p 8080:8080 web-spring-boot:1.0

#### GO
    cd ../go
    GOARCH=amd64 GOOS=linux CGO_ENABLED=0 go build -o web-go main.go
    docker build -t web-go:1.0 .
    docker run --rm -i -p 8080:8080 web-go:1.0

## Benchmarks
I was using apache benchmark (`ab`) for a while, but since I have a new notebook with a powerful CPU,
I realized, that ab does not bring my machine to the limit. The CPU usage while running the
benchmark was around 40%. That's why I moved to [wrk](https://github.com/wg/wrk). The data file
for the `ab` POST request is still there, so you can run `ab` benchmarks as well.

The wrk benchmark results are in a table down below.

### Apache benchmark (deprecated)
For each framework and for go, start the program as described and start the benchmark like this:

    # either run it via java -jar
    mvn clean package
    java -jar target/dropwizard-1.0-SNAPSHOT.jar server config.yml
    # or run the container
    docker run --rm -i -p 8080:8080 web-dw:1.0
    # warming up the JIT compiler
    ab -n 30000 -c 64 -p ./post.data -T application/json http://localhost:8080/login
    # run the benchmark
    ab -n 100000 -c 64 -p ./post.data -T application/json http://localhost:8080/login

### wrk benchmark
With this release (march 2022), I'm using wrk version 4.2.0 for benchmarking. These tests 
are running against Java (java -jar xxx.jar) or against binary executables. 

Three terminal windows (shell) are being used:

* The web app is started in terminal 1
* The wrk benchmark is started in terminal 2
  * `wrk -spost.lua -t12 -c400 -d10s http://127.0.0.1:8080/login` # warm-up
  * `wrk -spost.lua -t12 -c400 -d60s http://127.0.0.1:8080/login` # benchmark
* In terminal 3, btop is used to determine the amount of RAM being used by the web app 

#### Dropwizard
    cd dropwizard
    mvn clean package
    java -jar target/dropwizard-1.0-SNAPSHOT.jar server config.yml

#### Quarkus Java JVM
    cd ../quarkus
    mvn clean package
    java -jar target/quarkus-app/quarkus-run.jar

#### Quarkus Java native
    mvn package -Pnative -Dquarkus.native.container-build=true
    target/quarkus-java-1.0.0-SNAPSHOT-runner

#### Quarkus Kotlin JVM
    cd ../quarkus-kotlin
    mvn clean package
    java -jar target/quarkus-app/quarkus-run.jar

#### Quarkus Kotlin native
    mvn package -Pnative -Dquarkus.native.container-build=true
    target/quarkus-kotlin-1.0-SNAPSHOT-runner

#### Kotlin Ktor
    cd ../kotlin-ktor
    mvn clean package
    java -jar target/kotlin-ktor-0.0.1-jar-with-dependencies.jar

#### Spring Boot
    cd ../spring-boot
    mvn clean package
    java -jar target/spring-boot-0.0.1-SNAPSHOT.jar

#### GO
    cd ../go
    GOARCH=amd64 GOOS=linux CGO_ENABLED=0 go build -o web-go main.go
    ./web-go

### Results
The requests per second were measured with wrk (average of 3 runs each of 60 seconds). 
The RAM usage was measured with btop. 
Only one benchmark was running when the measurements took place.

The image size is what docker tells you when you execute `docker image ls`.

| framework / language | requests per second | RAM usage peak in MB | threads | image size in MB | remarks |
| --------------------- | ------: | ---: | --: | -----: | :----- |
| Dropwizard            |  69.633 | 1300 | 465 | 239.00 | |
| Quarkus Java jvm      | 113.841 | 1400 | 265 | 451.00 | |
| Quarkus Java native   |  44.245 |  569 | 236 | 148.00 | 80% CPU |
| Quarkus Kotlin jvm    | 112.176 | 1200 | 265 | 457.00 | |
| Quarkus Kotlin native |  44.289 |  555 | 236 | 148.00 | 82% CPU |
| Kotlin Ktor jvm       | 109.619 | 1100 |  59 | 238.00 | |
| Spring boot           |  81.617 |  904 | 241 | 238.00 | 95% CPU |
| Go                    | 106.055 |   33 |  29 |   7.35 | 96% CPU |

All tests were done on a Lenovo Thinkpad T14s gen1 with AMD Ryzen 7 pro 4750u
and 32GB of RAM running Manjaro Linux 64Bit.

## Conclusion
The CPU usage is about 20% less for the native compiled Quarkus binaries. 
There's probably some room for improvement. Spring Boot and Go use 5% less 
than possible, so there's maybe little room for improvement.

The memory usage is for dropwizard, spring boot, the Quarkus and Ktor jvm's 
nearly the same, with a light advantage for Spring Boot. Quarkus native
versions are using less than half the memory of the jvm versions. 
Go is using less than 1/17th of the best Java framework with native binaries.

The Docker image sizes depend on the base image being used. The biggest images
have the Quarkus JVM images. Next in size are Dropwizard, Kotlin Ktor JVM and
Spring Boot which are all around 240MB. These use the openjdk:11-jre-slim-buster 
base image. The images of the native compiled Quarkus versions are 90MB smaller.
The Go image is only using 1/20th of the next best Java solution.

All Java frameworks use more than 200 threads to get the work done. Dropwizard
spawns even more threads, but this did not result in better performance. Ktor
use approx. 60 threads and has very good performance. Go use even less threads (30)
and has also a very good performance.

The requests per second (r/s)is for the Java JVM solutions around 110.000 r/s with a 
light disadvantage for Kotlin Ktor. Next in speed is GO with 106.000 r/s.
With a gap of 25.000 r/s comes Spring Boot next. Another 12.000 r/s slower is 
Dropwizard. The slowest solutions are the native compiled ones. They can only
handle 40% of the fastest solution.

The comparison of JVM and native mode shows, that it's only possible to have either
speed (JVM) or low memory consumption (native). My conclusion is to use the native mode if you
have memory constraints in your environment. But if this is very important, you
should consider to move your code base to Go.

The overall best performance regarding speed, memory usage and image size delivers
the GO solution. It's not the fastest, but the amount of RAM needed outperformes all
Java based solutions.

I know, this is only a simple example. If somone else has a better use case, please fork this project and supply a merge request and I will try to add it.

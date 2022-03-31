#!/usr/bin/env bash

export START_FOLDER=$(pwd)
# Please append a slash to your dockerhub account or leave it empty
export YOUR_DOCKERHUB_ACCOUNT=

cd $START_FOLDER/dropwizard
mvn clean package
docker build -t ${YOUR_DOCKERHUB_ACCOUNT}web-dropwizard:1.0 .

cd $START_FOLDER/quarkus
./mvnw clean package
docker build -f src/main/docker/Dockerfile.jvm -t ${YOUR_DOCKERHUB_ACCOUNT}web-quarkus-jvm:1.0 .
./mvnw package -Pnative -Dquarkus.native.container-build=true
docker build -f src/main/docker/Dockerfile.native -t ${YOUR_DOCKERHUB_ACCOUNT}web-quarkus-native:1.0 .

cd $START_FOLDER/quarkus-kotlin
./mvnw clean package
docker build -f src/main/docker/Dockerfile.jvm -t ${YOUR_DOCKERHUB_ACCOUNT}web-quarkus-kotlin-jvm:1.0 .
./mvnw package -Pnative -Dquarkus.native.container-build=true
docker build -f src/main/docker/Dockerfile.native -t ${YOUR_DOCKERHUB_ACCOUNT}web-quarkus-kotlin-native:1.0 .

cd $START_FOLDER/kotlin-ktor
mvn clean package
docker build -t ${YOUR_DOCKERHUB_ACCOUNT}web-kotlin-ktor:1.0 .

cd $START_FOLDER/spring-boot
mvn clean package
docker build -t ${YOUR_DOCKERHUB_ACCOUNT}web-spring-boot:1.0 .

cd $START_FOLDER/go
GOARCH=amd64 GOOS=linux CGO_ENABLED=0 go build -o web-go main.go
docker build -t ${YOUR_DOCKERHUB_ACCOUNT}web-go:1.0 .

cd $START_FOLDER

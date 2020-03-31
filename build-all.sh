#!/usr/bin/env bash

export START_FOLDER=$(pwd)
export YOUR_DOCKERHUB_ACCOUNT=PLEASE_FILL

cd $START_FOLDER/dropwizard
mvn clean package
docker build -t ${YOUR_DOCKERHUB_ACCOUNT}/web-dw:1.0 .

cd $START_FOLDER/spring-boot
mvn clean package
docker build -t ${YOUR_DOCKERHUB_ACCOUNT}/web-spring:1.0 .

cd $START_FOLDER/quarkus
./mvnw clean package
docker build -f src/main/docker/Dockerfile.jvm -t ${YOUR_DOCKERHUB_ACCOUNT}/web-quarkus-jvm:1.0 .
./mvnw clean
docker build -f src/main/docker/Dockerfile.multistage -t ${YOUR_DOCKERHUB_ACCOUNT}/web-quarkus-native:1.0 .

cd $START_FOLDER/go
GOARCH=amd64 GOOS=linux CGO_ENABLED=0 go build -o web-go main.go
docker build -t ${YOUR_DOCKERHUB_ACCOUNT}/web-go:1.0 .

cd $START_FOLDER

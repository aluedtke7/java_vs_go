#!/usr/bin/env bash

# start minikube with latest k8s version using kvm as hypervisor
minikube start --memory=g --driver=kvm2
# enable the metrics server to get cpu and memory usage
minikube addons enable metrics-server

./build-all.sh

# deploy all services
kubectl apply -f k8s/dw.yml
kubectl apply -f k8s/go.yml
kubectl apply -f k8s/quarkus-jvm.yml
kubectl apply -f k8s/quarkus-native.yml
kubectl apply -f k8s/spring.yml

# print a list of all services and their ports
minikube service list

# start the dashboard and open it in browser
minikube dashboard

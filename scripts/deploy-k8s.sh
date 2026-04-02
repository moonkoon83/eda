#!/bin/bash

echo "1. Building applications with Gradle..."
./gradlew clean build -x test

echo "2. Building Docker images..."
# Order Service
docker build --build-arg JAR_FILE=eda-order-service/build/libs/*.jar -t order-service:latest .
# Stock Service
docker build --build-arg JAR_FILE=eda-stock-service/build/libs/*.jar -t stock-service:latest .
# Point Service
docker build --build-arg JAR_FILE=eda-point-service/build/libs/*.jar -t point-service:latest .

echo "3. Creating namespace..."
kubectl create namespace local --dry-run=client -o yaml | kubectl apply -f -

echo "4. Deploying Infrastructure (MySQL, Kafka)..."
kubectl apply -f ../k8s/infrastructure/mysql.yaml
kubectl apply -f ../k8s/infrastructure/kafka.yaml

echo "Waiting for infrastructure to be ready..."
sleep 20

echo "5. Deploying Applications..."
kubectl apply -f ../k8s/apps/order/deployment.yaml
kubectl apply -f ../k8s/apps/stock/deployment.yaml
kubectl apply -f ../k8s/apps/point/deployment.yaml

echo "Deployment complete!"
kubectl get pods -n local

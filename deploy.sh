#!/usr/bin/env bash

# ./gradlew dockerTag
# ./gradlew dockerPushGCP
# create a GKE cluster
# configure kubectl for your cluster!:  source .env && gcloud container clusters get-credentials $CLUSTER_NAME --region $REGION --project $PROJECT_ID

# create deployment, hpa and service
kubectl create -f deployment.yaml      #create pods
kubectl create -f service.yaml

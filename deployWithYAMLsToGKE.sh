#!/usr/bin/env bash

# you need a GKE cluster

# create deployment, hpa and service
# kubectl create/apply/diff -f ...yaml
kubectl create -f deployment.yaml #create pods


# roll-out
# 1- kubectl set image deployment/demo-app demo=gcr.io/sandbox-236618/demo:0.0.2-SNAPSHOT --record
# 2- kubectl edit deployment ....
# 3- kubectl apply -f ....

# after deployment update, to handle connection error
# 1- minReadySeconds
# 2- health probes (readiness, liveliness ) more robust solution

# decrease nodes
# gcloud container clusters resize core-cluster --region europe-west4 --num-nodes=0
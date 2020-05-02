#!/usr/bin/env bash

# you need a GKE cluster
# configure kubectl for your cluster!

# create deployment, hpa and service
kubectl create -f deployment.yaml      #create pods
kubectl create -f service.yaml

export CLUSTER_NAME=core-cluster
export IMAGE_NAME=gcr.io/sandbox-236618/demo:0.0.1-SNAPSHOT
export DEPLOYMENT_NAME=demo-app

kubectl create deployment $DEPLOYMENT_NAME --image=$IMAGE_NAME
kubectl expose deployment $DEPLOYMENT_NAME --type=LoadBalancer --port 8080

kubectl scale deployment  $DEPLOYMENT_NAME  --replicas=3
kubectl autoscale deployment $DEPLOYMENT_NAME --max=10 --cpu-percent=70

# get external ip
kubectl get services

# to test - (watch works in gshell or install on your local)
# watch curl 34.91.224.108:8080/dummy
# curl EXTERNAL_IP:8080/dummy
# curl EXTERNAL_IP:8080/dummy/enter-a-name
# curl EXTERNAL_IP:8080/actuator/health


# get initial yaml files
# kubectl get deployments demo-app -o yaml > deployment.yaml
# kubectl get services demo-app -o yaml > service.yaml

# decrease nodes
# gcloud container clusters resize core-cluster --region europe-west4 --num-nodes=0
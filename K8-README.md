# Kubernetes Real-World Project 🚀

A hands-on Kubernetes deployment project for a Python web application using Docker, Kubernetes Deployment, Service, Ingress, ConfigMap, Secret, Persistent Storage, HPA, RBAC, Jobs, and CronJobs.

The project was built and tested locally using **Minikube**.

---

## 📌 Project Overview

This project demonstrates how to deploy and manage a containerized Python application on Kubernetes.

The application is first containerized using Docker and then deployed to a Kubernetes cluster using a Deployment.

Kubernetes components used in this project provide:

- High availability using multiple replicas
- Service discovery and stable networking
- External access using Ingress
- Configuration management using ConfigMap
- Sensitive configuration using Secret
- Persistent storage using PV/PVC
- Automatic scaling using HPA
- Container health monitoring using probes
- Container security using SecurityContext
- Access control using RBAC
- Scheduled and one-time workloads using Jobs and CronJobs

---

## 🏗️ Architecture

```text
                         User
                          |
                          v
                    NGINX Ingress
                          |
                          v
                    Kubernetes Service
                          |
             +------------+------------+
             |            |            |
             v            v            v
           Pod 1        Pod 2        Pod 3
             |            |            |
             +------------+------------+
                          |
                    Python Application
                          |
          +---------------+---------------+
          |               |               |
          v               v               v
      ConfigMap         Secret          PVC
                                          |
                                          v
                                         PV


                  Deployment
                      |
                  ReplicaSet
                      |
                    Pods

                    HPA
                     |
              Automatic Scaling

             ServiceAccount
                     |
                 RBAC
              /          \
           Role      RoleBinding

             Job / CronJob
```

---

## 🛠️ Technologies Used

- Docker
- Kubernetes
- Minikube
- kubectl
- NGINX Ingress Controller
- Python
- Flask
- YAML
- Git / GitHub

---

## 📂 Project Structure

```text
Kubernetes-project/
│
├── k8/
│   ├── k8deployment.yaml
│   ├── service.yaml
│   ├── ingress.yaml
│   ├── configmap.yaml
│   ├── secret.yaml
│   ├── pvc.yaml
│   ├── namespace.yaml
│   ├── rbac.yaml
│   ├── job.yaml
│   └── cronjob.yaml
│
├── Dockerfile
├── requirements.txt
├── app.py
└── README.md
```

---

# 🐳 Docker Setup

The Python application is containerized using Docker.

### Build the image

```bash
docker build -t kubernetes-project-app:v4 .
```

### Run the application locally

```bash
docker run -p 8080:8080 kubernetes-project-app:v4
```

The application can then be accessed locally on:

```text
http://localhost:8080
```

---

# ☸️ Kubernetes Setup

## 1. Start Minikube

```bash
minikube start
```

Check the cluster:

```bash
kubectl get nodes
```

Expected:

```text
STATUS
Ready
```

---

# 2. Enable Ingress

```bash
minikube addons enable ingress
```

Check the Ingress Controller:

```bash
kubectl get pods -n ingress-nginx
```

---

# 3. Load Docker Image into Minikube

Because the Docker image is locally built, it must be available to the Minikube environment.

```bash
minikube image load kubernetes-project-app:v4
```

Verify:

```bash
minikube image ls | grep kubernetes-project-app
```

The Deployment uses:

```yaml
imagePullPolicy: IfNotPresent
```

so Kubernetes can use the image already available to the Minikube node.

---

# 4. Create ConfigMap

The ConfigMap contains non-sensitive application configuration.

Example:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: pythonapp-config

data:
  APP_NAME: "Kubernetes Real World Project"
  APP_ENV: "production"
  APP_PORT: "8080"
```

Apply:

```bash
kubectl apply -f k8/configmap.yaml
```

Verify:

```bash
kubectl get configmap
```

---

# 5. Create Secret

The Secret contains sensitive application configuration.

Example:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: pythonapp-secret

type: Opaque

stringData:
  DB_USERNAME: admin
  DB_PASSWORD: mypassword123
```

Apply:

```bash
kubectl apply -f k8/secret.yaml
```

Verify:

```bash
kubectl get secret
```

> **Security note:** Do not commit real passwords or credentials to GitHub. Use a `secret.example.yaml` template or another secret-management solution for public repositories.

---

# 6. Persistent Storage

The application uses a PersistentVolumeClaim to request persistent storage.

```text
Pod
 |
 | volumeMount
 v
PVC
 |
 v
PV
```

Apply:

```bash
kubectl apply -f k8/pvc.yaml
```

Check:

```bash
kubectl get pvc
kubectl get pv
```

Expected PVC status:

```text
Bound
```

---

# 7. Deploy the Application

Apply the Deployment:

```bash
kubectl apply -f k8/k8deployment.yaml
```

Check:

```bash
kubectl get deployment
```

Check Pods:

```bash
kubectl get pods
```

The Deployment runs **3 replicas**.

Expected:

```text
READY
1/1
```

for all Pods.

---

# 8. Deployment Configuration

The Deployment includes:

### Replicas

```yaml
replicas: 3
```

This provides multiple application instances.

### Resource Requests

```yaml
requests:
  cpu: "100m"
  memory: "128Mi"
```

Requests represent the minimum resources Kubernetes should reserve for the container.

### Resource Limits

```yaml
limits:
  cpu: "500m"
  memory: "256Mi"
```

Limits define the maximum resources the container can consume.

---

# 9. Health Checks

The application uses Kubernetes health probes.

### Readiness Probe

```yaml
readinessProbe:
  httpGet:
    path: /health
    port: 8080
```

Readiness determines whether the Pod should receive traffic.

### Liveness Probe

```yaml
livenessProbe:
  httpGet:
    path: /health
    port: 8080
```

Liveness determines whether Kubernetes should restart the container.

---

# 🔐 Security

The container uses a Kubernetes SecurityContext:

```yaml
securityContext:
  runAsNonRoot: true
  allowPrivilegeEscalation: false
  readOnlyRootFilesystem: true
```

This helps reduce the container's privileges and attack surface.

The Docker image is configured to run using a non-root application user.

---

# 🌐 Service

The application is exposed internally through a Kubernetes ClusterIP Service.

```text
Ingress
   |
   v
Service :80
   |
   v
Pod :8080
```

The Service exposes port `80` and forwards traffic to the application's container port `8080`.

Check the Service:

```bash
kubectl get svc
```

---

# 🌍 Ingress

NGINX Ingress is used to route external HTTP traffic to the application Service.

Example host:

```text
pythonapp.local
```

The request flow is:

```text
Browser / curl
      |
      v
pythonapp.local
      |
      v
Ingress
      |
      v
pythonapp-service
      |
      v
Python Pods
```

---

# 🖥️ Local Ingress Testing

For local Minikube testing, the hostname can be mapped in the Windows hosts file:

```text
C:\Windows\System32\drivers\etc\hosts
```

Example:

```text
192.168.49.2    pythonapp.local
```

Depending on the Minikube Docker driver and Windows networking configuration, `minikube service` can be used to expose the Ingress Controller locally.

Example:

```bash
minikube service ingress-nginx-controller -n ingress-nginx --url
```

Then test using the returned URL:

```bash
curl -H "Host: pythonapp.local" http://127.0.0.1:<PORT>
```

Expected response:

```text
Kubernetes Real World Project - Application Running
```

---

# 📈 Horizontal Pod Autoscaler

The application can be automatically scaled using HPA.

Example:

```bash
kubectl autoscale deployment k8-deployment \
  --cpu-percent=70 \
  --min=3 \
  --max=6
```

Check:

```bash
kubectl get hpa
```

The HPA can increase or decrease the number of replicas based on CPU utilization.

---

# 🔑 ServiceAccount & RBAC

The application uses a dedicated ServiceAccount.

RBAC components:

```text
ServiceAccount
      |
      v
Role
      |
      v
RoleBinding
```

The Role provides only the required permissions.

Example permissions used in the project:

```yaml
resources:
  - pods

verbs:
  - get
  - list
```

This follows the principle of **least privilege**.

---

# ⚙️ Job

A Kubernetes Job was created to demonstrate one-time workloads.

```bash
kubectl get jobs
```

Jobs run a task until successful completion.

---

# ⏰ CronJob

A CronJob was created to demonstrate scheduled workloads.

Example schedule:

```text
*/1 * * * *
```

This executes the Job every minute during testing.

Check:

```bash
kubectl get cronjobs
kubectl get jobs
```

---

# 🔍 Useful Kubernetes Commands

### Check everything

```bash
kubectl get all
```

### Check Pods

```bash
kubectl get pods
```

### Detailed Pod information

```bash
kubectl describe pod <pod-name>
```

### Application logs

```bash
kubectl logs <pod-name>
```

### Execute commands inside a Pod

```bash
kubectl exec -it <pod-name> -- sh
```

### Deployment status

```bash
kubectl rollout status deployment/k8-deployment
```

### Deployment history

```bash
kubectl rollout history deployment/k8-deployment
```

### Rollback

```bash
kubectl rollout undo deployment/k8-deployment
```

---

# 🛠️ Troubleshooting Experience

During development, several real Kubernetes issues were identified and resolved.

### ImagePullBackOff

Cause:

- Image was not available inside Minikube.
- Kubernetes attempted to pull the image from a registry.

Solution:

```bash
minikube image load kubernetes-project-app:v4
```

and:

```yaml
imagePullPolicy: IfNotPresent
```

---

### YAML / Kubernetes Schema Error

Example:

```text
unknown field "cofigMapRef"
```

Cause:

```yaml
cofigMapRef:
```

Correct:

```yaml
configMapRef:
```

---

### runAsNonRoot Error

Kubernetes initially could not verify that the image user was non-root.

The Docker image was updated to use a non-root application user and Kubernetes security settings were configured accordingly.

---

### Ingress Connectivity Issues

Local Minikube networking caused connectivity issues when accessing the Ingress from Windows.

Troubleshooting included:

```bash
kubectl get ingress
kubectl get svc -n ingress-nginx
kubectl get pods -n ingress-nginx
minikube status
minikube service ingress-nginx-controller -n ingress-nginx --url
```

This helped verify each layer of the request path.

---

# 📚 Kubernetes Concepts Demonstrated

This project covers:

- Pods
- Labels and Selectors
- ReplicaSets
- Deployments
- Rolling Updates
- Rollbacks
- Services
- ClusterIP
- Ingress
- NGINX Ingress Controller
- ConfigMaps
- Secrets
- Resource Requests
- Resource Limits
- Readiness Probes
- Liveness Probes
- SecurityContext
- PersistentVolumes
- PersistentVolumeClaims
- Storage
- ServiceAccounts
- RBAC
- Horizontal Pod Autoscaling
- Jobs
- CronJobs
- Kubernetes Networking
- Kubernetes Troubleshooting

---

# 🎯 Learning Objectives

The main objective of this project was to gain practical experience with Kubernetes rather than only learning theoretical concepts.

The project focuses on:

- Deploying containerized applications
- Managing application configuration
- Managing secrets
- Exposing applications
- Persistent storage
- Application health monitoring
- Container security
- Scaling
- Access control
- Batch workloads
- Troubleshooting Kubernetes failures

---

# 🚀 Future Improvements

The next phase of this project will introduce CI/CD.

Planned pipeline:

```text
Developer
    |
    v
GitHub
    |
    v
Jenkins
    |
    +---- Build
    |
    +---- Test
    |
    +---- Docker Build
    |
    +---- Docker Push
    |
    v
Container Registry
    |
    v
Kubernetes
    |
    v
Deployment
    |
    v
Ingress
    |
    v
Application


The existing Kubernetes deployment will therefore become the **CD target** for the CI/CD pipeline.

---

# 👨‍💻 Author

Sumit

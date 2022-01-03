```bash
  #查看访问端口
> kubectl get svc -n ingress-nginx

```







```bash



> cat /etc/red-release

> vim /etc/hosts
192.168.80.100 master
192.168.80.101 node1
192.168.80.102 node2
192.168.80.103 node3



> systemctl start chronyd
> systemctl enable chronyd
> date



> systemctl stop firewalld
> systemctl disable firewalld

> systemctl stop iptables
> systemctl disable iptables




> vim /etc/selinux/config
SELINUX=disabled




> vim /etc/fstab
#/dev/mapper/centos-swap swap                    swap    defaults        0 0




> vim /etc/sysctl.d/kubernetes.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
net.ipv4.ip_forward = 1

> sysctl -p
> modprobe br_netfilter
> lsmod | grep br_netfilter




> yum install -y ipset ipvsadm
> cat <<EOF > /etc/sysconfig/modules/ipvs.modules
#!/bin/bash
modprobe -- ip_vs
modprobe -- ip_vs_rr
modprobe -- ip_vs_wrr
modprobe -- ip_vs_sh
modprobe -- nf_conntrack_ipv4
EOF


> chmod +x /etc/sysconfig/modules/ipvs.modules
> /bin/bash /etc/sysconfig/modules/ipvs.modules
> lsmod | grep -e ip_vs -e nf_conntrack_ipv4

```











```bash

> yum install -y yum-utils
> yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
> yum list docker-ce --showduplicates | grep 18.06.3.ce-3.el7
> yum install -y --setopt=obsoletes=0 docker-ce-18.06.3.ce-3.el7





> mkdir /etc/docker
> cat <<EOF > /etc/docker/daemon.json
{
  "exec-opts": ["native.cgroupdriver=systemd"],
  "registry-mirrors": ["https://kn0t2bca.mirror.aliyuncs.com"]
}
EOF

> systemctl restart docker
> systemctl enable docker
> docker version


```





```bash

> cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64/
enabled=1
gpgcheck=1
repo_gpgcheck=0
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF

> yum list kubelet --showduplicates
> yum install -y --setopt=obsoletes=0 kubeadm-1.17.4-0 kubelet-1.17.4-0 kubectl-1.17.4-0


> vim /etc/sysconfig/kubelet
KUBELET_CGROUP_ARGS="--cgroup-driver=systemd"
KUBE_PROXY_MODE="ipvs"

> systemctl enable kubelet && systemctl start kubelet


```







```bash
> kubeadm config images list


> images=(
	kube-apiserver:v1.17.4
	kube-controller-manager:v1.17.4
	kube-scheduler:v1.17.4
	kube-proxy:v1.17.4
	pause:3.1
	etcd:3.4.3-0
	coredns:1.6.5
)

# 阿里镜像
> for imageName in ${images[@]} ; do
	docker pull registry.cn-hangzhou.aliyuncs.com/google_containers/$imageName
	docker tag registry.cn-hangzhou.aliyuncs.com/google_containers/$imageName k8s.gcr.io/$imageName
	docker rmi registry.cn-hangzhou.aliyuncs.com/google_containers/$imageName
done

# 谷歌镜像
> for imageName in ${images[@]} ; do
	docker pull k8s.gcr.io/$imageName
done


# master 上面执行
> kubeadm init \
  --kubernetes-version=v1.17.4 \
  --pod-network-cidr=10.244.0.0/16 \
  --service-cidr=10.96.0.0/12 \
  --apiserver-advertise-address=192.168.80.100
  
> mkdir -p $HOME/.kube
> sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
> sudo chown $(id -u):$(id -g) $HOME/.kube/config


# 节点加入master
> kubeadm join 192.168.80.100:6443 --token m4bbrm.s2kn8acy3jmr1zp1 \
    --discovery-token-ca-cert-hash sha256:349c47c4460dce4a1d473bfd714313317faca5769f13819258028fde9b09f41f
    
    
> kubeadm join 192.168.80.45:6443 --token otp85l.3aja7yret17e1d9j \
    --discovery-token-ca-cert-hash sha256:6e8eb231c6de2719748f9c36e811f78e6fa85b4b9583a8111ccd4fdcbb1a93b9


```





#### 安装网络插入

```bash
> wget https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml

> vim kube-flannel.yml
quay.io改为quay-mirror.qiniu.com

> kubectl apply -f kube-flannel.yml

> kubectl get nodes


```



```bash

> kubectl create deployment nginx --image=nginx:1.14-alpine
> kubectl expose deployment nginx --port=80 --type=NodePort
> kubectl get pod
> kubectl get service


```







```bash

kubectl [command] [type] [name] [flags]

> kubectl get pod
> kubectl get pod pod_name
> kubectl get pod pod_name -o wide
> kubectl get pod pod_name -o yaml
> kubectl get pod pod_name -o json


# 描述信息
> kubectl describe pod pod_name


> kubectl api-resources

> kubectl create ns dev
> kubectl run pod --image=nginx:1.17.1 -n dev
> kubectl describe pods pod_name -n dev
> kubectl delete pods pod_name -n dev   --还会自动启动
> kubectl delete ns dev
```



> nginxpod.yml

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: dev

---

apiVersion: v1
kind: Pod
metadata:
  name: nginxpod
  namespace: dev
spec:
  containers:
  - name: nginx-containers
    image: nginx:1.17.1

```

```shell
> kubectl create -f nginxpod.yml
> kubectl delete -f nginxpod.yml

> kubectl apply -f nginxpod.yml


> kubectl get ns dev
> kubectl run nginx --image=1.17.1 --port=80 --namespace=dev


```





> pod-nginx.yml

```yaml
apiVersion: v1
kind: Pod
metadata: 
  name: nginx
  namespace: dev
spec:
  containers:
  - image: nginx:1.17.1
    name: pod
    ports:
    - name: nginx-port
      containerPort: 80
      protocol: TCP
```





```ba
> kubectl apply -f pod-nginx.yml
> kubectl delete -f pod-nginx.yml
```





#### Label

```bash
> kubectl get pod -n dev
> kubectl get pod -n dev --show-labels
> kubectl label pod pod_name -n dev version=1.0
> kubectl label pod pod_name -n dev version=2.0 --overwrite

> kubectl get pod -l version=1.0 --show-labels

> kubectl label pod nginx -n dev env-


```



> pod-nginx.yml

```
apiVersion: v1
kind: Pod
metadata: 
  name: pod-nginx
  namespace: dev
  labels:
    version: "3.0"
    env: "test"
spec:
  containers:
  - image: nginx:1.17.1
    name: pod
    ports:
    - name: nginx-port
      containerPort: 80
      protocol: TCP
```





#### deployment

```bash

> kubectl delete ns dev
> kubectl create ns dev
> kubectl run nginx --image=nginx:1.17.1 --port=80 --namespace=dev --replicas=3

> kubectl get deployment -n dev
> kubectl get deployment -n dev -o wide
> kubectl describe deployment nginx -n dev
```



> deploy-nginx.yml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata: 
  name: nginx
  namespace: dev
spec:
  replicas: 3
  selector:
    matchLabels:
      run: nginx
  template:					# pod模板
    metadata:
      labels:
        run: nginx
    spec:
      containers:
      - image: nginx:1.17.1
        name: nginx
        ports:
        - containerPort: 80
          protocol: TCP
```




```bash
> kubectl apply -f deploy-nginx.yml
> kubectl get deployment,pods -n dev
```







#### Service

```bash
> kubectl expose deployment nginx --name=svc-nginx1 --type=ClusterIP --port=80 --target-port=80 -n dev

> kubectl expose deployment nginx --name=svc-nginx2 --type=NodePort --port=80 --target-port=80 -n dev

> kubectl get svc -n dev

> kubectl delete svc svc_name -n dev
```



> svc-nginx.yml

```yaml
apiVersion: v1
kind: Service
metadata: 
  name: svc-nginx
  namespace: dev
spec:
  clusterIP: 10.109.179.231
  ports:
  - port: 80
    protocol: TCP
    targetPort: 80
  selector:
    run: nginx
  type: ClusterIP
```



```bash
> kubectl apply -f svc-nginx.yml
> kubectl delete -f svc-nginx.yml
```













#### Pod

```bash

> kubectl explain pod
> kubectl api-versions
> kubectl api-resources
> kubectl explain pod.spec.containers

```



> pod-imagepullpolicy.yml

```yaml
apiVersion: v1
kind: Pod
metadata: 
  name: pod-imagepullpolicy
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
    imagePullPolicy: Always # 镜像拉取策略,Never Always IfNotPresent.
  - name: busybox
    image: busybox:1.30

```



> pod-command.yml

```yaml
apiVersion: v1
kind: Pod
metadata: 
  name: pod-command
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
    imagePullPolicy: Always # 镜像拉取策略,Never Always IfNotPresent.
  - name: busybox
    image: busybox:1.30
    command: ["/bin/sh","-c","touch /tmp/hello.txt;while true;do /bin/echo $(date +%T) >> /tmp/hello.txt; sleep 3; done;"]

```



```bash
> kubectl apply -f pod-command.yml

> kubectl exec -it pod-command -n dev -c busybox /bin/sh
```





> pod-env.yml

```yaml
apiVersion: v1
kind: Pod
metadata: 
  name: pod-env
  namespace: dev
spec:
  containers:
  - name: busybox
    image: busybox:1.30
    command: ["/bin/sh","-c","touch /tmp/hello.txt;while true;do /bin/echo $(date +%T) >> /tmp/hello.txt; sleep 60; done;"]
    env:
    - name: "username"
      value: "admin"
    - name: "password"
      value: "123456"

```



```bash
> kubectl apply -f pod-env.yml
> kubectl exec -it pod-env -n dev -c busybox /bin/sh
```







> pod-ports.yml

```yaml
apiVersion: v1
kind: Pod
metadata: 
  name: pod-ports
  namespace: dev
spec:
  containers:
    - image: nginx:1.17.1
      imagePullPolicy: IfNotPresent
      name: nginx
      ports:
      - containerPort: 80
        name: nginx-port
        protocol: TCP
```



> pod-resources.yml

```yaml
apiVersion: v1
kind: Pod
metadata: 
  name: pod-resources
  namespace: dev
spec:
  containers:
    - image: nginx:1.17.1
      imagePullPolicy: IfNotPresent
      name: nginx
      resources:
        limits:
          cpu: "2"
          memory: "10Gi"
        requests:
          cpu: "1"
          memory: "10Mi"
```





> pod-initcontainer.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-initcontainer
  namespace: dev
spec:
  containers:
  - name: main-container
    image: nginx:1.17.1
    ports:
    - name: nginx-port
      containerPort: 80
  initContainers:
  - name: test-mysql
    image: busybox:1.30
    command: ["sh", "-c", "until ping 192.168.80.201 -c 1 ; do echo wating for mysql...; sleep2; done;"]
  - name: test-redis
    image: busybox:1.30
    command: ["sh", "-c", "until ping 192.168.80.202 -c 1 ; do echo wating for redis...; sleep2; done;"]
    
    
```



```bash
> kubectl get pod pod-initcontainer -n dev -w
> ifconfig ens33:1 192.168.80.201 netmask 255.255.255.0 up
> ifconfig ens33:2 192.168.80.202 netmask 255.255.255.0 up
```





> pod-hook-exec.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-hook-exec
  namespace: dev
spec:
  containers:
  - name: main-container
    image: nginx:1.17.1
    ports:
    - name: nginx-port
      containerPort: 80
    lifecycle:
      postStart:
        exec:
          command: ["/bin/sh", "-c", "echo postStart... > /usr/share/nginx/html/index.html"]
      preStop:
        exec:
          command: ["/usr/sbin/nginx", "-s", "quit"]
```



#### 探针

> pod-liveness-exec.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-liveness-exec
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
    ports:
    - name: nginx-port
      containerPort: 80
      protocol: TCP
    livenessProbe:
      exec:
        command: ["/bin/cat", "/tmp/hello.txt"]
```





> pod-liveness-tcpsocket.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-liveness-tcpsocket
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
    ports:
    - name: nginx-port
      containerPort: 80
      protocol: TCP
    livenessProbe:
      tcpSocket:
        port: 8080
      initialDelaySeconds: 10  # 启动后等待多少秒第一次探测
      timeoutSeconds: 5        # 超时时间，默认1秒，最小1秒
```









> pod-liveness-httpget.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-liveness-httpget
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
    ports:
    - name: nginx-port
      containerPort: 80
      protocol: TCP
    livenessProbe:
      httpGet:
        scheme: HTTP
        #host: 127.0.0.1
        port: 80
        path: /hello
```





#### restartPolicy

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-restartpolicy
  namespace: dev
spec:
  restartPolicy: Never    # 重启策略[Always(默认) | OnFailure | Never]
  containers:
  - name: nginx
    image: nginx:1.17.1
    ports:
    - name: nginx-port
      containerPort: 80
      protocol: TCP
    livenessProbe:
      httpGet:
        scheme: HTTP
        #host: 127.0.0.1
        port: 80
        path: /hello
```









### 定向调度

> pod-nodename.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-nodename
  namespace: dev
spec:
  nodeName: node1  # 指定调度到node1节点上
  containers:
  - name: nginx
    image: nginx:1.17.1
```



```bash
> kubectl get nodes
> kubectl label nodes node1 nodeenv=pro
> kubectl label nodes node2 nodeenv=test
> kubectl label nodes node1 nodeenv-
```



> pod-nodeselector.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-nodeselector
  namespace: dev
spec:
  nodeSelector: 
    nodeenv: pro  # 指定调度到具有nodeenv=pro标签节点上
  containers:
  - name: nginx
    image: nginx:1.17.1
```





##### 亲和性调度

> pod-nodeaffinity-required.yml

```yaml
apiVersion: v1
kind: Pod
metadata: 
  name: pod-nodeaffinity-required
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
  affinity:
    nodeAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:  # 硬限制
        nodeSelectorTerms:
        - matchExpressions:
          - key: nodeenv
            operator: In
            values: ["xxx", "yyy"]
```



> pod-nodeaffinity-preferred.yml

```yaml
apiVersion: v1
kind: Pod
metadata: 
  name: pod-nodeaffinity-preferred
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
  affinity:
    nodeAffinity:
      preferredDuringSchedulingIgnoredDuringExecution:  # 软限制
      - weight: 1
        preference:
          matchExpressions:
          - key: nodeenv
            operator: In
            values: ["xxx", "yyy"]
```





> pod-podaffinity-target.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-podaffinity-target
  namespace: dev
  labels:
    podenv: pro
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
  nodeName: node1
```

> pod-podaffinity-required.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-podaffinity-required
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
  affinity:
    podAffinity:  # pod亲和性
      requiredDuringSchedulingIgnoredDuringExecution:  # 硬限制
      - labelSelector:
          matchExpressions:
          - key: podenv
            operator: In
            values: ["xxx", "yyy"]
        topologyKey: kubernetes.io/hostname
```



> pod-podantiaffinity-required.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-podantiaffinity-required
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
  affinity:
    podAntiAffinity:  # pod亲和性
      requiredDuringSchedulingIgnoredDuringExecution:  # 硬限制
      - labelSelector:
          matchExpressions:
          - key: podenv
            operator: In
            values: ["pro"]
        topologyKey: kubernetes.io/hostname
```





#### 污点

```bash

> kubectl taint nodes node1 tag=heima:PreferNoSchedule

> kubectl run taint1 --image=nginx:1.17.1 -n dev
> kubectl get pod -n dev -o wide

> kubectl taint nodes node1 tag:PreferNoSchedule-
> kubectl taint nodes node1 tag=heima:NoSchedule

> kubectl run taint2 --image=nginx:1.17.1 -n dev
> kubectl get pods taint2 -n dev -o wide
```





#### 容忍

> pod-toleration.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-toleration
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
  tolerations:       # 添加容忍
  - key: "tag"       # 要容忍的污点的key
    operator: "Equal" # 操作符
    value: "heima"    # 容忍的污点的value
    effect: "NoSchedule"  # 容忍规则
```





#### Pod控制器

##### ReplicaSet

> pc-replicaset.yml

```yaml
apiVersion: apps/v1
kind: ReplicaSet
metadata: 
  name: pc-replicaset
  namespace: dev
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx-pod
  template:
    metadata:
      labels:
        app: nginx-pod
    spec:
      containers:
      - name: nginx
        image: nginx:1.17.1
```



```bash

> kubectl apply -f pc-replicaset.yml
> kubectl get rs pc-replicaset -n dev -o wide
> kubectl edit rs pc-replicaset -n dev				  # 扩容
> kubectl scale rs pc-replicaset --replicas=2 -n dev  # 缩容
```





##### Deployment(deploy)

> pc-deployment.yml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: pc-deployment
  namespace: dev
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx-pod
  template:
    metadata:
      labels:
        app: nginx-pod
    spec:
      containers:
      - name: nginx
        image: nginx:1.17.1
```



```bash
> kubectl apply -f pc-deployment.yml
> kubectl get deployment -n dev -o dev
> kubectl get rs -n dev
> kubectl get pod -n dev

> kubectl scale deploy pc-deployment --replicas=5 -n dev
> kubectl edit deploy pc-deployment -n dev
```



> pc-deployment.yml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: pc-deployment
  namespace: dev
spec:
  strategy:          # 策略
    type: Recreate   # 重建更新策略
  replicas: 3
  selector:
    matchLabels:
      app: nginx-pod
  template:
    metadata:
      labels:
        app: nginx-pod
    spec:
      containers:
      - name: nginx
        image: nginx:1.17.1
```

```bash
> kubectl set image deployment pc-deployment nginx=nginx:1.17.2 -n dev
```





> pc-deployment.yml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: pc-deployment
  namespace: dev
spec:
  strategy:               # 策略
    type: RollingUpdate   # 滚动更新策略
    rollingUpdate:
      maxUnavailable: 25%
      maxSurge: 25%
  replicas: 3
  selector:
    matchLabels:
      app: nginx-pod
  template:
    metadata:
      labels:
        app: nginx-pod
    spec:
      containers:
      - name: nginx
        image: nginx:1.17.1
```

```bash
> kubectl set image deployment pc-deployment nginx=nginx:1.17.3 -n dev

```



#### 版本回退

```bash
> kubectl apply -f pc-deployment.yml --record
> kubectl set image deploy pc-deployment nginx=nginx:1.17.2 -n dev
> kubectl rollout status deploy pc-deployment -n dev
> kubectl rollout history deploy pc-deployment -n dev
> kubectl get rs -n dev

> kubectl set image deploy pc-deployment nginx=nginx:1.17.3 -n dev
> kubectl rollout status deploy pc-deployment -n dev
> kubectl rollout history deploy pc-deployment -n dev

> kubectl get rs -n dev -o wide         # 查看当前镜像版本

> kubectl get deploy -n dev -o wide		# 查看当前pod控制器使用的镜像版本
> kubectl rollout undo deploy pc-deployment --to-revision=1 -n dev    # 回退版本
> kubectl get deploy -n dev -o wide

```





##### 金丝雀发布

```yaml
> kubectl set image deploy pc-deployment nginx=nginx:1.17.4 -n dev && kubectl rollout pause deployment pc-deployment -n dev

> kubectl rollout status deploy pc-deployment -n dev
> kubectl get rs -n dev -o wide
> kubectl get pods -n dev

> kubectl rollout resume deploy pc-deployment -n dev

```





#### HPA

##### 准备

```bash

> yum install -y git
> git clone -b v0.3.6 https://github.com/kubernetes-sigs/metrics-server.git

> cd metrics-server/deploy/1.8+
> vim metrics-server-deployment.yaml
```



> metrics-server-deployment.yaml

```yaml
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: metrics-server
  namespace: kube-system
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: metrics-server
  namespace: kube-system
  labels:
    k8s-app: metrics-server
spec:
  selector:
    matchLabels:
      k8s-app: metrics-server
  template:
    metadata:
      name: metrics-server
      labels:
        k8s-app: metrics-server
    spec:
	  # 添加处一
      hostNetwork: true
      serviceAccountName: metrics-server
      volumes:
      - name: tmp-dir
        emptyDir: {}
      containers:
      - name: metrics-server
        image: k8s.gcr.io/metrics-server-amd64:v0.3.6
        imagePullPolicy: Always
		# 添加处二
        args:
        - --kubelet-insecure-tls
        - --kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname

        volumeMounts:
        - name: tmp-dir
          mountPath: /tmp
```



```bash
> kubectl apply -f ./
> kubectl top node

> kubectl top pod -n kube-system

> kubectl run nginx --image=nginx:1.17.1 --requests=cpu=100m -n dev
> kubectl expose deployment nginx --type=NodePort --port=80 -n dev
> kubectl get deploy,pod,svc -n dev

```



##### 部署

> pc-hpa.yml

```yaml
apiVersion: autoscaling/v1
kind: HorizontalPodAutoscaler
metadata:
  name: pc-hpa
  namespace: dev
spec:
  minReplicas: 1      # 最小pod数量
  maxReplicas: 10     # 最大pod数量
  targetCPUUtilizationPercentage: 3      # CPU使用率指标
  scaleTargetRef:      # 指定要控制的nginx信息
    apiVersion: apps/v1
    kind: Deployment
    name: nginx
```



```bash
> kubectl create -f pc-hpa.yml
> kubectl get hpa -n dev
```







#### DaemonSet

> pc-daemonset.yml

```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: pc-daemonset
  namespace: dev
spec:
  selector:
    matchLabels:
      app: nginx-pod
  template:
    metadata:
      labels:
        app: nginx-pod
    spec:
      containers:
      - name: nginx
        image: nginx:1.17.1
```



```bash
> kubectl apply -f pc-daemonset.yml
> kubectl get ds -n dev
```





#### Job

```yaml
apiVersion: batch/v1
kind: Job
metadata: 
  name: pc-job
  namespace: dev
spec:
  manualSelector: true
  completions: 6
  parallelism: 3
  selector:
    matchLabels:
      app: counter-pod
  template:
    metadata:
      labels:
        app: counter-pod
    spec:
      restartPolicy: Never
      containers:
      - name: counter
        image: busybox:1.30
        command: ["bin/sh","-c","for i in 9 8 7 6 5 4 3 2 1; do echo $i; sleep 3; done"]
```



```bash
> kubectl create -f pc-job.yml
> kubectl get job -n dev -o wide -w
```







##### CronJob

```yaml
apiVersion: batch/v1beta1
kind: CronJob
metadata:
  name: pc-cronjob
  namespace: dev
  labels:
    controller: cronjob
spec:
  schedule: "*/1 * * * *"
  jobTemplate:
    metadata:
    spec:
      template:
        spec:
          restartPolicy: Never    # [Never | OnFailure]
          containers:
          - name: counter
            image: busybox:1.30
            command: ["bin/sh","-c","for i in 9 8 7 6 5 4 3 2 1;do echo $i;sleep 3; done"]
```



```bash
> kubectl apply -f pc-cronjob.yml
> kubectl get cj -n dev
> kubectl get jobs -n dev
```





#### Service

```bash
> kubectl edit cm kube-proxy -n kube-system
mode: "ipvs"
> kubectl delete pod -l k8s-app=kube-proxy -n kube-system
> ipvsadm -Ln
```



> deployment.yml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: pc-deployment
  namespace: dev
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx-pod
  template:
    metadata:
      labels:
        app: nginx-pod
    spec:
      containers:
      - name: nginx
        image: nginx:1.17.1
        ports:
        - containerPort: 80
```



> service-clusterip.yml

```yaml
apiVersion: v1
kind: Service
metadata:
  name: service-clusterip
  namespace: dev
spec:
  sessionAffinity: ClientIP
  selector:
    app: nginx-pod
  clusterIP: 10.97.97.97
  type: ClusterIP
  ports:
  - port: 80          # Service端口
    targetPort: 80    # pod端口
```



```bash
> kubectl get endpoints -n dev

```



#### HeaderLiness

> service-headliness.yml

```yaml
apiVersion: v1
kind: Service
metadata:
  name: service-headliness
  namespace: dev
spec:
  sessionAffinity: ClientIP
  selector:
    app: nginx-pod
  clusterIP: None
  type: ClusterIP
  ports:
  - port: 80          # Service端口
    targetPort: 80    # pod端口
```



```bash
> kubectl exec -it pc-deployment-6696798b78-5nf4l -n dev /bin/sh
# cat /etc/resolv.conf (copy nameserver)
> dig @10.96.0.10 service-headliness.dev.svc.cluster.local
```



#### NodePort

> service-nodeport.yml

```yaml
apiVersion: v1
kind: Service
metadata:
  name: service-nodeport
  namespace: dev
spec:
  selector:
    app: nginx-pod
  type: NodePort
  ports:
  - port: 80          # Service端口
    nodePort: 30002   # 指定绑定的node的端口(默认取值范围为30000-32767)如不指定会默认分配
    targetPort: 80    # pod端口
```



#### ExternalName

> service-externalname.yml

```yaml
apiVersion: v1
kind: Service
metadata:
  name: service-externalname
  namespace: dev
spec:
  type: ExternalName
  externalName: www.baidu.com
```



```bash
> kubectl describe svc service-externalname -n dev
> dig @10.96.0.10 service-externalname.dev.svc.cluster.local
```











#### Ingress

```bash
> mkdir ingress-controller && cd ingress-controller

> wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/nginx-0.30.0/deploy/static/mandatory.yaml

> wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/nginx-0.30.0/deploy/static/provider/baremetal/service-nodeport.yaml

> kubectl apply -f ./

> kubectl get pod -n ingress-nginx
```





> ingress-tomcat-nginx.yml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
  namespace: dev
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx-pod
  template:
    metadata:
      labels:
        app: nginx-pod
    spec:
      containers:
      - name: nginx
        image: nginx:1.17.1
        ports:
        - containerPort: 80

---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: tomcat-deployment
  namespace: dev
spec:
  replicas: 3
  selector:
    matchLabels:
      app: tomcat-pod
  template:
    metadata:
      labels:
        app: tomcat-pod
    spec:
      containers:
      - name: tomcat
        image: tomcat:8.5-jre10-slim
        ports:
        - containerPort: 8080


---

apiVersion: v1
kind: Service
metadata:
  name: nginx-service
  namespace: dev
spec:
  selector:
    app: nginx-pod
  clusterIP: None
  type: ClusterIP
  ports:
  - port: 80
    targetPort: 80


---

apiVersion: v1
kind: Service
metadata:
  name: tomcat-service
  namespace: dev
spec:
  selector:
    app: tomcat-pod
  clusterIP: None
  type: ClusterIP
  ports:
  - port: 8080
    targetPort: 8080

```



> ingress-http.yml

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: ingress-http
  namespace: dev
spec:
  rules:
  - host: nginx.itheima.com
    http:
      paths:
      - path: /
        backend:
          serviceName: nginx-service
          servicePort: 80
  - host: tomcat.itheima.com
    http:
      paths:
      - path: /
        backend:
          serviceName: tomcat-service
          servicePort: 8080
```



```bash
> kubectl create -f ingress-http.yml
> kubectl get ing ingress-http -n dev
> kubectl get svc -n ingress-nginx     # 80->http 443->https

> kubectl describe ing ingress-http -n dev
```





> ingress-https.yml

```bash

> openssl req -x509 -sha256 -nodes -days 365 -newkey rsa:2048 -keyout tls.key -out tls.crt -subj "/C=CN/ST=BJ/L=BJ/O=nginx/CN=itheima.com"

> kubectl create secret tls tls-secret --key tls.key --cert tls.crt

```



> ingress-https.yml

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata: 
  name: ingress-https
  namespace: dev
spec:
  tls:
    - hosts:
      - nginx.itheima.com
      - tomcat.itheima.com
      secretName: tls-secret  # 指定秘钥
  rules:
  - host: nginx.itheima.com
    http:
      paths:
      - path: /
        backend:
          serviceName: nginx-service
          servicePort: 80
  - host: tomcat.itheima.com
    http:
      paths:
      - path: /
        backend:
          serviceName: tomcat-service
          servicePort: 8080
```





#### Volume

##### EmptyDir

> volume-emptydir.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: volume-emptydir
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
    ports:
    - containerPort: 80
    volumeMounts:
    - name: logs-volume
      mountPath: /var/log/nginx
  - name: busybox
    image: busybox:1.30
    command: ["/bin/sh","-c","tail -f /logs/access.log"]
    volumeMounts:
    - name: logs-volume
      mountPath: /logs
  volumes:
  - name: logs-volume
    emptyDir: {}
```



```bash
> kubectl apply -f volume-emptydir.yml

> kubectl logs -f volume-emptydir -n dev -c busybox

```



##### HostPath

> volume-hostpath.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: volume-hostpath
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
    ports:
    - containerPort: 80
    volumeMounts: 
    - name: logs-volume
      mountPath: /var/log/nginx
  - name: busybox
    image: busybox:1.30
    command: ["/bin/sh","-c","tail -f /logs/access.log"]
    volumeMounts:
    - name: logs-volume
      mountPath: /logs
  volumes:
  - name: logs-volume
    hostPath:
      path: /root/logs
      type: DirectoryOrCreate
```





#### nfs

```bash
# 所有节点需要安装
> yum install -y nfs-utils

# NFS服务端
> mkdir -pv /root/data/nfs
> vim /etc/exports
/root/data/nfs    192.168.80.0/24(rw,no_root_squash)
> systemctl start nfs
> showmount -e localhost
```





> volume-nfs.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: volume-nfs
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
    ports:
    - containerPort: 80
    volumeMounts: 
    - name: logs-volume
      mountPath: /var/log/nginx
  - name: busybox
    image: busybox:1.30
    command: ["/bin/sh","-c","tail -f /logs/access.log"]
    volumeMounts:
    - name: logs-volume
      mountPath: /logs
  volumes:
  - name: logs-volume
    nfs:
      server: 192.168.80.100
      path: /root/data/nfs
```



```bash
> kubectl apply -f volume-nfs.yml
> cd /root/data/nfs && cat access.log
```





#### pv

```yaml
> mkdir -pv /root/data/{pv1,pv2,pv3}
> vim /etc/exports
/root/data/pv1      192.168.80.0/24(rw,no_root_squash)
/root/data/pv2      192.168.80.0/24(rw,no_root_squash)
/root/data/pv3      192.168.80.0/24(rw,no_root_squash)

> systemctl restart nfs
```



> pv.yml

```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv1
spec:
  capacity:
    storage: 1Gi
  accessModes:
  - ReadWriteMany
  persistentVolumeReclaimPolicy: Retain
  nfs:
    path: /root/data/pv1
    server: 192.168.80.100


---


apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv2
spec:
  capacity:
    storage: 2Gi
  accessModes:
  - ReadWriteMany
  persistentVolumeReclaimPolicy: Retain
  nfs:
    path: /root/data/pv2
    server: 192.168.80.100


---


apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv3
spec:
  capacity:
    storage: 3Gi
  accessModes:
  - ReadWriteMany
  persistentVolumeReclaimPolicy: Retain
  nfs:
    path: /root/data/pv3
    server: 192.168.80.100


```



```bash
> kubectl apply -f pv.yml
> kubectl get pv -o wide

```





#### pvc

> pvc.yml

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: pvc1
  namespace: dev
spec:
  accessModes:
  - ReadWriteMany
  resources:
    requests:
      storage: 1Gi

---

apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: pvc2
  namespace: dev
spec:
  accessModes:
  - ReadWriteMany
  resources:
    requests:
      storage: 1Gi

---

apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: pvc3
  namespace: dev
spec:
  accessModes:
  - ReadWriteMany
  resources:
    requests:
      storage: 1Gi

```



```bash
> kubectl get pvc -n dev

```



> pods.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod1
  namespace: dev
spec:
  containers:
  - name: busybox
    image: busybox:1.30
    command: ["/bin/sh","-c","while true; do echo pod1 >> /root/out.txt; sleep 10; done;"]
    volumeMounts:
    - name: volume
      mountPath: /root/
  volumes:
    - name: volume
      persistentVolumeClaim:
        claimName: pvc1
        readOnly: false

---

apiVersion: v1
kind: Pod
metadata:
  name: pod2
  namespace: dev
spec:
  containers:
  - name: busybox
    image: busybox:1.30
    command: ["/bin/sh","-c","while true; do echo pod2 >> /root/out.txt; sleep 10; done;"]
    volumeMounts:
    - name: volume
      mountPath: /root/
  volumes:
    - name: volume
      persistentVolumeClaim:
        claimName: pvc2
        readOnly: false

---

apiVersion: v1
kind: Pod
metadata:
  name: pod3
  namespace: dev
spec:
  containers:
  - name: busybox
    image: busybox:1.30
    command: ["/bin/sh","-c","while true; do echo pod3 >> /root/out.txt; sleep 10; done;"]
    volumeMounts:
    - name: volume
      mountPath: /root/
  volumes:
    - name: volume
      persistentVolumeClaim:
        claimName: pvc3
        readOnly: false

```



```bash
> kubectl apply -f pods.yml
```





#### ConfigMap

> configmap.yml

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: configmap
  namespace: dev
data:
  info: |
    username:admin
    password:123456
```



``` bash
> kubectl apply -f configmap.yml
> kubectl describe cm configmap -n dev
```



> pod-configmap.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-configmap
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
    volumeMounts:
    - name: config
      mountPath: /configmap/config
  volumes:
  - name: config
    configMap:
      name: configmap
```



```bash
> kubectl apply -f pod-configmap.yml
> kubectl get pod pod-configmap -n dev
> kubectl exec -it pod-configmap -n dev /bin/sh
cat /configmap/config/info
> kubectl edit cm configmap -n dev
```





#### secret

```bash

> echo -n 'admin' | base64
YWRtaW4=
> echo -n '123456' | base64
MTIzNDU2

```



> secret.yml

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: secret
  namespace: dev
type: Opaque
data:
  username: YWRtaW4=
  password: MTIzNDU2
```



```bash
> kubectl describe secret/secret -n dev

```



> pod-secret.yml

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-secret
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:1.17.1
    volumeMounts:
    - name: config
      mountPath: /secret/config
  volumes:
  - name: config
    secret:
      secretName: secret

```



```bash
> kubectl exec -it pod-secret -n dev /bin/sh
```





#### 安全认证

```bash

> cd /etc/kubernetes/pki
> (umask 077; openssl genrsa -out devman.key 2048)
> openssl req -new -key devman.key -out devman.csr -subj "/CN=devman/O=devgroup"

> openssl x509 -req -in devman.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out devman.crt -days 3650

# 3) 设置集群，用户，上下文信息
> kubectl config set-cluster kubernetes --embed-certs=true --certificate-authority=/etc/kubernetes/pki/ca.crt --server=https://192.168.80.100:6443

> kubectl config set-credentials devman --embed-certs=true --client-certificate=/etc/kubernetes/pki/devman.crt --client-key=/etc/kubernetes/pki/devman.key

> kubectl config set-context devman@kubernetes --cluster=kubernetes --user=devman

# 切换帐户到devman
> kubectl config use-context devman@kubernetes

# 查看dev下pod，发现没有权限
> kubectl get pods -n dev

# 切换到admin帐号
> kubectl config use-context kubernetes-admin@kubernetes



```



> dev-role.yml

```yaml

kind: Role
apiVersion: rbac.authorization.k8s.io/v1beta1
metadata:
  name: dev-role
  namespace: dev
rules:
- apiGroups: [""]
  resources: ["pods"]
  verbs: ["get", "watch", "list"]

---

kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1beta1
metadata:
  name: authorization-role-binding
  namespace: dev
subjects:
- kind: User
  name: devman
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: Role
  name: dev-role
  apiGroup: rbac.authorization.k8s.io


```



```bash

> kubectl apply -f dev-role.yml
> kubectl config use-context devman@kubernetes
# 现在可以拿到了，因为添加权限了
> kubectl get pod -n dev
# 切回admin帐户
> kubectl config use-context kubernetes-admin@kubernetes
```





#### DashBoard

```bash

> wget https://raw.githubusercontent.com/kubernetes/dashboard/v2.0.0/aio/deploy/recommended.yaml

> vim recommended.yml
kind: Service
apiVersion: v1
metadata:
  labels:
    k8s-app: kubernetes-dashboard
  name: kubernetes-dashboard
  namespace: kubernetes-dashboard
spec:
  type: NodePort              # 新增
  ports:
    - port: 443
      targetPort: 8443
      nodePort: 30009         # 新增
  selector:
    k8s-app: kubernetes-dashboard


```



```bash

> kubectl apply -f recommended.yml
> kubectl get pod,svc -n kubernetes-dashboard

# 创建帐号，获取token
# 1) 创建帐号
> kubectl create serviceaccount dashboard-admin -n kubernetes-dashboard
# 2） 授权
> kubectl create clusterrolebinding dashboard-admin-rb --clusterrole=cluster-admin --serviceaccount=kubernetes-dashboard:dashboard-admin
# 3) 获取帐号
> kubectl get secrets -n kubernetes-dashboard | grep dashboard-admin
# 4) 查看token
> kubectl describe secrets dashboard-admin-token-xxxxx -n kubernetes-dashboard
```


























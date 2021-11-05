```bash
> cat /etc/red-release

> vim /etc/hosts
192.168.80.100 master
192.168.80.101 node1
192.168.80.102 node2



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




> yum install -y ipset ipvsadmin
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
> kubeadm join 192.168.80.100:6443 --token htey45.21dllrindv7oan7b \
    --discovery-token-ca-cert-hash sha256:20c7bb3e62cb9d363a01bae768ac6cd7541eebbfc3f82c2b082b2515f7f13384

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




















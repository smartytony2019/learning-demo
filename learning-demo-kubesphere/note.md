## 系统配置 

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





## NFS

### 1、安装nfs-server

```bash
# 在每个机器。
yum install -y nfs-utils


# 在master 执行以下命令 
echo "/nfs/data/ *(insecure,rw,sync,no_root_squash)" > /etc/exports


# 执行以下命令，启动 nfs 服务;创建共享目录
mkdir -p /nfs/data


# 在master执行
systemctl enable rpcbind
systemctl enable nfs-server
systemctl start rpcbind
systemctl start nfs-server

# 使配置生效
exportfs -r


#检查配置是否生效
exportfs
```

### 2、配置nfs-client（选做）

```bash
showmount -e 192.168.80.45

mkdir -p /nfs/data

mount -t nfs 192.168.80.45:/nfs/data /nfs/data
```









## KubeSphere

### 1、下载KubeKey

```bash
export KKZONE=cn


curl -sfL https://get-kk.kubesphere.io | VERSION=v1.1.1 sh -

chmod +x kk
```



### 2、创建集群配置文件 

```bash
./kk create config --with-kubernetes v1.20.4 --with-kubesphere v3.1.1
```



### 3、创建集群

```bash
yum install -y conntrack
vim config-sample.yaml



./kk create cluster -f config-sample.yaml
```



### 4、查看进度

```bash
kubectl logs -n kubesphere-system $(kubectl get pod -n kubesphere-system -l app=ks-install -o jsonpath='{.items[0].metadata.name}') -f
```







## 开启 `应用商店`



> `集群管理` > `自定义资源 (CRD)` > `ClusterConfiguration ` > 编辑 `**ks-installer**`

```json
  openpitrix:
    store:
      enabled: true   #默认为false,修改为true
```



## 创建StorageClass

```yaml
## 创建了一个存储类
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: nfs-storage
  annotations:
    storageclass.kubernetes.io/is-default-class: "false"
provisioner: k8s-sigs.io/nfs-subdir-external-provisioner
parameters:
  archiveOnDelete: "true"  ## 删除pv的时候，pv的内容是否要备份

---

apiVersion: apps/v1
kind: Deployment
metadata:
  name: nfs-client-provisioner
  labels:
    app: nfs-client-provisioner
  # replace with namespace where provisioner is deployed
  namespace: default
spec:
  replicas: 1
  strategy:
    type: Recreate
  selector:
    matchLabels:
      app: nfs-client-provisioner
  template:
    metadata:
      labels:
        app: nfs-client-provisioner
    spec:
      serviceAccountName: nfs-client-provisioner
      containers:
        - name: nfs-client-provisioner
          image: registry.cn-hangzhou.aliyuncs.com/lfy_k8s_images/nfs-subdir-external-provisioner:v4.0.2
          # resources:
          #    limits:
          #      cpu: 10m
          #    requests:
          #      cpu: 10m
          volumeMounts:
            - name: nfs-client-root
              mountPath: /persistentvolumes
          env:
            - name: PROVISIONER_NAME
              value: k8s-sigs.io/nfs-subdir-external-provisioner
            - name: NFS_SERVER
              value: 192.168.80.80 ## 指定自己nfs服务器地址
            - name: NFS_PATH
              value: /nfs/data  ## nfs服务器共享的目录
      volumes:
        - name: nfs-client-root
          nfs:
            server: 192.168.80.80
            path: /nfs/data
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: nfs-client-provisioner
  # replace with namespace where provisioner is deployed
  namespace: default
---
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: nfs-client-provisioner-runner
rules:
  - apiGroups: [""]
    resources: ["nodes"]
    verbs: ["get", "list", "watch"]
  - apiGroups: [""]
    resources: ["persistentvolumes"]
    verbs: ["get", "list", "watch", "create", "delete"]
  - apiGroups: [""]
    resources: ["persistentvolumeclaims"]
    verbs: ["get", "list", "watch", "update"]
  - apiGroups: ["storage.k8s.io"]
    resources: ["storageclasses"]
    verbs: ["get", "list", "watch"]
  - apiGroups: [""]
    resources: ["events"]
    verbs: ["create", "update", "patch"]
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: run-nfs-client-provisioner
subjects:
  - kind: ServiceAccount
    name: nfs-client-provisioner
    # replace with namespace where provisioner is deployed
    namespace: default
roleRef:
  kind: ClusterRole
  name: nfs-client-provisioner-runner
  apiGroup: rbac.authorization.k8s.io
---
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-nfs-client-provisioner
  # replace with namespace where provisioner is deployed
  namespace: default
rules:
  - apiGroups: [""]
    resources: ["endpoints"]
    verbs: ["get", "list", "watch", "create", "update", "patch"]
---
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-nfs-client-provisioner
  # replace with namespace where provisioner is deployed
  namespace: default
subjects:
  - kind: ServiceAccount
    name: nfs-client-provisioner
    # replace with namespace where provisioner is deployed
    namespace: default
roleRef:
  kind: Role
  name: leader-locking-nfs-client-provisioner
  apiGroup: rbac.authorization.k8s.io

```





## 中间件部署

### MySQL 部署

#### 1、MySQL容器启动

```bash
docker run -p 3306:3306 --name mysql-01 \
-v /mydata/mysql/log:/var/log/mysql \
-v /mydata/mysql/data:/var/lib/mysql \
-v /mydata/mysql/conf:/etc/mysql/conf.d \
-e MYSQL_ROOT_PASSWORD=root \
--restart=always \
-d mysql:5.7 
```



#### 2、MySQL配置示例

```properties
[client]
default-character-set=utf8mb4
 
[mysql]
default-character-set=utf8mb4
 
[mysqld]
init_connect='SET collation_connection = utf8mb4_unicode_ci'
init_connect='SET NAMES utf8mb4'
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
skip-character-set-client-handshake
skip-name-resolve
```

> `配置中心` > `配置` 下新建 `mysql-conf` >  key： `my.cnf`  value: 如上




#### 3、MySQL部署分析

<img src="./library/image/01.png" style="zoom:80%;" />



#### 4、MySQL 添加容器

- 添加容器的时环境变量 `MYSQL_ROOT_PASSWORD=123456` 来设置密码
- 添加存储卷模板: 挂载路径为 `/var/lib/mysql` 权限为 `读写`

- 配置文件和密钥: `mysql-conf ` 路径为 `/etc/mysql/conf.d`  权限为 `只读`













### Redis部署

#### 1、redis容器启动

```bash
#创建配置文件
## 1、准备redis配置文件内容
mkdir -p /mydata/redis/conf && vim /mydata/redis/conf/redis.conf


##配置示例
appendonly yes
port 6379
bind 0.0.0.0


#docker启动redis
docker run -d -p 6379:6379 --restart=always \
-v /mydata/redis/conf/redis.conf:/etc/redis/redis.conf \
-v  /mydata/redis-01/data:/data \
 --name redis-01 redis:6.2.5 \
 redis-server /etc/redis/redis.conf
```



#### 2、redis部署分析

<img src="./library/image/02.png" style="zoom:80%;" />







### RabbitMQ部署

```bash
#docker启动rabbitmq
docker run -d -p 15672:15672 --restart=always \
-v /rabbitmq/data:/var/lib/rabbitmq/mnesia \
-v  /rabbitmq/config:/etc/rabbitmq \
 --name rabbitmq 
 rabbitmq:3.8.1-alpine
 
 
 #三个配置文件
 rabbitmq/{advanced.config, enabled_plugins，rabbitmq.conf}
 
```





### Nacos部署

#### 1、导入库

```bash
# SQL文件位置
nacos > tg-nacos.sql
```



#### 2、修改配置文件

```bash
#*************** Config Module Related Configurations ***************#
### If use MySQL as datasource:
spring.datasource.platform=mysql

### Count of DB:
db.num=1

### Connect URL of DB:
db.url.0=jdbc:mysql://tg-mysql.tg:3306/tg-nacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=30000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
db.user.0=root
db.password.0=123456
```

> application.properties



#### 3、docker启动

```bash
#docker启动nacos
docker run -d -p 8848:8848 --restart=always \
-v  /nacos/config/application.properties:/home/nacos/conf/application.properties \  #添加子路径
-e MODE=standalone
-e JVM_XMS=256m
-e JVM_XMX=256m
 --name nacos
 nacos/nacos-server:1.4.1

```



### Seata部署

#### 1、docker启动

```bash
#docker启动seata
docker run -d -p 8091:8091 --restart=always \
-v  /seata/config/registry.conf:/root/seata/config/registry.conf \  #添加子路径
-e SEATA_PORT=8091
-e STORE_MODE=file
-e SEATA_CONFIG_NAME=file:/root/seata/config/registry
 --name seata
 seataio/seata-server:1.4.0

```





### Xxl-job-admin部署

```bash
#docker启动seata
docker run -d -p 8080:8080 --restart=always \
-e PARAMS=--spring.datasource.url=jdbc:mysql://tg-mysql.tg:3306/tg_xxl_job?Unicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai --spring.datasource.username=root --spring.datasource.password=123456 --xxl.job.login.username=admin --xxl.job.login.password=123456
 --name xxl-job-admin
 xuxueli/xxl-job-admin:2.2.0

```

> 需要先导库`tg_xxl_job.sql`





### Zookeeper部署

```properties
dataDir=/data
dataLogDir=/datalog
tickTime=2000
initLimit=5
syncLimit=2
autopurge.snapRetainCount=3
autopurge.purgeInterval=0
maxClientCnxns=1000
standaloneEnabled=true
admin.enableServer=true
server.1=localhost:2888:3888;2181
4lw.commands.whitelist=*
```

> zoo.cfg配置内容



```bash
docker run -d -p 2181:2181 --restart=always \
-v  /conf/zoo.cfg:/conf/zoo.cfg \  #添加子路径
-e ZOO_MY_ID=1
 --name zookeeper
 zookeeper:3.5.9
```









### 帐号管理

#### Github帐号

```bash

凭证 ID*：github-id
类型：SSH
私钥：
-----BEGIN OPENSSH PRIVATE KEY-----
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
-----END OPENSSH PRIVATE KEY-----

密码：111qqq///.com

```



#### Harbor帐号

```bash

凭证 ID*：hub-harbor-registry
类型：帐户凭证
用户名：admin
token / 密码：Harbor12345

```



#### Kube配置文件

```bash
凭证 ID*：tg-kubeconfig
类型：kubeconfig
Content：(默认)

```



#### Harbor密钥

```
配置中心 > 密钥 > 创建

harbor-hub-secrets tg
类型：kubernetes.io/dockerconfigjson (镜像仓库密钥)

http://192.168.80.80:30002
admin
Harbor12345
```


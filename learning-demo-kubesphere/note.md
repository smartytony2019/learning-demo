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






































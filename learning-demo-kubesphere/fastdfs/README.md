



```bash

docker run -d --name tracker --net=host morunchang/fastdfs sh tracker.sh
21000 22122
vim /etc/nginx/conf/nginx.conf
vim /etc/fdfs/client.conf
vim /etc/fdfs/tracker.conf

tg-fastdfs-tracker.tg1






```







```bash


tg-fastdfs-storage.tg1

22000 23000 22122
docker run -d --name storage --net=host \
-e TRACKER_IP=192.168.19.129:22122 \
-e GROUP_NAME=group1 \
-v /home/docker/fastdfs/storage/image:/data/fast_data \
morunchang/fastdfs \
/bin/sh storage.sh \


vim /etc/nginx/conf/nginx.conf
vim /etc/fdfs/storage.conf
vim /etc/fdfs/client.conf




```


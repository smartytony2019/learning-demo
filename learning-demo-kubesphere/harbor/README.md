```bash

vim /etc/docker/daemon.json
{
  "insecure-registries" : ["192.168.80.45:30002"]
}


sudo systemctl daemon-reload
sudo systemctl restart docker
```


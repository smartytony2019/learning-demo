```bash

cat > /etc/docker/daemon.json <<EOF
{
  "insecure-registries" : [ "192.168.80.80:30002" ]
}
EOF

sudo systemctl daemon-reload
sudo systemctl restart docker
```


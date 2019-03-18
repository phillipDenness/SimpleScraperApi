#!/bin/bash

sudo apt-get -y update
sudo apt-get -y install nginx

echo "server {
    listen 80 default_server;
    listen [::]:80 default_server;
    server_name localhost;


    location / {
        proxy_set_header Host \$host;
        proxy_pass http://127.0.0.1:5000;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    }
}

server {
    listen 4445;
    server_name localhost;


    location / {
        proxy_set_header Host \$host;
        proxy_pass http://127.0.0.1:4444;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    }
}" | sudo tee /etc/nginx/sites-available/default

sudo systemctl restart nginx
sudo systemctl reload nginx


sudo apt install docker-compose
sudo usermod -aG docker azureuser

cd /home/azureuser/
sudo chmod 777 docker-compose.yml
sudo curl https://raw.githubusercontent.com/phillipDenness/SimpleScraperApi/docker/docker-compose.yml > docker-compose.yml

sudo docker container stop $(docker container ls -aq)

docker-compose -f docker-compose.yml up -d
docker-compose logs -f -t >> myDockerCompose.log &

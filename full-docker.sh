#!/usr/bin/env bash

cd $(git rev-parse --show-toplevel)
docker build  -f Dockerfile.java -t java-app .
docker build  -f Dockerfile.mysql -t mysql-server .
docker-compose up

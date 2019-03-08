#!/usr/bin/env bash

docker pull selenium/standalone-chrome
docker run -d -p 4444:4444 selenium/standalone-chrome
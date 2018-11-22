#!/bin/bash

#
# Copyright Droidefense API. All Rights Reserved.
# SPDX-License-Identifier: GNU GPL v3
#

cd "$(dirname "$0")"

source ./colors.sh

# move to project root dir from ./scripts to ./
cd ..

containerName="worker1.engine.droidefense.com"

# HELPER FUNCTIONS
# Check whether a given container (filtered by name) exists or not
function existsContainer(){
	containerName=$1
	if [ -n "$(docker ps -aq -f name=$containerName)" ]; then
	    return 0 #true
	else
		return 1 #false
	fi
}

if existsContainer $containerName; then
	docker stop $containerName && \
	docker rm $containerName
fi

log "starting engine.droidefense.com container with production docker configuration..."
docker run \
	-d \
	--name $containerName \
	--cpus="1" \
	--memory="512m" \
	-e 'PUBSUB_INTERFACE=0.0.0.0' \
	--log-driver json-file \
	--log-opt mode=non-blocking \
	--log-opt max-buffer-size=4m \
	--log-opt max-file=3 \
	--log-opt max-size=20m \
	--net="host" \
	--ulimit nofile=262144:262144 \
	engine.droidefense.com:latest

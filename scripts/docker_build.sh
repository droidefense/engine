#!/bin/bash

#
# Copyright Droidefense. All Rights Reserved.
# SPDX-License-Identifier: GNU GPL v3
#

# go to current script location
cd "$(dirname "$0")"

source ./colors.sh

# go to previous folder. from ./scripts to ./
cd ..
log "building docker engine image"
docker build -t engine.droidefense.com:latest .
ok "docker engine image built"
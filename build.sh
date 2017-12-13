#!/bin/bash

function command_exists() {
  #this should be a very portable way of checking if something is on the path
  #usage: "if command_exists foo; then echo it exists; fi"
  type "$1" &> /dev/null
}

function prerequirements(){
	
	log ' Installing prerequisites...'

	if command_exists "mvn"; then
		ok "maven already installed"
	else
		log "Installing maven..."
		log "sudo apt install -y maven"
		sudo apt install -y maven
	fi
}

function main(){

	log ' Building droidefense from current version'

	log "mvn clean package"
	mvn clean package

	ok " Building done "
}

set -e

source colors.sh

prerequirements
main

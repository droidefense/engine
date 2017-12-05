#!/bin/bash

function prerequirements(){
	echo " ########################################## "
	echo ' Installing prerequisites...'
	echo " ########################################## "

	#sudo apt install -y maven
}

function main(){

	echo " ########################################## "
	echo ' Building droidefense from current version'
	echo " ########################################## "

	mvn clean install

	echo " Building done "
}

set -e
prerequirements
main

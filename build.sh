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

	echo " ######################################## "
	echo " Creating 'droidefense' alias..."
	echo " ######################################## "

	cd $base
	version='target'
	cd $version

	jarname=$(ls *jar)
	path=$base'/'$version/$jarname
	echo $path
	echo "Creating alias"
	aliasName="'java -jar "$path"'"
	echo "alias content: " $aliasName
	cmd='alias droidefense='$aliasName

	echo "Deleting previous droidefense alias..."
	awk '!/droidefense/' ~/.bashrc > ~/.bashrc.temp && mv ~/.bashrc.temp ~/.bashrc

	echo "Updating droidefense alias..."
	echo "new alias value: "$cmd
	echo $cmd >> ~/.bashrc
	eval $cmd

	echo "Content written in ~/.bashrc"
	cat ~/.bashrc | grep droidefense

	echo " Building done "
}

set -e
prerequirements
main

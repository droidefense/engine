#!/bin/bash

function compile(){
	module=$1
	#compile module
	echo "Compiling module: $module"
	cd $base && cd mods/$1 && mvn clean install && cd $base
}

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

	export base=$(pwd)

	compile axml
	compile batch
	compile vfs
	compile ssdeep
	compile pscout
	compile entropy
	compile sdk
	compile generator
	compile manparser
	compile memapktool
	compile ml
	compile portex
	#compile mqtt
	compile simplemagic

	compile core

	echo " ######################################## "
	echo " Building Command Line Tools..."
	echo " ######################################## "

	cd $base
	cd mods/cli
	mvn clean install -P debug

	cd $base
	cd dist/debug

	echo " ######################################## "
	echo " Cleaning old files..."
	echo " ######################################## "

	rm -rf classes cli-1.0-SNAPSHOT.jar generated-sources generated-test-sources surefire-reports test-classes maven-archiver maven-status

	ls -alh

	echo " ######################################## "
	echo " Creating 'droidefense' alias..."
	echo " ######################################## "

	cd $base
	version='dist/debug'
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
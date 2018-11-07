#!/bin/bash

cd "$(dirname "$0")"

installation_path="/usr/bin/droidefense.jar"

function install_on_system(){

	base=$(pwd)

	log " Creating 'droidefense' installation..."
	version='target'
	cd $version

	jarname=$(ls *jar)

	if [ ! -f $jarname ]; then
		fail "compiled droidefense jar not found"
		exit -1
	else
		log "Installing latest version..."

		#install file to /usr/bin as droidefense
		log "cp -a $jarname $installation_path"
		sudo cp -a $jarname $installation_path

		log "chmod +x $installation_path"
		sudo chmod +x $installation_path

		cd $base

		sudo cp -a wrapper.sh /usr/bin/droidefense
		sudo chmod +x /usr/bin/droidefense
	fi
}

function create_alias(){
	targetFile=$HOME"/.bash_aliases"
	base=$(pwd)

	if [[ ! -f $targetFile ]]; then
		fail "Alias file does not exist"
		log "Creating..."
		touch $targetFile
	fi

	cd $base
	log " Creating 'droidefense' alias..."
	version='target'
	cd $version

	jarname=$(ls *jar)

	if [ ! -f $jarname ]; then
		fail "compiled droidefense jar not found"
		exit -1
	else
		path=$base'/'$version/$jarname
		log $path
		log "Creating alias"
		aliasName="'java -jar $path'"
		log "alias content: $aliasName"
		cmd='alias droidefense='$aliasName

		log "Deleting previous droidefense alias..."
		awk '!/droidefense/' $targetFile > $targetFile.temp && mv $targetFile.temp $targetFile

		log "Updating droidefense alias..."
		log "new alias value: $cmd"
		log "Writing alias to $targetFile..."
		echo $cmd >> $targetFile

		log "Enabling alias on current console session..."
		eval $cmd
		ok "Content written in $targetFile"

		bashrcData=$(cat $targetFile | grep droidefense)
		echo $bashrcData
		if [[ $bashrcData==$cmd ]]; then
			ok "Alias successfully created"
		else
			fail "Alias creation failed"
		fi
	fi
}

set -e

source colors.sh

install_on_system
#!/bin/bash

function create_alias(){

	targetFile=$HOME"/.bash_aliases"
	base=$(pwd)

	if [[ ! -f $targetFile ]]; then
		fail "Alias file does not exist"
		log "Creating..."
		touch $targetFile
	fi

	log " Creating 'droidefense' alias..."
	version='target'
	cd $version

	jarname=$(ls *jar)

	if [ ! -f $jarname ]; then
		fail "compiled droidefense jar not found"
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

create_alias

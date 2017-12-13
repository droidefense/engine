#!/bin/bash

#custom terminal colores
bold_red=`tput bold; tput setaf 1`
bold_green=`tput bold; tput setaf 2`
bold_yellow=`tput bold; tput setaf 3`
bold_blue=`tput bold; tput setaf 4`
bold_magenta=`tput bold; tput setaf 5`
bold_cyan=`tput bold; tput setaf 6`
bold_white=`tput bold; tput setaf 7`

normal_red=`tput setaf 1`
normal_green=`tput setaf 2`
normal_yellow=`tput setaf 3`
normal_blue=`tput setaf 4`
normal_magenta=`tput setaf 5`
normal_cyan=`tput setaf 6`
normal_white=`tput setaf 7`

reset=`tput sgr0`

function fail(){
	echo "${bold_red}[ FAIL ] $1${reset}"
}

function ok(){
	echo "${bold_green}[ SUCCESS ] $1${reset}"
}

function log(){
	echo "${bold_yellow}[ LOG ] $1${reset}"
}

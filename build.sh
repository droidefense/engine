#!/bin/sh
echo " ########################################## "
echo ' Building droidefense from current version'
echo " ########################################## "

base=$(pwd)

cd mods/core
mvn clean install

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
#!/bin/sh
echo " ########################################## "
echo ' Building droidefense from current version'
echo " ########################################## "

cd mods/core
mvn clean install

echo " ######################################## "
echo " Building Command Line Tools..."
echo " ######################################## "

cd ../cli
mvn clean install -P debug
cd  ../../dist/debug

echo " ######################################## "
echo " Cleaning old files..."
echo " ######################################## "

rm -rf classes cli-1.0-SNAPSHOT.jar generated-sources generated-test-sources surefire-reports test-classes maven-archiver maven-status

ls -alh

echo " ######################################## "
echo " Creating 'droidefense' alias..."
echo " ######################################## "

now=$(pwd)
jarname=$(ls *jar)
echo $now/$jarname
echo "Creating alias"
aliasName="'java -jar "$now"/"$jarname"'"
echo "ALIAS: " $aliasName
cmd='alias droidefense='$aliasName

echo "Deleting previous droidefense alias..."
awk '!/droidefense/' ~/.bashrc > ~/.bashrc.temp && mv ~/.bashrc.temp ~/.bashrc

echo "Updating droidefense alias..."
echo $cmd >> ~/.bashrc

echo "Content written in ~/.bashrc"
cat ~/.bashrc | grep droidefense

echo " Building done "
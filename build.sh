#!/bin/sh
echo " ########################################## "
echo ' Building droidefense from current version'
echo " ########################################## "

cd mods/core
mvn clean install
cd ../mods/cli

echo " ######################################## "
echo " Building Command Line Tools..."
echo " ######################################## "

mvn clean install -P debug
cd  ../../dist/debug

echo " ######################################## "
echo " Cleaning old files..."
echo " ######################################## "

rm -rf classes cli-1.0-SNAPSHOT.jar generated-sources maven-archiver maven-status
cd dist/debug

ls -alh

echo " ######################################## "
echo " Creating 'droidefense' alias..."
echo " ######################################## "

now=$(pwd)
echo $now/droidefense-cli-1.0-SNAPSHOT.jar
echo "Creating alias"
aliasName="'java -jar "$now"/droidefense-cli-1.0-SNAPSHOT.jar'"
echo "ALIAS: " $aliasName
cmd='alias droidefense='$aliasName

echo "Deleting previous droidefense alias..."
awk '!/droidefense/' ~/.bashrc > ~/.bashrc.temp && mv ~/.bashrc.temp ~/.bashrc

echo "Updating droidefense alias..."
echo $cmd >> ~/.bashrc

echo "Content written in ~/.bashrc"
cat ~/.bashrc | grep droidefense

echo " Building done "
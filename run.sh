#!/bin/sh

base=$(pwd)

cd target

jarname=$(ls *jar)

echo "Running: " $jarname
java -jar $jarname
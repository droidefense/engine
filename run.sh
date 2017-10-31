#!/bin/sh

base=$(pwd)

cd dist/debug

jarname=$(ls *jar)

echo "Running: " $jarname
java -jar $jarname
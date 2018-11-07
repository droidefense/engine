#!/bin/sh

cd "$(dirname "$0")"

jarname=$(ls *jar)

echo "Running: " $jarname
java -jar $jarname
#!/bin/sh

# go to current script location
cd "$(dirname "$0")"

# go to previous folder. from ./scripts to ./
cd ..

jarname=$(ls *jar)

echo "Running: " $jarname
java -jar $jarname
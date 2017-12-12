#!/bin/bash

echo "Downloading latest version of apktool"
if [[ ! -d Apktool ]]; then
	git clone https://github.com/iBotPeaches/Apktool
fi
base=$(pwd)
cd Apktool
echo cp -ra $base'/Apktool/brut.j.util/src/main/java' $base'/mods/memapktool/src/main/java/' 

#remove temp files
rm -rf Apktool
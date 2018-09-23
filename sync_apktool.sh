#!/bin/bash

cd "$(dirname "$0")"

echo "Downloading latest version of apktool"
if [[ ! -d Apktool ]]; then
	git clone https://github.com/iBotPeaches/Apktool
fi
base=$(pwd)
cd Apktool

echo "Syncing files..."

echo "	Syncing util..."
echo cp -ra $base'/Apktool/brut.j.util/src/main/java/.' $base'/mods/memapktool/src/main/java/' 
cp -ra $base'/Apktool/brut.j.util/src/main/java/.' $base'/mods/memapktool/src/main/java/' 

echo "	Syncing dir..."
echo cp -ra $base'/Apktool/brut.j.dir/src/main/java/.' $base'/mods/memapktool/src/main/java/' 
cp -ra $base'/Apktool/brut.j.dir/src/main/java/.' $base'/mods/memapktool/src/main/java/' 

echo "	Syncing common..."
echo cp -ra $base'/Apktool/brut.j.common/src/main/java/.' $base'/mods/memapktool/src/main/java/' 
cp -ra $base'/Apktool/brut.j.common/src/main/java/.' $base'/mods/memapktool/src/main/java/' 

echo "	Syncing apktool-lib..."
echo cp -ra $base'/Apktool/brut.apktool/apktool-lib/src/main/java/.' $base'/mods/memapktool/src/main/java'
cp -ra $base'/Apktool/brut.apktool/apktool-lib/src/main/java/.' $base'/mods/memapktool/src/main/java/' 

echo "	Syncing apktool-lib resources..."
echo cp -ra $base'/Apktool/brut.apktool/apktool-lib/src/main/resources/.' $base'/mods/memapktool/src/main/resources'
cp -ra $base'/Apktool/brut.apktool/apktool-lib/src/main/resources/.' $base'/mods/memapktool/src/main/resources'

echo "	Syncing apktool-lib test resources..."
echo cp -ra $base'/Apktool/brut.apktool/apktool-lib/src/test/resources/.' $base'/mods/memapktool/src/test/resources'
cp -ra $base'/Apktool/brut.apktool/apktool-lib/src/test/resources/.' $base'/mods/memapktool/src/test/resources'

echo "	Syncing apktool-lib test files..."
echo cp -ra $base'/Apktool/brut.apktool/apktool-lib/src/test/java/.' $base'/mods/memapktool/src/test/java'
cp -ra $base'/Apktool/brut.apktool/apktool-lib/src/test/resources/.' $base'/mods/memapktool/src/main/resources'

echo "	Syncing apktool-cli..."
cp -ra $base'/Apktool/brut.apktool/apktool-cli/src/main/java/brut/apktool/.' $base'/mods/memapktool/src/main/java/brut/apktool'

#remove temp files
echo rm -rf $base/Apktool
rm -rf $base/Apktool

echo "apktool sync"
echo "done"
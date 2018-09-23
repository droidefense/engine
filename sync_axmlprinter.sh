#!/bin/bash

cd "$(dirname "$0")"

echo "Downloading latest version of axmlprinter"
cloneFolder=rednaga_axmlprinter
targetFolder=axmlprinter
if [[ ! -d $cloneFolder ]]; then
	git clone https://github.com/rednaga/axmlprinter $cloneFolder
fi
base=$(pwd)
cd $cloneFolder

echo "Deleting old files..."

oldFilesFolder="$base/mods/$targetFolder/"
if [[ -d $oldFilesFolder ]]; then
	echo "rm -rf $oldFilesFolder"
	rm -rf $oldFilesFolder
else
	mkdir -p $oldFilesFolder
fi

echo "Syncing files..."

echo "	Syncing src..."
if [[ ! -d $base/mods/$targetFolder/src/main/ ]]; then
	mkdir -p $base/mods/$targetFolder/src/main/
fi
cmd="cp -ra $base/$cloneFolder/src/main/. $base/mods/$targetFolder/src/main/"
echo $cmd
$cmd

echo "	Syncing tests..."
if [[ ! -d $base/mods/$targetFolder/src/test/ ]]; then
	mkdir -p $base/mods/$targetFolder/src/test/
fi
cmd="cp -ra $base/$cloneFolder/src/test/. $base/mods/$targetFolder/src/test/"
echo $cmd
$cmd

#remove temp files
echo "removing temp files..."
echo rm -rf $base/$cloneFolder
rm -rf $base/$cloneFolder

cd  $base
git checkout mods/axmlprinter/axmlprinter.iml
git checkout mods/axmlprinter/pom.xml

echo "axmlprinter repository files updated"
echo "done"
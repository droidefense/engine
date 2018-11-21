#!/bin/sh

# go to current script location
cd "$(dirname "$0")"

# go to previous folder. from ./scripts to ./
cd ..

echo 'Updating remote content with new local push'
git add .
echo 'Adding new commit...'
message=\'$1\'
echo 'Commit message: ' $message
git commit -m $message
echo 'Pushing changes...'
git push
echo 'DONE'
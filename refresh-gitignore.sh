#!/bin/sh
echo 'Updating remote content with new local .gitignore configuration'
git rm -r --cached .
git add .
echo 'Adding new commit...'
git commit -m '[refresh-gitignore.sh] .gitignore conditions updated and working'
echo 'Pushing changes...'
git push
echo 'DONE'

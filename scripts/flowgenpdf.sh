#!/bin/sh

# go to current script location
cd "$(dirname "$0")"

# go to previous folder. from ./scripts to ./
cd ..

echo 'Generating Flowmap pdf...'
dot -Tps2 app-debug.dot > intermediate.ps2
ps2pdf intermediate.ps2 flowmap_graph.pdf
echo 'done'
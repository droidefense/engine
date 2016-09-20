#!/bin/sh
echo 'Generating Flowmap pdf...'
dot -Tps2 app-debug.dot > intermediate.ps2
ps2pdf intermediate.ps2 flowmap_graph.pdf
echo 'done'
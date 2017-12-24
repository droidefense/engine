package droidefense.rulengine.map;

import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.rulengine.base.AbstractAtomNode;
import droidefense.rulengine.base.AbstractFlowMap;
import droidefense.rulengine.base.NodeConnection;
import droidefense.rulengine.nodes.EntryPointNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BasicCFGFlowMap extends AbstractFlowMap {

    //config
    private boolean optimized;

    //list of node names = identifiers

    private transient HashMap<String, Integer> nodeIdentifierList;

    public BasicCFGFlowMap() {
        connectionMap = new HashMap<>();
        nodeMap = new HashMap<>();
        //avoid repetition of nodes
        nodeIdentifierList = new HashMap<>();
        nodes = 0;
        connections = 0;
        //remove init methods as default
        this.setOptimized(false);
    }

    public AbstractAtomNode addNode(AbstractAtomNode d) {
        //get node name from identifier list
        Integer nodeId = nodeIdentifierList.get(d.getNodeLabel());
        if (nodeId == null) {
            //add new
            nodeMap.put(d.getId(), d);
            //add node name to node identifier list
            nodeIdentifierList.put(d.getNodeLabel(), d.getId());
            nodes++;
            //return just created node
            return d;
        } else {
            //return preivously created node. avoid node duplication
            return nodeMap.get(nodeId);
        }
    }

    public int getNodeCount() {
        return nodeMap.size();
    }

    public void addConnection(NodeConnection conn) {

        NodeConnection con = connectionMap.get(conn.getId());
        if (con == null) {
            //add new
            connectionMap.put(conn.getId(), conn);
            connections++;
        } else {
            //update connection weight
            con.increaseWeight(1);
        }
    }

    public int getConnectionsCount() {
        return connectionMap.size();
    }

    public AbstractAtomNode getNode(int id) {
        return nodeMap.get(id);
    }

    public boolean isOptimized() {
        return optimized;
    }

    public void setOptimized(boolean optimized) {
        this.optimized = optimized;
    }

    public String getAsDotGraph() {
        if (nodes == 0)
            return "";
        Log.write(LoggerType.TRACE, "Generating .dot graph...");
        String begin = "digraph flowmap{\n";
        begin += "\trankdir=LR\n";
        begin += "\tnodesep=0.5 // increases the separation between nodes\n";
        begin += "\tnode[shape=none]";
        String connections = "";
        String end = "}";

        //draw connections
        Iterator it = connectionMap.entrySet().iterator();
        ArrayList<AbstractAtomNode> alreadyPrinted = new ArrayList<>();
        while (it.hasNext()) {
            //get pair
            Map.Entry pair = (Map.Entry) it.next();
            NodeConnection conn = (NodeConnection) pair.getValue();

            if (isOptimized()) {
                //optimized graph
                boolean drawable = conn.isDrawable();
                if (drawable) {
                    if (conn.getSource() instanceof EntryPointNode) {
                        //draw connection if destination node is orphane
                        if (conn.getDestination().hasNoParent()) {
                            //draw
                            connections += conn.getAsDotGraph();
                            if (!alreadyPrinted.contains(conn.getSource()))
                                alreadyPrinted.add(conn.getSource());
                            if (!alreadyPrinted.contains(conn.getDestination()))
                                alreadyPrinted.add(conn.getDestination());
                        }
                    } else {
                        connections += conn.getAsDotGraph();
                        if (!alreadyPrinted.contains(conn.getSource()))
                            alreadyPrinted.add(conn.getSource());
                        if (!alreadyPrinted.contains(conn.getDestination()))
                            alreadyPrinted.add(conn.getDestination());
                    }
                }
            } else {
                //no optimized graph
                alreadyPrinted.add(conn.getSource());
                alreadyPrinted.add(conn.getDestination());
                connections += conn.getAsDotGraph();
            }
        }

        //add entry points in the same level
        connections += "\t{rank = same;";
        for (EntryPointNode e : EntryPointNode.getList())
            connections += e.getId() + "; ";
        connections += "}\n";

        //draw nodes
        if (isOptimized()) {
            for (AbstractAtomNode n : alreadyPrinted) {
                if (n.isDrawable() && !n.isOrphane() && alreadyPrinted.contains(n)) {
                    connections += n.getAsDotGraph();
                }
                connections += n.getAsDotGraph();
            }
        } else {
            for (AbstractAtomNode n : alreadyPrinted) {
                connections += n.getAsDotGraph();
            }
        }
        connections += "\n";

        Log.write(LoggerType.TRACE, "Graph generated!!");
        return begin + connections + end;
    }
}

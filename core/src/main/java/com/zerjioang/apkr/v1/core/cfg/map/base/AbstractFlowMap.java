package com.zerjioang.apkr.v1.core.cfg.map.base;

import com.zerjioang.apkr.v1.core.cfg.NodeConnection;
import com.zerjioang.apkr.v1.core.cfg.base.AbstractAtomNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sergio on 22/5/16.
 */
public abstract class AbstractFlowMap implements Serializable {

    protected int maxNodes;
    protected int nodes;
    protected int connections;


    protected HashMap<Integer, AbstractAtomNode> nodeMap;

    protected HashMap<Integer, NodeConnection> connectionMap;

    public abstract AbstractAtomNode addNode(AbstractAtomNode d);

    public abstract AbstractAtomNode getNode(int id);

    public abstract int getNodeCount();

    public abstract void addConnection(NodeConnection conn);

    public abstract int getConnectionsCount();

    public abstract String getAsDotGraph();

    public int getMaxNodes() {
        return maxNodes;
    }

    public void setMaxNodes(int maxNodes) {
        this.maxNodes = maxNodes;
    }

    public int getNodes() {
        return nodes;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    public int getConnections() {
        return connections;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

    public ArrayList<AbstractAtomNode> getNodeList() {
        return new ArrayList<AbstractAtomNode>(nodeMap.values());
    }

    public ArrayList<NodeConnection> getConnectionList() {
        return new ArrayList<NodeConnection>(connectionMap.values());
    }
}

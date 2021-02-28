package com.adam.practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/*
Calculate minimum number of hops from source node to all nodes on map.  If node cannot be visited, output should say Inf
Input:
Line 1 - Source to calculate from
Line 2 - ; delimited list of nodes neighboring each other
A
A,B;A,C;B,D;C,E;F,H;D,E;D,I

Visual Representation of Graph (note: it is disjointed, source may not have path to all nodes)
A-B-D-I  F-H
|  /
| E
|/
C

Output:
Line by line of node name and hops to get to node from source, print Inf if you cannot get to node from source
A 0
B 1
C 1
D 2
E 2
F Inf
H Inf
I 3
*/
public class SourcePathLength {

    interface Node {
        String getName();
        void addNeighbor(Node node);
        boolean isNeighbor(Node node);
        boolean hasSameNeighbor(Node node);
        Set<Node> getNeighborNodes();
    }

    interface Graph{
        void createSourceMap(Node newNode) ;
        boolean nodeInSet(Node node);
        List<Node> getPreviousNodes(Node node);
        String distanceToNode(Node destinationNode);
    }

    private static Map<String, Node> allNodes = new HashMap<>();
    private static final Graph sourceGraph = createGraphOfSourceConnectedNodes();

    /**
     * Iterate through each line of input.
     * Create node objects with all neighbors
     * Create graph with only part that connects to source
     * Print output
     */
    public static void main(String[] args) throws IOException {

        try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))){
            String sourceNodeName = in.readLine();
            String rawGraph = in.readLine();
            in.close();

            allNodes.put(sourceNodeName, createNodes(sourceNodeName));
            Arrays
                    .stream(rawGraph.split(";"))
                    .forEach(SourcePathLength::putInRawNodePair);

            //Heavily relies on input always having pairs placed in certain order
            sourceGraph.createSourceMap(allNodes.get(sourceNodeName));
            allNodes.keySet().forEach(r -> System.out.println(r + " " + sourceGraph.distanceToNode(allNodes.get(r))));

        }catch (IOException ex){
            ex.printStackTrace();
        }

    }

    private static void putInRawNodePair(String nodePair){
        String[] sourceNeighborPair = nodePair.split(",");
        Node currentNode = allNodes.getOrDefault(sourceNeighborPair[0], createNodes(sourceNeighborPair[0]));
        Node nextNode = allNodes.getOrDefault(sourceNeighborPair[1], createNodes(sourceNeighborPair[1]));
        currentNode.addNeighbor(nextNode);

        allNodes.putIfAbsent(sourceNeighborPair[0], currentNode);
        allNodes.putIfAbsent(sourceNeighborPair[1], nextNode);
    }

    private static Node createNodes(String nodeName){
        return new Node(){
            String name = nodeName;
            Set<Node> nextNodes = new HashSet<>();

            @Override
            public String getName() {
                return name;
            }

            @Override
            public void addNeighbor(Node node){
                nextNodes.add(node);
            }

            @Override
            public boolean isNeighbor(Node node){
                return nextNodes.contains(node);
            }

            @Override
            public boolean hasSameNeighbor(Node node){
                return node.getNeighborNodes().stream().anyMatch(nextNodes::contains);
            }

            @Override
            public Set<Node> getNeighborNodes(){
                return nextNodes;
            }

            @Override
            public boolean equals(Object node){
                return node.getClass().equals(this.getClass()) && this.getName().equals(((Node)node).getName());
            }

            @Override
            public int hashCode(){
                return this.getName().hashCode();
            }
        };
    }

    private static Graph createGraphOfSourceConnectedNodes() {
        return new Graph() {
            final List<Node> setsOfConnectedNodesInGraph = new ArrayList<>();
            Node sourceNode;

            @Override
            public void createSourceMap(Node newNode) {
                sourceNode = newNode;
                this.createSourceMapHelper(newNode);
            }

            private void createSourceMapHelper(Node newNode) {
                setsOfConnectedNodesInGraph.add(newNode);
                newNode.getNeighborNodes().forEach(this::createSourceMapHelper);
            }

            @Override
            public boolean nodeInSet(Node node) {
                return setsOfConnectedNodesInGraph.contains(node);
            }

            @Override
            public List<Node> getPreviousNodes(Node node) {
                return setsOfConnectedNodesInGraph.stream().filter(r -> r.isNeighbor(node)).collect(Collectors.toList());
            }

            @Override
            public String distanceToNode(Node destinationNode) {
                int distanceToNode = distanceToNodeHelper(destinationNode);
                return distanceToNode == -1 ? "Inf" : Integer.toString(distanceToNode);
            }

            private int distanceToNodeHelper(Node destinationNode){
                if (sourceNode.equals(destinationNode)) {
                    return 0;
                }
                if (sourceNode.isNeighbor(destinationNode)) {
                    return 1;
                }
                if (!nodeInSet(destinationNode)) {
                    return -1;
                }

                return getPreviousNodes(destinationNode).stream().mapToInt(r -> 1 + distanceToNodeHelper(r)).min().orElse(-1);
            }

        };
    }
}

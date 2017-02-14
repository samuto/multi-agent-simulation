package org.socialforce.strategy.impl;

import org.socialforce.geom.Point;
import org.socialforce.geom.Shape;
import org.socialforce.geom.impl.Point2D;
import org.socialforce.geom.impl.Segment2D;
import org.socialforce.model.InteractiveEntity;
import org.socialforce.model.impl.Entity;
import org.socialforce.model.impl.Wall;
import org.socialforce.scene.Scene;
import org.socialforce.strategy.DynamicStrategy;
import org.socialforce.strategy.PathFinder;

import java.io.File;
import java.util.*;

/**
 * Created by sunjh1999 on 2017/1/28.
 * BEST STRATEGY
 */
public class CompoundECStrategy extends ECStrategy implements DynamicStrategy {
    LinkedList<Gate> gates = new LinkedList<>();
    int regionNum;
    LinkedList<Tree<String>> paths = new LinkedList<>();
    Graph<String> graph = new Graph<>();  //图的邻接表
    Fields fields = new Fields();

    public CompoundECStrategy(Scene scene, PathFinder pathFinder){
        super(scene, pathFinder);
        gates.addLast(new Gate(new Segment2D(new Point2D(-0.5,1), new Point2D(-0.5,2)), "A"));
        gates.addLast(new Gate(new Segment2D(new Point2D(3.5,1), new Point2D(3.5,2)), "B"));
        gates.addLast(new Gate(new Segment2D(new Point2D(4,7), new Point2D(5,7)), "C"));
        for(Gate gate:gates){
            gate.setScene(scene);
            gate.setShape(((Segment2D)gate.getShape()).flatten(1));
            scene.getStaticEntities().add(gate);
        }
        graph.combine("A", "B");
        graph.combine("C", "B");
        //graph.combine("D", "C");
        //graph.combine("A", "D");
        initMaps();
        setPaths("A");
        setPaths("B");
    }

    public void initMaps(){
        for(Gate gate:gates){
            for(String target:graph.find(gate.getName())){
                Gate t = getGate(target);
                Scene newScene = prepareScene(gate, t);
                pathFinder.setScene(newScene, gate.getShape().getReferencePoint());
                fields.addMap(((AStarPath)pathFinder.plan_for(gate.getShape().getReferencePoint())).map, gate.getName(), t.getName());

            }
        }

    }

    private Gate getGate(String name){
        for(Gate gate:gates){
            if(gate.getName().equals(name)) return gate;
        }
        return null;
    }

    private Scene prepareScene(Gate gate, Gate toAvoid){
        Scene newScene = scene.standardclone();
        for(String target:graph.find(gate.getName())){
            Gate t = getGate(target);
            if(t.getShape() instanceof Segment2D && !t.equals(toAvoid)) newScene.addStaticEntity(new Wall(((Segment2D)t.getShape()).flatten(0.6)));
        }
        return newScene;
    }

    @Override
    public void dynamicDecision() {

    }

    @Override
    public void pathDecision() {

    }

    public void setPaths(String target){
        Tree<String> path = new Tree<>();
        Stack<Node<String>> candidates = new Stack<>();
        Node<String> root = new Node<>(target);
        candidates.push(root);
        path.setRoot(root);   //设置目标点
        while(!candidates.empty()){
            Node<String> candidate = candidates.pop();
            LinkedList<Node<String>> onPath = path.findPathByNode(candidate); //找路径上存在过的点
            for(String name: graph.find(candidate.getData())){
                boolean succ = true;
                for(Node<String> nodeOnPath: onPath){
                    if(nodeOnPath.getData().equals(name)){
                        succ = false;
                        break;
                    }
                }
                if(succ){
                    Node<String> newNode = new Node<>(name);
                    path.addNode(newNode, candidate);
                    candidates.push(newNode);
                }
            }
        }
        paths.addLast(path);
    }

    private class Gate extends Entity{
        boolean isExit = false;
        public Gate(Shape shape, String name){
            super(shape);
            setName(name);
            if(shape instanceof Point) isExit = true;
        }

        public boolean isExit(){ return isExit;}

        @Override
        public void affect(InteractiveEntity affectedEntity) {

        }

        @Override
        public double getMass() {
            return 0;
        }

        @Override
        public InteractiveEntity standardclone() {
            return new Gate(shape.clone(), name);
        }
    }

    private class Fields{
        private LinkedList<AStarPathFinder.Maps> mapSet = new LinkedList<>();
        private LinkedList<String> startPoints, endPoints;
        public Fields(){
            startPoints = new LinkedList<>();
            endPoints = new LinkedList<>();
        }
        public void addMap(AStarPathFinder.Maps map, String start, String end){
            mapSet.addLast(map);
            startPoints.addLast(start);
            endPoints.addLast(end);
        }
    }

    public class Node<T> {
        private T data = null;
        private Node<T> parent = null;
        private LinkedList<Node<T>> childs = new LinkedList<>();

        public Node(T data){  //根节点
            this.data = data;
            this.parent = this;
        }

        public Node(T data, Node<T> parent){
            this.data = data;
            this.parent = parent;
            parent.setChild(this);
        }

        public T getData(){
            return this.data;
        }

        public void setParent(Node<T> parent){
            this.parent = parent;
            parent.setChild(this);
        }

        public Node<T> getParent(){
            return this.parent;
        }

        public boolean isRoot(){ return this.parent == this;}

        private void setChild(Node<T> child){
            this.childs.addLast(child);
        }

        public LinkedList<Node<T>> getChildren(){
            return this.childs;
        }

        public boolean isLeaf(){ return this.childs.size() == 0; }
    }

    public class Tree<T>{    //实际上只是对Nodes提供方法约束 同时储存了所有路径 根Node本身就可以表示一棵树
        LinkedList<Node<T>> nodes = new LinkedList<>();
        public Tree(){}
        public Tree(Node<T> node){
            nodes.addLast(node);
        }

        public void setRoot(Node<T>node){
            nodes.add(0,node);
        }

        public Node<T> getRoot(){ return nodes.getFirst();}

        public boolean addNode(Node<T> node, Node<T> parent){
            for(Node<T> aNode:nodes){
                if(aNode == parent){
                    node.setParent(aNode);
                    nodes.addLast(node);
                    return true;
                }
            }
            return false;
        }

        public LinkedList<Node<T>> findPathByNode(Node<T> node){
            if(!nodes.contains(node)) return null;
            LinkedList<Node<T>> Path = new LinkedList<>();
            Path.addLast(node);
            while(!node.isRoot()){
                node = node.getParent();
                Path.addLast(node);
            }
            return Path;
        }

        public LinkedList<Node<T>> findNodesByData(T data){
            LinkedList<Node<T>> nodeWithData = new LinkedList<>();
            nodes.stream().filter(aNode -> aNode.getData().equals(data)).forEach(nodeWithData::addLast);
            return nodeWithData;
        }

        public String toString(){
            LinkedList<Node<T>> leaves = new LinkedList<>();
            String output = "";
            nodes.stream().filter(Node::isLeaf).forEach(leaves::addLast);
            for(Node<T> leaf:leaves){
                Node<T> present = leaf;
                output += present.getData().toString();
                while(!present.isRoot()){
                    present = present.getParent();
                    output +="->"+ present.getData().toString();
                }
                output += "\n";
            }
            return output;
        }
    }

    public class Graph<T> extends HashMap<T, List<T>> {  //储存图的邻接表

        private static final long serialVersionUID = 1L;

        public void combine(T key, T value) {
            if (!super.containsKey(key)) {
                super.put(key, new Vector<T>());
            }
            List<T> list = get(key);
            if (list.contains(value)) return;
            list.add(value);
            /*value作为key再来一次*/
            combine(value, key);
        }

        public List<T> find(T key) {
            return super.get( key ) ;
        }

    }

    public String toString(){
        String output = "";
        for(Tree<String> path: paths){
            output += path.toString();
        }
        return output;
    }
}
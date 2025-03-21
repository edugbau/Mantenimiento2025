package org.mps.tree;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class BinarySearchTree<T> implements BinarySearchTreeStructure<T> {
    private Comparator<T> comparator;
    private T value;
    private BinarySearchTree<T> left;
    private BinarySearchTree<T> right;

    public String render(){
        String render = "";

        if (value != null) {
            render += value.toString();
        }

        if (left != null || right != null) {
            render += "(";
            if (left != null) {
                render += left.render();
            }
            render += ",";
            if (right != null) {
                render += right.render();
            }
            render += ")";
        }

        return render;
    }

    public BinarySearchTree(Comparator<T> comparator) {
        // TODO
    }

    @Override
    public void insert(T value) {
        // TODO
    }

    @Override
    public boolean isLeaf() {
        // TODO
        return false;
    }

    @Override
    public boolean contains(T value) {
        // TODO
        return false;
    }

    @Override
    public T minimum() {
        // TODO
        return null;
    }

    @Override
    public T maximum() {
        // TODO
        return null;
    }

    @Override
    public void removeBranch(T value){
        // TODO
    }

    @Override
    public int size() {
        //TODO
        return 0;
    }

    @Override
    public int depth() {
        // TODO
        return 0;
    }

    // Complex operations
    // (Estas operaciones se incluirán más adelante para ser realizadas en la segunda
    // sesión de laboratorio de esta práctica)
}

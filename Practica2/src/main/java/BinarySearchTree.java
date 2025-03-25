/*
Integrantes:
Eduardo González Bautista
Juan Manuel Valenzuela González
 */

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
        this.comparator = comparator;
        this.value = null;
        this.left = null;
        this.right = null;
    }

    @Override
    public void insert(T value) {
        this.value = value;
    }

    @Override
    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }

    @Override
    public boolean contains(T value) {
        if(value == null){
            throw new BinarySearchTreeException("El valor no puede ser nulo.");
        }
        if (this.isLeaf()) {
            return this.value.equals(value);
        } else if (this.value.equals(value)) {
            return true;
        } else if (comparator.compare(this.value, value) < 0) {
            return this.left != null && this.left.contains(value);
        } else {
            return this.right != null && this.right.contains(value);
        }
    }

    @Override
    public T minimum() {
        if(this.left == null){
            return this.value;
        } else {
            return this.left.minimum();
        }
    }

    @Override
    public T maximum() {
        if (this.right == null){
            return this.value;
        } else {
            return this.right.maximum();
        }
    }

    @Override
    public void removeBranch(T value){ // no hay que comprobar si el valor es nulo ya que ya lo hace el contains
        if  (!this.contains(value)){ // esto va a tener que ser cambiado
            throw new BinarySearchTreeException("El valor no se encuentra en el árbol");
        } else {
            removeBranchRecursive(value);
        }
    }

    private void removeBranchRecursive(T value){
        if(this.value.equals(value)){
            this.delete();
        } else if(this.left != null && comparator.compare(this.value, value) < 0){
            this.left.removeBranch(value);
        } else {
            this.right.removeBranch(value);
        }
    }

    private void delete(){
        if(this.left != null){
            this.left.delete();
        }
        if(this.right != null){
            this.right.delete();
        }
        this.value = null;
        this.left = null;
        this.right = null;
    }

    @Override
    public int size() {
        int n = 1;
        if (this.left != null) {
            n += this.left.size();
        }
        if (this.right != null) {
            n += this.right.size();
        }
        return n;
    }

    @Override
    public int depth() {
        int leftDepth = 0, rightDepth = 0;
        if (this.left != null) {
            leftDepth = this.left.depth();
        }
        if (this.right != null) {
            rightDepth = this.right.depth();
        }
        return 1 + Integer.max(leftDepth, rightDepth);
    }

    // Complex operations
    // (Estas operaciones se incluirán más adelante para ser realizadas en la segunda
    // sesión de laboratorio de esta práctica)
}

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
        if (comparator == null) {
            throw new BinarySearchTreeException("El comparador no puede ser nulo.");
        }
        this.comparator = comparator;
        this.value = null;
        this.left = null;
        this.right = null;
    }

    @Override
    public void insert(T value) { // los duplicados los va a insertar a la derecha
        if(this.value == null){
            this.value = value;
        } else {
            if (comparator.compare(value, this.value) < 0) {
                if (this.left == null) {
                    this.left = new BinarySearchTree<>(this.comparator);
                }
                this.left.insert(value);

            } else {
                if (this.right == null) {
                    this.right = new BinarySearchTree<>(this.comparator);
                }
                this.right.insert(value);
            }
        }
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
        if (this.value == null) {
            return false;
        }
        else if (this.value.equals(value)) {
            return true;
        } else if (comparator.compare(value, this.value) < 0) {
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
        if  (!this.contains(value)){
            throw new BinarySearchTreeException("El valor no se encuentra en el árbol");
        }
        removeBranchRecursive(value,false,null);

    }

    private void removeBranchRecursive(T value,boolean left,BinarySearchTree<T> parent){
        if (this.left != null && comparator.compare(value, this.value) < 0){
            this.left.removeBranchRecursive(value,true,this);
        } else if(this.right != null && comparator.compare(value, this.value) > 0){
            this.right.removeBranchRecursive(value,false,this);
        } else {
            this.delete(left,parent); // hay que borrar el arbol hasta abajo
        }
    }

    private void delete(boolean left,BinarySearchTree<T> parent){
        if(this.left != null){
            this.left.delete(true,this);
        }
        if(this.right != null){
            this.right.delete(false,this);
        }
        if (parent != null) { // Si no es la raíz
            if (left) { // Si es el hijo izquierdo se va a tener que eliminar desde el padre
                parent.left = null;
            } else {
                parent.right = null;
            }
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

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
        if(value == null){
            throw new BinarySearchTreeException("El valor no puede ser nulo.");
        }
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
        return this.left == null && this.right == null && this.value != null;
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
        if (this.value == null) {
            throw new BinarySearchTreeException("El árbol no puede ser vacío.");
        }
        return this.minimumRecursive();
    }

    private T minimumRecursive(){
        if(this.left == null){
            return this.value;
        } else {
            return this.left.minimum();
        }
    }


    @Override
    public T maximum() {
        if(this.value == null){
            throw new BinarySearchTreeException("El árbol no puede estar vacío");
        }
        return this.maximumRecursive();
    }
    private T maximumRecursive(){
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
        if(value == null){
            return 0;
        }
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
        if(value == null){
            return 0;
        }
        int leftDepth = 0, rightDepth = 0;
        if (this.left != null) {
            leftDepth = this.left.depth();
        }
        if (this.right != null) {
            rightDepth = this.right.depth();
        }
        return 1 + Integer.max(leftDepth, rightDepth);
    }

    @Override
    public void removeValue(T value) {
        if(!this.contains(value)){ // no hay que comprobar que value sea null porque lo hace le contains
            throw new BinarySearchTreeException("El valor no se encuentra en el árbol");
        }

    }

    private void removeValueRecursive(T value,boolean left,BinarySearchTree<T> parent){
        if (this.left != null && comparator.compare(value, this.value) < 0){
            this.left.removeValueRecursive(value,true,this);
        } else if(this.right != null && comparator.compare(value, this.value) > 0){
            this.right.removeValueRecursive(value,false,this);
        } else {
            // Si tiene hijo izquierdo
            if(this.left != null){
                T valueMax = this.left.maximum();
                this.value = valueMax;
                deletevalueLeaf(valueMax,true,this);

                // si tiene hijo derecho pero no izquierdo y no es la raiz
            } else if(parent != null && this.right != null && this.left == null){
                this.value = null;
                parent.right = this.right;
                this.right = null;

                // si tiene hijo derecho peor no izquierdo y es la raiz
            } else if (parent == null && this.right != null && this.left == null){
                this.value = this.right.value;
                BinarySearchTree<T> aux = this.right;
                this.right = aux.right;
                this.left = aux.left;
                aux.value = null;
                aux.right = null;
                aux.left = null;
            }
            // si no tiene hijos
            else if (this.left == null && this.right == null) {
                // y es la raiz
                if (parent == null){
                    this.value = null;

                    // si no es la raiz
                } else {
                    if (left) {
                        parent.left = null;
                    } else {
                        parent.right = null;
                    }
                    this.value = null;
                    this.left = null;
                    this.right = null;
                }
            }
        }
    }

    private void deletevalueLeaf(T value,boolean left,BinarySearchTree<T> parent){
        if (this.left != null && comparator.compare(value, this.value) < 0){
            this.left.removeValueRecursive(value,true,this);
        } else if(this.right != null && comparator.compare(value, this.value) > 0){
            this.right.removeValueRecursive(value,false,this);
        } else {
            if (left) { // Si es el hijo izquierdo se va a tener que eliminar desde el padre
                parent.left = null;
            } else {
                parent.right = null;
            }
            this.value = null;
            this.left = null;
            this.right = null;
        }
    }


    @Override
    public List<T> inOrder() {
        List<T> res = new ArrayList<>();
        return this.value == null ? res : inOrderRecursive(res);
    }
    private List<T> inOrderRecursive(List<T> lista){
        if (this.isLeaf()) {
            lista.add(this.value);
            return lista;
        }
        if (this.left != null) {
            lista = this.left.inOrderRecursive(lista);
        }
        lista.add(this.value);
        if (this.right != null) {
            lista = this.right.inOrderRecursive(lista);
        }
        return lista;
    }

    @Override
    public void balance() {

    }

}

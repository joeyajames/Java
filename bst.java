// Java Binary Search Tree

public class Tree {
    Node root;

    public boolean insert(int val) {
        if (root == null) {
            root = new Node(val);
            return true;
        }
        else 
            return root.insert(val);
    }
    
    public boolean find(int val) {
        if (root == null) 
            return false;
        else 
            return root.find(val);
    }
    
    public void preorder() {
        if (root != null) {
            System.out.println("Preorder:");
            root.preorder();
        }
    }
    
    public void postorder() {
        if (root != null) {
            System.out.println("Postorder:");
            root.postorder();
        }
    }
    
    public void inorder() {
        if (root != null) {
            System.out.println("Inorder:");
            root.inorder();
        }
    }

    private class Node {
        private Node leftChild;
        private Node rightChild;
        private int data;

        private Node(int val) {
            data = val;
        }

        private boolean insert(int val) {
            boolean added = false;
            if (this == null) {
                this.data = val;
                return true;
            }
            else {
                if (val < this.data) {
                    if (this.leftChild == null) {
                        this.leftChild = new Node(val);
                        return true;
                    }
                    else 
                        added = this.leftChild.insert(val);
                }
                else if (val > this.data) {
                    if (this.rightChild == null) {
                        this.rightChild = new Node(val);
                        return true;
                    }
                    else
                        added = this.rightChild.insert(val);
                }
            }
            return added;
        }
        
        private boolean find(int val) {
            boolean found = false;
            if (this == null) 
                return false;
            else {
                if (val == this.data)
                    return true;                        
                else if (val < this.data && this.leftChild != null)
                    found = this.leftChild.find(val);
                else if (val > this.data && this.rightChild != null)
                    found = this.rightChild.find(val);
            }
            return found;
        }
        
        private void preorder() {
            if (this != null) {
                System.out.println(this.data);
                if (this.leftChild != null)
                    this.leftChild.preorder();
                if (this.rightChild != null)
                    this.rightChild.preorder();
            }
        }
        
        private void postorder() {
            if (this != null) {
                if (this.leftChild != null)
                    this.leftChild.postorder();
                if (this.rightChild != null)
                    this.rightChild.postorder();
                System.out.println(this.data);
            }
        }
        
        private void inorder() {
            if (this != null) {
                if (this.leftChild != null)
                    this.leftChild.inorder();
                System.out.println(this.data);
                if (this.rightChild != null)
                    this.rightChild.inorder();
            }
        }
    }
}


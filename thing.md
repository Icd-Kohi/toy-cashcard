```java
class LinkedList{
    Node head; 

    public static class Node{
        int data;
        Node next;
        public Node (int data){
            this.data=data;
            next = null;
        }
    }

    public LinkedList insert(LinkedList list, int data){
        Node newNode = new Node(data);

        if(list.head == null){
            list.head = node;            
        }else {
            Node newTail = newNode.head;
            while(newTail.head != null){
                newTail = newNode;                
            }
            newTail.next = newNode;
        }
        return list;

    }
}
public static void main(String [] args){

}
``` 

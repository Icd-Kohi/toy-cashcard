import java.util.*;
public class LinkedList{
    Node head;  // starts pointing to Null;
    static class Node {
        int data;
        Node next;

        public Node(int data){
            this.data = data;
            next = null;
        }
    }

    public static LinkedList insert(LinkedList list, int data) {
        Node node = new Node(data); // next -> null;
        if(list.head == null){
            list.head = node;  // list.head -> data;
        }else{ //list.head not null;
            Node tailNode = list.head;
            while(tailNode.next != null){
                tailNode = tailNode.next;
            }
            tailNode.next = node;
            
        }
        return list; // return always pointing to head;
    }
    public static void printList(LinkedList list){
        Node currNode = list.head;
        while(currNode != null){
            System.out.print(currNode.data + " ");
            currNode = currNode.next;
        }
    }
    public static void main(String [] args) {
        LinkedList list = new LinkedList();

        insert(list, 3);
        insert(list, 4);
        insert(list, 5);
        insert(list, 6);
        insert(list, 7);
        insert(list, 8);
        insert(list, 9);

        printList(list);
    }
}

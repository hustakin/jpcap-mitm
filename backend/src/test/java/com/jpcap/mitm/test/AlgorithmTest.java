/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.jpcap.mitm.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
public class AlgorithmTest {

    @Test
    public void test() throws Exception {
    }

    @Test
    public void testOneWayList() {
        OneWayList<String> oneWayList = new OneWayList<>();
        Node<String> node1 = new Node<>("one");
        Node<String> node2 = new Node<>("two");
        Node<String> node3 = new Node<>("three");
        Node<String> node4 = new Node<>("four");
        oneWayList.insertNode(node1);
        oneWayList.insertNode(node2);
        oneWayList.insertNode(node3);
        oneWayList.insertNode(node4);
        oneWayList.printNodes();
        oneWayList.revertPrintNodes();

        oneWayList.revertNodes();
        oneWayList.printNodes();
        oneWayList.revertPrintNodes();
    }

    class OneWayList<T> {
        private Node<T> head;
        private Node<T> tail;

        public OneWayList() {
        }

        public OneWayList(Node<T> head, Node<T> tail) {
            this.head = head;
            this.tail = tail;
        }

        public void insertNode(Node<T> node) {
            if (head == null) {
                head = node;
                tail = head;
            } else {
                tail.setNext(node);
                tail = node;
            }
        }

        public void printNodes() {
            Node<T> current = head;
            while (current != null) {
                System.out.print(current.getItem() + "  ");
                current = current.getNext();
            }
            System.out.println();
        }

        public void revertNodes() {
            if (head == null)
                return;
            Node<T> prev = null;
            Node<T> current = head;
            while (current != null) {
                Node<T> next = current.getNext();

                current.setNext(prev);
                prev = current;
                current = next;
            }
            tail = head;
            head = prev;
        }

        public void revertPrintNodes() {
            revertPrintNodes(head);
            System.out.println();
        }

        private void revertPrintNodes(Node<T> head) {
            if (head != null) {
                revertPrintNodes(head.getNext());
                System.out.print(head.getItem() + "  ");
            }
        }

        public Node<T> getHead() {
            return head;
        }

        public void setHead(Node<T> head) {
            this.head = head;
        }

        public Node<T> getTail() {
            return tail;
        }

        public void setTail(Node<T> tail) {
            this.tail = tail;
        }
    }

    class Node<T> {
        private T item;
        private Node<T> next;

        public Node(T item) {
            this.item = item;
        }

        public T getItem() {
            return item;
        }

        public void setItem(T item) {
            this.item = item;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }
    }
}

package com.thoughtworks.webdriver.ie;


public class ElementNode extends AbstractNode {
    public ElementNode(long nodePointer) {
        super(nodePointer);
    }

    public native AttributeNode getFirstAttribute();
}

package DO;

import it.unisa.dia.gas.jpbc.Element;
import tools.Pair;
import tools.SHA;

import java.util.List;

public class AccTree {
    public Element data;
    public List<AccTree> childes;
    public AccTree parent;

    public Element[] getChildSiblings(AccTree child) {
        Element[] siblings = new Element[childes.size() - 1];
        int t = 0;
        for (int i = 0; i < childes.size(); i++) {
            if (childes.get(i) == child) {
                continue;
            }

            siblings[t++] = Pair.Zr.newElement(SHA.HASHData(childes.get(i).data.toString())).getImmutable();
        }
        return siblings;
    }

    public AccTree(Element data) {
        this.data = data;
    }

    public AccTree(List<AccTree> data, Element sk, Element[] pk) {
        this.childes = data;
        Element temp = Pair.Zr.newOneElement().getImmutable();
        for (int i = 0; i < data.size(); i++) {
            AccTree child = data.get(i);
            child.parent = this;
            temp = temp.mul(Pair.Zr.newElement(SHA.HASHData(child.data.toString())).add(sk));
        }
        this.data = pk[0].powZn(temp);
    }
//    public AccTree(Element data, AccTree leftChild, AccTree rightChild) {
//        this.data = data;
//        leftChild.sibling = rightChild;
//        rightChild.sibling = leftChild;
//        leftChild.parent = this;
//        rightChild.parent = this;
//    }
}

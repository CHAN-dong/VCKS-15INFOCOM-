package SP;

import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class SearchResult {
    List<Integer> resList;
    Element[] keywordAcc;
    Element[] wits;
    Element[] cWits;
    Element gcd;
    List[] treePaths;

    @Override
    public String toString() {
        return "SearchResult{" +
                "resList=" + resList +
                ", keywordAcc=" + Arrays.toString(keywordAcc) +
                ", wits=" + Arrays.toString(wits) +
                ", cWits=" + Arrays.toString(cWits) +
                ", gcd=" + gcd +
                ", treePaths=" + Arrays.toString(treePaths) +
                '}';
    }

    public SearchResult(List<Integer> resList, Element[] keywordAcc, Element[] wits, Element[] cWits, Element gcd, List[] treePaths) {
        this.resList = resList;
        this.keywordAcc = keywordAcc;
        this.wits = wits;
        this.cWits = cWits;
        this.gcd = gcd;
        this.treePaths = treePaths;
    }

    public List<Integer> getResList() {
        return resList;
    }

    public Element[] getKeywordAcc() {
        return keywordAcc;
    }

    public Element[] getWits() {
        return wits;
    }

    public Element[] getcWits() {
        return cWits;
    }

    public Element getGcd() {
        return gcd;
    }

    public List[] getTreePaths() {
        return treePaths;
    }
}

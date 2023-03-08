package SP;

import com.sun.istack.internal.localization.NullLocalizable;
import tools.*;
import DO.AccTree;
import it.unisa.dia.gas.jpbc.Element;
import DO.SearchToken;
import java.math.BigInteger;
import java.util.*;

import static tools.PloyExGcd.Polynomial_multiply;
import static tools.PloyExGcd.Polynomial_plus;

public class SP {
    Element[] pk;
    HashMap<BigInteger, AccTree> pai_accMap;
    HashMap<BigInteger, boolean[]> secureIndex;

    private String treeToStr(AccTree tree) {
        StringBuffer stringBuffer = new StringBuffer();
        if (tree == null) {
            return "";
        } else {
            stringBuffer.append(tree.data.toString() + " ");
            if (tree.childes != null) {
                for (AccTree accTree : tree.childes) {
                    stringBuffer.append("<");
                    treeToStr(accTree);
                    stringBuffer.append(">");
                }
            }
        }
        return stringBuffer.toString();
    }

    private void getIndexSize() {
        StringBuffer ans = new StringBuffer();
        for (Map.Entry<BigInteger, boolean[]> entry : secureIndex.entrySet()) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(entry.getKey().toString() + " ");
            stringBuffer.append("[");
            for (boolean b : entry.getValue()) {
                if (b) {
                    stringBuffer.append("1");
                } else {
                    stringBuffer.append("0");
                }
                stringBuffer.append(" ");
            }
            stringBuffer.append("]");
//            stringBuffer.append(Arrays.toString(entry.getValue()));
            stringBuffer.append("\n");
            ans.append(stringBuffer);
        }

        for (Map.Entry<BigInteger, AccTree> entry : pai_accMap.entrySet()) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(entry.getKey().toString() + " ");
            stringBuffer.append(treeToStr(entry.getValue()));
            stringBuffer.append("\n");
            ans.append(stringBuffer);
        }

        long size = WriteVO.writeVOToLocal(ans.toString());
        System.out.println("索引大小：" + size / 1024.0 / 1024 + "MB");
    }

    public SP(Element[] pk, HashMap<BigInteger, AccTree> pai_accMap, HashMap<BigInteger, boolean[]> secureIndex) {
        this.pk = pk;
        this.pai_accMap = pai_accMap;
        this.secureIndex = secureIndex;
        getIndexSize();
    }

    public SearchResult search(SearchToken token) {
        int len = token.delta_.length;
        long startTime1 = System.currentTimeMillis();

        //获取查询结果
        boolean[] finalDelta = null;
        boolean[][] allDelta = new boolean[len][];
        for (int i = 0; i < len; i++) {
            allDelta[i] = XOR.xor(secureIndex.get(token.pai[i]), token.delta_[i]);
            if(finalDelta == null){
                finalDelta = allDelta[i];
            }else{
                finalDelta = BitsAnd.bitsAnd(finalDelta, allDelta[i]);
            }
        }
        List<Integer> resList = new LinkedList<>();
        for (int i = 0; i < Objects.requireNonNull(finalDelta).length; i++) {
            if(finalDelta[i]){
                resList.add(i);
            }
        }
        long endTime1 = System.currentTimeMillis();
        System.out.println("查询异或时间：" + (endTime1 - startTime1));

        //获取存在性和完整性证明:keywordAcc, wits, cWits
        Element[] wits = new Element[len];
        Element[] keywordAcc = new Element[len];
        Element[][] witsArrays = new Element[len][];
        for (int i = 0; i < len; i++) {
            List<Element> witList = new LinkedList<>();
            List<Element> accList = new LinkedList<>();
            for(int j = 0; j < allDelta[i].length; j++) {
                if(allDelta[i][j]){
                    Element temp = Pair.Zr.newElement(SHA.HASHData(String.valueOf(j))).getImmutable();
                    accList.add(temp);
                    if(!finalDelta[j]){
                        witList.add(temp);
                    }
                }
            }
            Element[] accArr = accList.toArray(new Element[accList.size()]);
            keywordAcc[i] = getAcc(getCoefficient(accArr));

            Element[] witArr = witList.toArray(new Element[witList.size()]);
            witsArrays[i] = witArr;
            wits[i] = getAcc(getCoefficient(witArr));
        }
        //计算cWits
        Element[] cWits = new Element[witsArrays.length];
        Element[][] a = new Element[witsArrays.length][];
        for (int i = 0; i < cWits.length; i++) {
            a[i] = getCoefficient(witsArrays[i]);
        }
        Element[][] coefficients = PloyExGcd.multi_Gcd(a);


        for (int i = 0; i < cWits.length; i++) {
            cWits[i] = getAcc(coefficients[i]);
        }
        Element[] temp = coefficients[coefficients.length - 1];
        if(temp.length != 1){
            return null;
        }
        Element gcd = temp[temp.length - 1];

        //获取累加器树路径
        List[] treePaths = new List[len];
        for (int i = 0; i < len; i++) {
            List<Element> path = new LinkedList<>();
            AccTree node = pai_accMap.get(token.pai[i]);
            path.add(node.data);
            while(node.parent != null){
                Element[] siblings = node.parent.getChildSiblings(node);
                Element sib = getAcc(getCoefficient(siblings));
//                Element sib = pk[0].pow(SHA.HASHData(node.sibling.data.toString())).mul(pk[1]);
                path.add(sib);
                path.add(node.parent.data);
                node = node.parent;
            }
            treePaths[i] = path;
        }
        return new SearchResult(resList, keywordAcc, wits, cWits, gcd, treePaths);
    }

    public Element getAcc(Element[] coefficient) {
        Element acc = Pair.pairing.getG1().newRandomElement().setToOne().getImmutable();
        for (int i = coefficient.length - 1; i >= 0; i--) {
//            if(coefficient[i].compareTo(Fraction.ZERO) >= 0){
//                BigInteger temp = new BigInteger(coefficient[i].toString());
//                acc = acc.mul(pk[coefficient.length - 1 - i].powZn(Pair.Zr.newElement(temp)));
//            }else{
//                BigInteger temp = new BigInteger(coefficient[i].toString()).negate();
//                acc = acc.div(pk[coefficient.length - 1 - i].powZn(Pair.Zr.newElement(temp)));
//            }
//            BigInteger temp = new BigInteger(coefficient[i].toString());
            acc = acc.mul(pk[coefficient.length - 1 - i].powZn(coefficient[i]));
        }
        return acc;
    }

    public Element[] getCoefficient(Element[] roots){
//        long l1 = System.currentTimeMillis();
        int l = roots.length;
        Element[][] dp = new Element[l + 1][l];
        Element[] coefficient = new Element[l + 1];
        dp[0][0] = roots[0];
        for(int j = 1;j < l;j++){
            dp[0][j] = dp[0][j - 1].mul(roots[j]);
        }
        coefficient[l] = dp[0][l - 1];
        for(int i = 1;i < l + 1;i++){
            dp[i][i - 1] = Pair.Zr.newOneElement().getImmutable();
            for(int j = i;j < l;j++){
                dp[i][j] = dp[i - 1][j - 1].add(dp[i][j - 1].mul(roots[j]));
            }
            coefficient[l - i] = dp[i][l - 1];
        }
//        long l2 = System.currentTimeMillis();
//        System.out.println("获取系数时间：" + (l2 - l1) + "ms");
        return coefficient;
    }


}

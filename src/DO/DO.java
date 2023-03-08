package DO;

import SP.SearchResult;
import it.unisa.dia.gas.jpbc.Element;
import tools.Pair;
import tools.SHA;
import tools.WriteVO;
import tools.XOR;
import java.math.BigInteger;
import java.util.*;

import static DO.WriteKeyAndGetKey.getPublicKey;
import static DO.WriteKeyAndGetKey.getSecretKey;
import static tools.Parameter.M;
import static tools.Parameter.N;

public class DO {
    String k1;
    String k2;

    public Element[] getPk() {
        return pk;
    }

    public Element getRootAcc() {
        return rootAcc;
    }

    public HashMap<BigInteger, AccTree> getPai_accMap() {
        return pai_accMap;
    }

    public HashMap<BigInteger, boolean[]> getSecureIndex() {
        return secureIndex;
    }

    Element sk;
    Element[] pk;
    Element rootAcc;
    AccTree root;
    HashMap<BigInteger, AccTree> pai_accMap;
    HashMap<BigInteger, boolean[]> secureIndex;

    public String getTreeSizeMain(AccTree node) {
        StringBuilder ans = new StringBuilder();

        if (node.childes != null) {
            ans.append("<");
            for (AccTree child : node.childes) {
                if (child != null) {
                    ans.append(getTreeSizeMain(child) + ",");
                }
            }
        } else {
            return "";
        }

        ans.append(node.data.toString() + ">");
        return ans.toString();
    }

    public void getAccTreeSize() {
        String ans = getTreeSizeMain(this.root);
        ans += "\n";
        StringBuilder str = new StringBuilder();
        for (Map.Entry<BigInteger, AccTree> entry : pai_accMap.entrySet()) {
            str.append(entry.getKey() + ":" + entry.getValue().data.toString() + "" + "\n");
        }
        ans += str.toString();
        long l = WriteVO.writeVOToLocal(ans);
        System.out.println("保存索引大小:" + l / 1024 + "kb");
    }

    public DO(String k1, String k2, String filePath) {
//        Element g = Pair.pairing.getG1().newRandomElement().getImmutable();
        this.k1 = k1;
        this.k2 = k2;
        this.sk = getSecretKey(filePath);
        this.pk = getPublicKey(filePath);
    }

    public void getUpdToken(int id, List<Integer> keywords, boolean isAdd) {
        if (isAdd) {
            HashSet<Integer> st = new HashSet<>();
            for (int k : keywords) {
                st.add(k);
            }
            boolean[] col = new boolean[M];
            for (int k = 1; k <= M; ++k) {
                String tempK = SHA.HASHDataToString(k2 + k);
                int tempS = tempK.charAt(M % tempK.length());
                if (st.contains(k)) {
                    col[k - 1] = (tempS + 1) % 2 == 0;
                    BigInteger pai = SHA.HASHData(k1 + k);
                    AccTree node = pai_accMap.get(pai);
                    BigInteger preHash = SHA.HASHData(node.data.toString());
                    node.data = node.data.powZn(sk.add(Pair.pairing.getZr().newElement(SHA.HASHData(String.valueOf(id)))));
                    BigInteger newHash = SHA.HASHData(node.data.toString());
                    node = node.parent;
                    while (node != null) {
                        BigInteger temp = preHash;
                        preHash = SHA.HASHData(node.data.toString());
                        node.data = node.data.powZn(sk.add(Pair.pairing.getZr().newElement(newHash)).div(sk.add(Pair.pairing.getZr().newElement(temp))));
                        newHash = SHA.HASHData(node.data.toString());
                        node = node.parent;
                    }
                } else {
                    col[k - 1] = (tempS + 0) % 2 == 0;
                }
            }
        } else {
            for (int k : keywords) {
                BigInteger pai = SHA.HASHData(k1 + k);
                AccTree node = pai_accMap.get(pai);
                BigInteger preHash = SHA.HASHData(node.data.toString());
                node.data = node.data.powZn(Pair.Zr.newOneElement().div(sk.add(Pair.pairing.getZr().newElement(SHA.HASHData(String.valueOf(id))))));
                BigInteger newHash = SHA.HASHData(node.data.toString());
                node = node.parent;
                while (node != null) {
                    BigInteger temp = preHash;
                    preHash = SHA.HASHData(node.data.toString());
                    node.data = node.data.powZn(sk.add(Pair.pairing.getZr().newElement(newHash)).div(sk.add(Pair.pairing.getZr().newElement(temp))));
                    newHash = SHA.HASHData(node.data.toString());
                    node = node.parent;
                }
            }
        }
    }
    public void setup(HashMap<Integer, boolean[]> data) {
        secureIndex = new HashMap<>();
        pai_accMap = new HashMap<>();
        HashMap<Integer, Element> dLeaf = new HashMap<>();
//        Element[] dLeaf = new Element[data.size()];
        for (Map.Entry<Integer, boolean[]> obj : data.entrySet()) {
//        for(int i = 0;i < data.length;i++){
            int keyword = obj.getKey();//关键字从1开始
            boolean[] value = obj.getValue();
            BigInteger pai = SHA.HASHData(k1 + keyword);
            boolean[] delta = XOR.xor(value, SHA.HASHDataToString(k2 + keyword));
            secureIndex.put(pai, delta);
            //计算叶节点累加器值
            Element temp = sk.add(Pair.Zr.newElement(pai));
//            Element temp = Pair.pairing.getZr().newElement(1);
            for(int t = 0;t < value.length;t++){
                if(value[t]){
                    temp = temp.mul(sk.add(Pair.pairing.getZr().newElement(SHA.HASHData(String.valueOf(t)))));
                }
            }
            dLeaf.put(keyword, pk[0].powZn(temp));
//            dLeaf[i] = pk[0].powZn(temp);
        }
        buildTree(dLeaf);
    }

    private void buildTree(HashMap<Integer, Element> data) {
        List<AccTree> currentNode = new ArrayList<>(data.size());
        int m = (int) Math.sqrt(data.size());
        for (Map.Entry<Integer, Element> obj : data.entrySet()) {
            int keyword = obj.getKey();
            Element value = obj.getValue();
            BigInteger pai = SHA.HASHData(k1 + keyword);
            AccTree leafNode = new AccTree(value);
            pai_accMap.put(pai, leafNode);
            currentNode.add(leafNode);
        }
        List<AccTree> tempNode = new ArrayList<>();
        int i = 0;
        while (i < currentNode.size()) {
            if ((i + m) <= currentNode.size()) {
                tempNode.add(new AccTree(currentNode.subList(i, i + m), sk, pk));
            } else {
                tempNode.add(new AccTree(currentNode.subList(i, currentNode.size()), sk, pk));
            }
            i += m;
        }
        AccTree rootNode = new AccTree(tempNode, sk, pk);
        root = rootNode;
        this.rootAcc = rootNode.data;
//        while(currentNode.size() > 1) {
//            List<AccTree> tempNode = new ArrayList<>(currentNode.size() / 2);
//            if(currentNode.size() % 2 == 0){
//                for(int i = 0;i < currentNode.size();i = i + 2) {
//                    AccTree left = currentNode.get(i);
//                    AccTree right = currentNode.get(i + 1);
//                    Element acc = pk[0].powZn(Pair.Zr.newElement(SHA.HASHData(left.data.toString())).add(sk).mul(Pair.Zr.newElement(SHA.HASHData(right.data.toString())).add(sk)));
//                    AccTree midNode = new AccTree(acc, left, right);
//                    tempNode.add(midNode);
//                }
//            } else {
//                for(int i = 0;i < currentNode.size() - 3;i = i + 2) {
//                    AccTree left = currentNode.get(i);
//                    AccTree right = currentNode.get(i + 1);
//                    Element acc = pk[0].powZn(Pair.Zr.newElement(SHA.HASHData(left.data.toString())).add(sk).mul(Pair.Zr.newElement(SHA.HASHData(right.data.toString())).add(sk)));
//                    AccTree midNode = new AccTree(acc, left, right);
//                    tempNode.add(midNode);
//                }
//                AccTree left = currentNode.get(currentNode.size() - 3);
//                AccTree right = currentNode.get(currentNode.size() - 2);
//                Element acc = pk[0].powZn(Pair.Zr.newElement(SHA.HASHData(left.data.toString())).add(sk).mul(Pair.Zr.newElement(SHA.HASHData(right.data.toString())).add(sk)));
//
//                AccTree midNode = new AccTree(acc, left, right);
//                tempNode.add(midNode);
//                left = midNode;
//                right = currentNode.get(currentNode.size() - 1);
//                acc = pk[0].powZn(Pair.Zr.newElement(SHA.HASHData(left.data.toString())).add(sk).mul(Pair.Zr.newElement(SHA.HASHData(right.data.toString())).add(sk)));
//                midNode = new AccTree(acc, left, right);
//                tempNode.add(midNode);
//            }
//            currentNode = tempNode;
//        }
//        this.rootAcc = currentNode.get(0).data;
    }

    public SearchToken getSearchToken(int[] keywords) {
        BigInteger[] pai = new BigInteger[keywords.length];
        String[] delta_ = new String[keywords.length];
        for(int i = 0;i < keywords.length;i++) {
            pai[i] = SHA.HASHData(k1 + keywords[i]);
            delta_[i] = SHA.HASHDataToString(k2 + keywords[i]);
        }
        return new SearchToken(pai, delta_);
    }


    public boolean verifyRes(int[] keywords, SearchResult res) {
        long l0 = System.currentTimeMillis();
        //验证路径是否正确
        List[] treePaths = res.getTreePaths();
        Element[] keywordAcc = res.getKeywordAcc();
        for (int i = 0; i < treePaths.length; i++) {
            List<Element> path = treePaths[i];
            if(!this.rootAcc.equals(path.get(path.size() - 1))){
                return false;
            }
            Element leafAcc = path.get(0);
            Element left = Pair.pairing.pairing(leafAcc, pk[0]);
            BigInteger pai = SHA.HASHData(k1 + keywords[i]);
            Element right = Pair.pairing.pairing(keywordAcc[i], pk[0].powZn(Pair.Zr.newElement(pai).add(sk)));
            if(!left.isEqual(right)){
                return false;
            }
            for (int j = 0; j < path.size() - 1; j = j + 2) {
                left = Pair.pairing.pairing(path.get(j + 2), pk[0]);
                right = Pair.pairing.pairing(path.get(j + 1), pk[0].powZn(Pair.Zr.newElement(SHA.HASHData(path.get(j).toString())).add(sk)));
                if(!left.isEqual(right)){
                    return false;
                }
            }
        }
        long l1 = System.currentTimeMillis();
        //验证正确性
        Element left;
        Element right;
        List<Integer> result = res.getResList();
        Element temp = Pair.Zr.newOneElement().getImmutable();
        for (int i = 0; i < result.size(); i++) {
            temp = temp.mul(Pair.Zr.newElement(SHA.HASHData(String.valueOf(result.get(i)))).add(sk));
        }
        Element resAcc = pk[0].powZn(temp);
        Element[] wits = res.getWits();

        Element allWitAcc = Pair.pairing.getG1().newOneElement().getImmutable();
        for (int i = 0; i < wits.length; i++) {
            allWitAcc = allWitAcc.mul(wits[i]);
        }
        left = Pair.pairing.pairing(resAcc, allWitAcc);
        Element allKeyAcc = Pair.pairing.getG1().newOneElement().getImmutable();
        for (int i = 0; i < keywordAcc.length; i++) {
            allKeyAcc = allKeyAcc.mul(keywordAcc[i]);
        }
        right = Pair.pairing.pairing(allKeyAcc, pk[0]);
        if(!left.equals(right)){
            return false;
        }
        long l2 = System.currentTimeMillis();
        //验证完整性
        Element[] cWits = res.getcWits();
        left = Pair.pairing.pairing(cWits[0], wits[0]).getImmutable();
        for (int i = 1; i < cWits.length; i++) {
            left = left.mul(Pair.pairing.pairing(cWits[i], wits[i]));
        }
        right = Pair.pairing.pairing(pk[0], pk[0]).powZn(res.getGcd()).getImmutable();//
        if(!left.equals(right)){
            return false;
        }
        long l3 = System.currentTimeMillis();
        System.out.println("路劲验证时间：" + (l1 - l0));
        System.out.println("正确性验证时间：" + (l2 - l1));
        System.out.println("完整性验证时间：" + (l3 - l2));
        return true;
    }


//    @Test
//    public void test() {
//        Fraction[] arr = new Fraction[]}{new Fraction()};
//    }
}

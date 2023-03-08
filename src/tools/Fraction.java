//package tools;
//
//import it.unisa.dia.gas.jpbc.Element;
//import org.testng.annotations.Test;
//
//import java.math.BigInteger;
//import java.util.Comparator;
//import java.util.Objects;
//
//public class Fraction {
//    private Element ntNum;
//    private Element doNum;
//
//    public static final Fraction ZERO = new Fraction(Pair.Zr.newElement(0));
//    public Fraction(Element ntNum, Element doNum) {
//        this.ntNum = ntNum;
//        this.doNum = doNum;
//        if (doNum.equals(Pair.Zr.newElement(0))) {
//            System.out.println("分母为0，已更改为1");
//            this.doNum = Pair.Zr.newElement(1);
//        }
//        //约分处理
//        reduce();
//    }
//
//    public Fraction(Element ntNum) {
//        this.ntNum = ntNum;
//        this.doNum = Pair.Zr.newElement(1);
//    }
//
//    private void reduce() {
//        // 获得最大公约数
//        Element m = maxDe(ntNum, doNum);
//        ntNum = ntNum.div(m);
//        doNum = doNum.div(m);
//    }
//
//    // 欧几里得算法求最大公约数
//    private Element maxDe(Element a, Element b) {
//        while (!b.equals(Pair.Zr.newElement(0))) {
//            Element r = a.sub(a.div(b));
//            a = b;
//            b = r;
//        }
//        return a;
//    }
//
//    // 分数加法
//    public Fraction addition(Fraction f) {
//        // a/b + c/d
//        // 生成新的分子: a * d + b * c
//        Element newNt = ntNum.mul(f.getDoNum()).add(doNum.mul(f.getNtNum()));
//        // 生成新的分母: b * d
//        Element newDo = doNum.mul(f.getDoNum());
//        // 使用新的分子分母创建一个新的分数对象
//        return new Fraction(newNt, newDo);
//    }
//
//    //减法
//    public Fraction subtraction(Fraction f) {
//        Element newNt = ntNum.mul(f.getDoNum()).sub(doNum.mul(f.getNtNum()));
//        Element newDo = doNum.mul(f.getDoNum());
//        return new Fraction(newNt, newDo);
//    }
//
//    //求负数
//    public Fraction negative() {
//        return new Fraction(ntNum.negate(), doNum);
//    }
//
//    //乘法
//    public Fraction multiplication(Fraction f) {
//        Element newNt = ntNum.mul(f.getNtNum());
//        Element newDo = doNum.mul(f.getDoNum());
//        return new Fraction(newNt, newDo);
//    }
//
//    //除法
//    public Fraction division(Fraction f) {
//        if (f.getDoNum().equals(Pair.Zr.newElement(0))) {
//            System.out.println("不能除以0");
//            return null;
//        }
//        Element newNt = ntNum.mul(f.getDoNum());
//        Element newDo = doNum.mul(f.getNtNum());
//        return new Fraction(newNt, newDo);
//    }
//
//    // 输出字符串, 按照 分子/分母的形式输出
//    @Override
//    public String toString() {
//        // 分子为0时输出0
//        if (ntNum.equals(new BigInteger("0"))) {
//            return "0";
//        }
//        // 分母为1，输出整数
//        if (doNum.equals(new BigInteger("1"))) {
//            return String.valueOf(ntNum);
//        }
//        // 其余方式按 分子/分母 输出
//        return ntNum + "/" + doNum;
//    }
//
//
//    public Element getNtNum() {
//        return ntNum;
//    }
//
//    public void setNtNum(Element ntNum) {
//        this.ntNum = ntNum;
//    }
//
//    public Element getDoNum() {
//        return doNum;
//    }
//
//    public void setDoNum(Element doNum) {
//        this.doNum = doNum;
//    }
//
//
//}

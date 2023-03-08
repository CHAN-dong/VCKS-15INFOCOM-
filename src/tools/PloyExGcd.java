package tools;

import it.unisa.dia.gas.jpbc.Element;
import java.util.Arrays;


public class PloyExGcd {

    //多参数的欧几里得算法，返回的是pi(0<= i< a.length),使p0 * a0 + p1 * a1 + ... = c。如果a中元素不互素，则返回null
    public static Element[][] multi_Gcd(Element[][] a){
        Element[][] res = new Element[a.length + 1][];
        res[0] = new Element[]{Pair.Zr.newOneElement().getImmutable()};
        Element[] gcd = a[0];
        for(int i = 1;i < a.length;i++){
            Element[][] temp;
            if(gcd.length >= a[i].length){
                temp = Gcd(gcd, a[i]);
                gcd = temp[0];
                res[i] = temp[2];
                for(int j = 0;j < i;j++){
                    res[j] = Polynomial_multiply(res[j], temp[1]);
                }
            }else{
                temp = Gcd(a[i], gcd);
                gcd = temp[0];
                res[i] = temp[1];
                for(int j = 0;j < i;j++){
                    res[j] = Polynomial_multiply(res[j], temp[2]);
                }
            }
        }

        Element[] t = Polynomial_multiply(a[0], res[0]);
        for(int i = 1;i < res.length - 1;i ++) {
            t = Polynomial_plus(t, Polynomial_multiply(a[i], res[i]));
        }

        res[a.length] = gcd;
        return res;
    }

    private static Element[] reduce(Element[] arr) {
        int tag = 0;
        while(tag < arr.length && arr[tag].equals(Pair.Zr.newZeroElement())){
            tag++;
        }
        Element[] res = new Element[arr.length - tag];
        for(int i = 0;i < res.length;i++,tag++){
            res[i] = arr[tag];
        }
        return res;
    }

    private static Element[][] Gcd(Element[] a, Element[] b){
        Element[][] res = new Element[3][];
        if(b.length == 0){
            res[1] = new Element[]{Pair.Zr.newOneElement().getImmutable()};
            res[2] = new Element[]{Pair.Zr.newZeroElement().getImmutable()};
            res[0] = Arrays.copyOf(a,a.length);
            return res;
        }

        Element[][] divide_R = Polynomial_divide(a, b);
        res = Gcd(b, divide_R[1]);
        Element[] temp = res[1];
        res[1] = res[2];
        res[2] = Polynomial_minus(temp, Polynomial_multiply(divide_R[0], res[2]));
        return res;
    }



    //多项式除法
    public static Element[][] Polynomial_divide(Element[] A, Element[] B){
        Element[][] res = new Element[2][];
        Element[] R = new Element[A.length - B.length + 1];
        int b_l = B.length;
        Element[] temp = Arrays.copyOf(A, A.length);
        for(int i = 0;i < R.length;i++){
            R[i] = temp[i].div(B[0]);
            for(int j = 0;j < b_l;j++){
                temp[j + i] = temp[j + i].sub(R[i].mul(B[j]));
            }
        }
        Element[] L = reduce(temp);
        res[0] = R;
        res[1] = L;
        return res;
    }

    //多项式减法
    public static Element[] Polynomial_minus(Element[] A, Element[] B){
        int _a = A.length;
        int _b = B.length;
        int l = Math.max(_a, _b);
        Element[] R = new Element[l];
        if(_a >= _b){
            for(int i = 0;i < _b;i++){
                R[l - i - 1] = A[_a - i - 1].sub(B[_b - i - 1]);
            }
            for(int i = _b;i < _a;i++){
                R[l - i - 1] = A[_a - i - 1];
            }
        }else{
            for(int i = 0;i < _a;i++){
                R[l - i - 1] = A[_a - i - 1].sub(B[_b - i - 1]);
            }
            for(int i = _a;i < _b;i++){
                R[l - i - 1] = B[_b - i - 1].negate();
            }
        }
        return reduce(R);
    }


    //多项式加法
    public static Element[] Polynomial_plus(Element[] A, Element[] B){
        int _a = A.length;
        int _b = B.length;
        int l = Math.max(_a, _b);
        Element[] R = new Element[l];
        if(_a >= _b){
            for(int i = 0;i < _b;i++){
                R[l - i - 1] = A[_a - i - 1].add(B[_b - i - 1]);
            }
            for(int i = _b;i < _a;i++){
                R[l - i - 1] = A[_a - i - 1];
            }
        }else{
            for(int i = 0;i < _a;i++){
                R[l - i - 1] = A[_a - i - 1].add(B[_b - i - 1]);
            }
            for(int i = _a;i < _b;i++){
                R[l - i - 1] = B[_b - i - 1];
            }
        }
        return reduce(R);
    }

    //多项式乘法
    public static Element[] Polynomial_multiply(Element[] A, Element[] B){
        Element[] R = new Element[A.length + B.length - 1];
        Arrays.fill(R, Pair.Zr.newZeroElement().getImmutable());
        int a_l = A.length;
        int b_l = B.length;
        for(int i = 0;i < a_l;i++){
            for(int j = 0;j < b_l;j++){
                R[i + j] = R[i + j].add(A[i].mul(B[j]));
            }
        }
        return R;
    }



    public static void main(String[] args) {
        Element[][] a = new Element[3][];
        a[0] = new Element[]{Pair.Zr.newRandomElement().getImmutable(), Pair.Zr.newElement(1).getImmutable(), Pair.Zr.newRandomElement().getImmutable()};
        a[1] = new Element[]{Pair.Zr.newRandomElement().getImmutable(), Pair.Zr.newRandomElement().getImmutable()};
        a[2] = new Element[]{Pair.Zr.newRandomElement().getImmutable(),Pair.Zr.newRandomElement().getImmutable(),};


//        Element[] p1 = new Element[]{Pair.Zr.newRandomElement().getImmutable(),Pair.Zr.newRandomElement().getImmutable(),Pair.Zr.newRandomElement().getImmutable()};
//        Element[] A = new Element[]{Pair.Zr.newRandomElement().getImmutable(),Pair.Zr.newRandomElement().getImmutable(),Pair.Zr.newRandomElement().getImmutable()};
//        Element[] p2 = new Element[]{Pair.Zr.newRandomElement().getImmutable(),Pair.Zr.newRandomElement().getImmutable(),Pair.Zr.newRandomElement().getImmutable()};
//        Element[] B = new Element[]{Pair.Zr.newRandomElement().getImmutable(),Pair.Zr.newRandomElement().getImmutable(),Pair.Zr.newRandomElement().getImmutable()};
//        Element[] p3 = new Element[]{Pair.Zr.newRandomElement().getImmutable(),Pair.Zr.newRandomElement().getImmutable(),Pair.Zr.newRandomElement().getImmutable()};
//
//        Element[] p1 = new Element[]{Pair.Zr.newElement(1).getImmutable(),Pair.Zr.newElement(2).getImmutable(),Pair.Zr.newElement(3).getImmutable()};
//        Element[] A = new Element[]{Pair.Zr.newElement(1).getImmutable(),Pair.Zr.newElement(2).getImmutable(),Pair.Zr.newElement(3).getImmutable()};
//        Element[] p2 = new Element[]{Pair.Zr.newElement(1).getImmutable(),Pair.Zr.newElement(2).getImmutable(),Pair.Zr.newElement(3).getImmutable()};
//        Element[] B = new Element[]{Pair.Zr.newElement(1).getImmutable(),Pair.Zr.newElement(2).getImmutable(),Pair.Zr.newElement(3).getImmutable()};
//        Element[] p3 = new Element[]{Pair.Zr.newElement(1).getImmutable(),Pair.Zr.newElement(2).getImmutable(),Pair.Zr.newElement(3).getImmutable()};
//
//
//        Element[] C = Polynomial_plus(Polynomial_multiply(p1, A), Polynomial_multiply(p2, B));
//
//        Element[] p1p3 = Polynomial_multiply(p1, p3);
//        Element[] p2p3 = Polynomial_multiply(p2, p3);
//
//        Element[] l = Polynomial_plus(Polynomial_multiply(p1p3, A), Polynomial_multiply(p2p3, B));
//        Element[] r = Polynomial_multiply(C, p3);
//        boolean s = l.equals(r);


//        Fraction[][] divide_R = Polynomial_divide(A, B);
//
//        Fraction[] multi_R = Polynomial_multiply(A, B);
//
//        Fraction[] minus_R = Polynomial_minus(B, A);
//        Fraction[][] input = new Fraction[4][];
//        input[0] = new double[]{1,-3,2};
//        input[1] = new double[]{1,-7,12};
//        input[2] = new double[]{1,-4,3};
//        input[3] = new double[]{1,-6,8};
//        double[][] res = multi_Gcd(input);
//        double[] verify = new double[]{0};
//        for(int i = 0;i < res.length;i++){
//            verify = Polynomial_plus(verify, Polynomial_multiply(input[i], res[i]));
//        }

//        Element[][] res = multi_Gcd(a);

//
//        Element[] verify0 = Polynomial_plus(Polynomial_multiply(A, gcd[1]), Polynomial_multiply(B, gcd[2]));
//
//
//        Fraction[][] input = new Fraction[4][];
//        input[0] = new Fraction[]{new Fraction(new BigInteger("1")), new Fraction(new BigInteger("-3")), new Fraction(new BigInteger("2"))};
//        input[1] = new Fraction[]{new Fraction(new BigInteger("1")), new Fraction(new BigInteger("-7")), new Fraction(new BigInteger("12"))};
//        input[2] = new Fraction[]{new Fraction(new BigInteger("1")), new Fraction(new BigInteger("-4")), new Fraction(new BigInteger("3"))};
//        input[3] = new Fraction[]{new Fraction(new BigInteger("1")), new Fraction(new BigInteger("-6")), new Fraction(new BigInteger("8"))};
//        Fraction[][] res = multi_Gcd(input);
//
//        Fraction[] verify = new Fraction[]{new Fraction(new BigInteger("0"))};
//        for(int i = 0;i < res.length - 1;i++){
//            verify = Polynomial_plus(verify, Polynomial_multiply(input[i], res[i]));


    }
}

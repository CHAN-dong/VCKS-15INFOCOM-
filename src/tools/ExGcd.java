//package tools;
//
//import it.unisa.dia.gas.jpbc.Element;
//import org.testng.annotations.Test;
//
//import java.math.BigInteger;
//import java.util.Arrays;
//
//public class ExGcd {
//
//    //多参数的欧几里得算法，返回的是pi(0<= i< a.length),使p0 * a0 + p1 * a1 + ... = c，且pi系数为整数。如果a中元素不互素，则返回null
//    public static Fraction[][] multi_Gcd(Fraction[][] a){
//        Fraction[][] res = new Fraction[a.length + 1][];
//        res[0] = new Fraction[]{new Fraction(Pair.Zr.newOneElement())};
//        Fraction[] gcd = a[0];
//        for(int i = 1;i < a.length;i++){
//            Fraction[][] temp;
//            if(gcd.length >= a[i].length){
//                temp = polynomial_Gcd(gcd, a[i]);
//                gcd = temp[0];
//                res[i] = temp[2];
//                for(int j = 0;j < i;j++){
//                    res[j] = Polynomial_multiply(res[j], temp[1]);
//                }
//            }else{
//                temp = polynomial_Gcd(a[i], gcd);
//                gcd = temp[0];
//                res[i] = temp[1];
//                for(int j = 0;j < i;j++){
//                    res[j] = Polynomial_multiply(res[j], temp[2]);
//                }
//            }
//        }
//        res[a.length] = gcd;
//        return res;
//    }
//
//    private static Fraction[] reduce(Fraction[] arr) {
//        int tag = 0;
//        while(tag < arr.length && arr[tag].getNtNum().equals(new BigInteger("0"))){
//            tag++;
//        }
//        Fraction[] res = new Fraction[arr.length - tag];
//        for(int i = 0;i < res.length;i++,tag++){
//            res[i] = arr[tag];
//        }
//        return res;
//    }
//
//    private static Fraction[][] polynomial_Gcd(Fraction[] a, Fraction[] b) {
//        Fraction[][] res = Gcd(a, b);
//        Element[] arr = new Element[res[0].length + res[1].length + res[2].length];
//        for (int i = 0; i < res[0].length; i++) {
//            arr[i] = res[0][i].getDoNum();
//        }
//        for (int i = 0; i < res[1].length; i++) {
//            arr[res[0].length + i] = res[1][i].getDoNum();
//        }
//        for (int i = 0; i < res[2].length; i++) {
//            arr[res[0].length + res[1].length + i] = res[2][i].getDoNum();
//        }
//        Fraction x = new Fraction(getMinMultiCommonMultiple(arr));
//        for (int i = 0; i < res.length; i++) {
//            for (int j = 0; j < res[i].length; j++) {
//                res[i][j] = res[i][j].multiplication(x);
//            }
//        }
//        return res;
//    }
//
//
//
//    private static Fraction[][] Gcd(Fraction[] a, Fraction[] b){//这个Gcd求出来的系数有小数
//        Fraction[][] res = new Fraction[3][];
//        if(b.length == 0){
//            res[1] = new Fraction[]{new Fraction(Pair.Zr.newOneElement())};
//            res[2] = new Fraction[]{new Fraction(Pair.Zr.newZeroElement())};
//            res[0] = Arrays.copyOf(a,a.length);
//            return res;
//        }
//
//        Fraction[][] divide_R = Polynomial_divide(a, b);
//        res = Gcd(b, divide_R[1]);
//        Fraction[] temp = res[1];
//        res[1] = res[2];
//        res[2] = Polynomial_minus(temp, Polynomial_multiply(divide_R[0], res[2]));
//        return res;
//    }
//
//
//
//    //多项式除法
//    static Fraction[][] Polynomial_divide(Fraction[] A, Fraction[] B){
//        Fraction[][] res = new Fraction[2][];
//        Fraction[] R = new Fraction[A.length - B.length + 1];
//        int b_l = B.length;
//        Fraction[] temp = Arrays.copyOf(A, A.length);
//        for(int i = 0;i < R.length;i++){
//            R[i] = temp[i].division(B[0]);
//            for(int j = 0;j < b_l;j++){
//                temp[j + i] = temp[j + i].subtraction(R[i].multiplication(B[j]));
//            }
//        }
//        Fraction[] L = reduce(temp);
//        res[0] = R;
//        res[1] = L;
//        return res;
//    }
//
//    //多项式减法
//    static Fraction[] Polynomial_minus(Fraction[] A, Fraction[] B){
//        int _a = A.length;
//        int _b = B.length;
//        int l = Math.max(_a, _b);
//        Fraction[] R = new Fraction[l];
//        if(_a >= _b){
//            for(int i = 0;i < _b;i++){
//                R[l - i - 1] = A[_a - i - 1].subtraction(B[_b - i - 1]);
//            }
//            for(int i = _b;i < _a;i++){
//                R[l - i - 1] = A[_a - i - 1];
//            }
//        }else{
//            for(int i = 0;i < _a;i++){
//                R[l - i - 1] = A[_a - i - 1].subtraction(B[_b - i - 1]);
//            }
//            for(int i = _a;i < _b;i++){
//                R[l - i - 1] = B[_b - i - 1].negative();
//            }
//        }
//        return reduce(R);
//    }
//
//
//    //多项式加法
//    static Fraction[] Polynomial_plus(Fraction[] A, Fraction[] B){
//        int _a = A.length;
//        int _b = B.length;
//        int l = Math.max(_a, _b);
//        Fraction[] R = new Fraction[l];
//        if(_a >= _b){
//            for(int i = 0;i < _b;i++){
//                R[l - i - 1] = A[_a - i - 1].addition(B[_b - i - 1]);
//            }
//            for(int i = _b;i < _a;i++){
//                R[l - i - 1] = A[_a - i - 1];
//            }
//        }else{
//            for(int i = 0;i < _a;i++){
//                R[l - i - 1] = A[_a - i - 1].addition(B[_b - i - 1]);
//            }
//            for(int i = _a;i < _b;i++){
//                R[l - i - 1] = B[_b - i - 1];
//            }
//        }
//        return reduce(R);
//    }
//
//    //多项式乘法
//    static Fraction[] Polynomial_multiply(Fraction[] A, Fraction[] B){
//        Fraction[] R = new Fraction[A.length + B.length - 1];
//        Arrays.fill(R, new Fraction(Pair.Zr.newZeroElement()));
//        int a_l = A.length;
//        int b_l = B.length;
//        for(int i = 0;i < a_l;i++){
//            for(int j = 0;j < b_l;j++){
//                R[i + j] = R[i + j].addition(A[i].multiplication(B[j]));
//            }
//        }
//        return R;
//    }
//
//
//
//    //多个数的最小公倍数
//    static Element getMinMultiCommonMultiple(Element []arrays) {
//        //实现原理：拿前两个数的最小公约数和后一个数比较，求他们的公约数以此来推。。。
//        Element val = arrays[0];
//        for (int i = 1; i < arrays.length; i++) {
//            val = getMinCommonMultiple(val, arrays[i]);
//        }
//        return val;
//    }
//
//    //两个数的最小公倍数
//    static Element getMinCommonMultiple(Element a,Element b) {
//        return a.mul(b).div(getMaxCommonDivisor(a, b));
//    }
//
//    // 欧几里得算法求最大公约数
//    static Element getMaxCommonDivisor(Element a, Element b) {
//        while (!b.equals(Pair.Zr.newZeroElement())) {
//            Element r = a.sub(a.div(b));
//            a = b;
//            b = r;
//        }
//        return a;
//    }
//
//
//    @Test
//    public void test() {
//        Element []arrays = new Element[]{Pair.Zr.newElement(6), Pair.Zr.newElement(9), Pair.Zr.newElement(12)};
//        System.out.println(getMinMultiCommonMultiple(arrays));
//    }
//
//    public static void main(String[] args) {
////        Fraction[] A = new Fraction[]{new Fraction(1), new Fraction(2), new Fraction(0), new Fraction(-2)};
////        Fraction[] B = new Fraction[]{new Fraction(1), new Fraction(0), new Fraction(-1)};
////
////        Fraction[][] divide_R = Polynomial_divide(A, B);
////
////        Fraction[] multi_R = Polynomial_multiply(A, B);
////
////        Fraction[] minus_R = Polynomial_minus(B, A);
////        Fraction[][] input = new Fraction[4][];
////        input[0] = new double[]{1,-3,2};
////        input[1] = new double[]{1,-7,12};
////        input[2] = new double[]{1,-4,3};
////        input[3] = new double[]{1,-6,8};
////        double[][] res = multi_Gcd(input);
////        double[] verify = new double[]{0};
////        for(int i = 0;i < res.length;i++){
////            verify = Polynomial_plus(verify, Polynomial_multiply(input[i], res[i]));
////        }
//
////        Fraction[] A = new Fraction[]{new Fraction(new BigInteger("1")), new Fraction(new BigInteger("-3")), new Fraction(new BigInteger("2"))};
////        Fraction[] B = new Fraction[]{new Fraction(new BigInteger("1")), new Fraction(new BigInteger("-7")), new Fraction(new BigInteger("12"))};
////        Fraction[][] gcd = polynomial_Gcd(A, B);
////
////        Fraction[] verify0 = Polynomial_plus(Polynomial_multiply(A, gcd[1]), Polynomial_multiply(B, gcd[2]));
////
////
////        Fraction[][] input = new Fraction[4][];
////        input[0] = new Fraction[]{new Fraction(new BigInteger("1")), new Fraction(new BigInteger("-3")), new Fraction(new BigInteger("2"))};
////        input[1] = new Fraction[]{new Fraction(new BigInteger("1")), new Fraction(new BigInteger("-7")), new Fraction(new BigInteger("12"))};
////        input[2] = new Fraction[]{new Fraction(new BigInteger("1")), new Fraction(new BigInteger("-4")), new Fraction(new BigInteger("3"))};
////        input[3] = new Fraction[]{new Fraction(new BigInteger("1")), new Fraction(new BigInteger("-6")), new Fraction(new BigInteger("8"))};
////        Fraction[][] res = multi_Gcd(input);
////
////        Fraction[] verify = new Fraction[]{new Fraction(new BigInteger("0"))};
////        for(int i = 0;i < res.length - 1;i++){
////            verify = Polynomial_plus(verify, Polynomial_multiply(input[i], res[i]));
//    }
//}

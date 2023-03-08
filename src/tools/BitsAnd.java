package tools;

public class BitsAnd {
    public static boolean[] bitsAnd(boolean[] a, boolean[] b) {
        if(a.length != b.length){
            return null;
        }
        boolean[] res = new boolean[a.length];
        for(int i = 0; i < a.length; i++) {
            if(a[i] && b[i]){
                res[i] = true;
            }else{
                res[i] = false;
            }
        }
        return res;
    }
}

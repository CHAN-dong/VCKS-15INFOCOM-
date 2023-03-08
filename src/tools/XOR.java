package tools;

public class XOR {
    public static boolean[] xor(boolean[] data, String s){//s是16进制字符串，开始两个字符为0x
        boolean[] res = new boolean[data.length];
        int t = 0;
        int i = 2;
        while(true){
            if(i == s.length()){
                i = 2;
            }
            String temp = Integer.toBinaryString(Integer.valueOf(s.substring(i,i + 1),16));
            while(temp.length() != 4){
                temp = 0 + temp;
            }
            for(int j = 0;j < 4;j++){
                if(t < data.length){
                    res[t] = bool_XOR_char(data[t], temp.charAt(j));
                    t++;
                }else{
                    return res;
                }
            }
            i++;
        }
    }
    private static boolean bool_XOR_char(boolean b, char c){
        if(b){
            if(c == '1'){
                return false;
            }else{
                return true;
            }
        }else{
            if(c == '1'){
                return true;
            }else{
                return false;
            }
        }
    }
}

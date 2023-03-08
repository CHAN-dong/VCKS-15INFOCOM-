package DO;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import static tools.Parameter.N;

public class ReadDataToInvertedIndex {
    public static HashMap<Integer, boolean[]> readGeneratedData(String path) throws Exception{
        HashMap<Integer, boolean[]> res = new HashMap<>();
        File file = new File(path);
        if(file.isFile()&&file.exists()){
            InputStreamReader fla = new InputStreamReader(new FileInputStream(file));
            BufferedReader scr = new BufferedReader(fla);
            String str = null;
            while((str = scr.readLine()) != null){
                String[] data = str.split(" ");
                boolean[] index = new boolean[N];
                for(int i = 1;i < data.length;i++){
                    index[Integer.parseInt(data[i])] = true;
                }
                res.put(Integer.parseInt(data[0]), index);
            }
            scr.close();
            fla.close();
        }
        return res;
    }
//    @Test
//    public void test() throws Exception {
//        String filePath = "./src/invertedIndex.txt";
//        boolean[][] allIndex = ReadDataToInvertedIndex.readGeneratedData(filePath);
//
//    }
}

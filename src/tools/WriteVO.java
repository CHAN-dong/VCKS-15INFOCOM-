package tools;
import SP.SearchResult;
import it.unisa.dia.gas.jpbc.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteVO {
    public static long writeVOToLocal(String vo) {
        try {
            File writeName = new File("./src/vo.txt");
            writeName.createNewFile();
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(vo);
            }
            return writeName.length();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String voToStr(SearchResult res) {

        return res.toString();
    }

}

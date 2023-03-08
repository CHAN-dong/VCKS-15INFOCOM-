import DO.DO;
import DO.SearchToken;
import DO.ReadDataToInvertedIndex;
import SP.*;
import tools.WriteVO;
import tools.queryAndVerifyRes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

import static tools.WriteVO.voToStr;
import static tools.WriteVO.writeVOToLocal;

public class main {
    public static HashMap readForwardData(String path) throws Exception{
        HashMap<Integer, List<Integer>> forwardIndexMap = new HashMap<>();
        File file = new File(path);
        if(file.isFile()&&file.exists()){
            InputStreamReader fla = new InputStreamReader(new FileInputStream(file));
            BufferedReader scr = new BufferedReader(fla);
            String str = null;
            while((str = scr.readLine()) != null){
                String[] data = str.split(" ");
                List<Integer> keywords = new ArrayList<Integer>();
                for(int i = 1;i < data.length;i++){//第三个才是文档id
                    keywords.add(Integer.valueOf(data[i]));
                }
                keywords.sort(new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o1 - o2;
                    }
                });
                forwardIndexMap.put(Integer.valueOf(data[0]), keywords);
            }
            scr.close();
            fla.close();
        }
        return forwardIndexMap;
    }
    static HashSet<Integer> queryKeywords = new HashSet<>();
    public static int[] getQuery (HashMap<Integer, List<Integer>> allForwardIndex, boolean hasRes, int keywordSize) {
        int[] res = new int[keywordSize];
        int id = (int)(Math.random() * allForwardIndex.size());
        List<Integer> keywordList = allForwardIndex.get(id);
        if (hasRes) {
            while(keywordList.size() < keywordSize) {
                id = (int)(Math.random() * allForwardIndex.size());
                keywordList = allForwardIndex.get(id);
            }
            HashSet<Integer> tempSet = new HashSet<>();
            for (int i = 0; i < keywordSize; i++) {
                int randIndex = (int) (Math.random() * keywordList.size());
                while (tempSet.contains(randIndex)) {
                    randIndex = (int) (Math.random() * keywordList.size());
                }
                tempSet.add(randIndex);
                res[i] = keywordList.get(randIndex);
                queryKeywords.add(keywordList.get(randIndex));
            }
        } else {
            for (int i = 0; i < keywordSize; i++) {
                int rand;
                do {
                    rand = (int)(Math.random() * 2000) + 1;
                } while (queryKeywords.contains(rand));
                queryKeywords.add(rand);
                res[i] = rand;
            }
        }
        return res;
    }
    public static queryAndVerifyRes queryAndVerifyTime(int[] keywords, DO dataOwner, SP serviceProvider) {
        SearchToken token = dataOwner.getSearchToken(keywords);
        long startTime1 = System.nanoTime();
        SearchResult res = serviceProvider.search(token);
        long endTime1 = System.nanoTime();
//        System.out.println("查询时间:" + (endTime1 - startTime1));
        long startTime2 = System.nanoTime();
        boolean r = dataOwner.verifyRes(keywords, res);
        System.out.println(r);
        long endTime2 = System.nanoTime();
        long size = writeVOToLocal(voToStr(res));
        queryAndVerifyRes queryAndVerifyRes = new queryAndVerifyRes((endTime1 - startTime1), (endTime2 - startTime2), size, r);
        System.out.println(queryAndVerifyRes.toString());
        System.out.println();
        return queryAndVerifyRes;
    }

        public static void printTime(int round, int keywordSize, HashMap<Integer, List<Integer>> allForwardIndex, DO dataOwner, SP serviceProvider) {
        queryAndVerifyRes[] query = new queryAndVerifyRes[round];
        for (int i = 0; i < query.length; i += 2) {
            query[i] = queryAndVerifyTime(getQuery(allForwardIndex, true, keywordSize), dataOwner, serviceProvider);
            while (!query[i].isPass) {
                query[i] = queryAndVerifyTime(getQuery(allForwardIndex, true, keywordSize), dataOwner, serviceProvider);
            }
            query[i + 1] = queryAndVerifyTime(getQuery(allForwardIndex, false, keywordSize), dataOwner, serviceProvider);
            while (!query[i + 1].isPass) {
                query[i + 1] = queryAndVerifyTime(getQuery(allForwardIndex, false, keywordSize), dataOwner, serviceProvider);
            }
        }
        long sumQuery = 0;
        long sumVerify = 0;
        long sumVOSize = 0;
        double l = query.length;
        for (int i = 0; i < l; i++) {
            sumQuery += query[i].queryTime;
            sumVerify += query[i].verifyTime;
            sumVOSize += query[i].VOSize;
        }
        System.out.println("查询关键字数量" + keywordSize + "---" + "查询时间：" + (sumQuery / l) / 1000000 + "ms" + "," + "验证时间：" + (sumVerify / l) / 1000000 + "ms" + "," + "VO大小：" + (sumVOSize / l) / 1024 + "kb");
    }

    public static void main(String[] args) throws Exception {
        String k1 = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        String k2 = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
//        Element sk = Pair.Zr.newElement(2).getImmutable();
//        String filePath = "D:\\mycode\\mywork\\Dataset_processing\\test_dataset\\invertedIndex100000id5000keyword.txt";
        String filePath = "D:\\mycode\\mywork\\Dataset_processing\\test_dataset\\invertedIndex100000id1000keyword_Uniform_new_k2000.txt";
//        String forwardPath = "D:\\mycode\\mywork\\Dataset_processing\\test_dataset\\forwardIndex80000id2000keyword_Uniform_new_k2000.txt";
        String keyFilePath = "./src/allKeys.txt";
        HashMap<Integer, boolean[]> allInvertedIndex = ReadDataToInvertedIndex.readGeneratedData(filePath);
        DO dataOwner = new DO(k1, k2, keyFilePath);
        long start = System.currentTimeMillis();
        dataOwner.setup(allInvertedIndex);
        long end = System.currentTimeMillis();
        dataOwner.getAccTreeSize();
//        long start1 = System.currentTimeMillis();
//        dataOwner.setup(allInvertedIndex);
//        long end1 = System.currentTimeMillis();
        System.out.println("索引构造时间：" + (end - start) + "ms");

        dataOwner.getUpdToken(0, Arrays.asList(206), true);
        dataOwner.getUpdToken(0, Arrays.asList(206), false);






        long l = System.currentTimeMillis();
        dataOwner.getUpdToken(0, Arrays.asList(0, 977, 689, 690, 661,791, 716), true);
//        dataOwner.getUpdToken(0, Arrays.asList(0, 977, 689, 690, 661,791, 716, 1118), true);
//        dataOwner.getUpdToken(0, Arrays.asList(0, 977, 689, 690, 661,791, 716, 1118, 1214), true);
//        dataOwner.getUpdToken(0, Arrays.asList(0, 977, 689, 690, 661,791, 716, 1118, 1214, 1400), true);
//        dataOwner.getUpdToken(0, Arrays.asList(0, 977, 689, 690, 661,791, 716, 1118, 1214, 1400, 1612), true);
//        dataOwner.getUpdToken(0, Arrays.asList(0, 977, 689, 690, 661,791, 716, 1118, 1214, 1612, 1400, 1933, 1911), true);

        dataOwner.getUpdToken(1, Arrays.asList(1, 114, 482, 55, 556, 255), true);
//        dataOwner.getUpdToken(1, Arrays.asList(1, 114, 482, 55, 556, 255, 1107, 1097), true);
//        dataOwner.getUpdToken(1, Arrays.asList(1, 114, 482, 55, 556, 255, 1107, 1097, 1220, 1237), true);
//        dataOwner.getUpdToken(1, Arrays.asList(1, 114, 482, 55, 556, 255, 1107, 1097, 1220, 1237, 1485, 1473), true);
//        dataOwner.getUpdToken(1, Arrays.asList(1, 114, 482, 55, 556, 255, 1107, 1097, 1220, 1237, 1485, 1473, 1700), true);
//        dataOwner.getUpdToken(1, Arrays.asList(1, 114, 482, 55, 556, 255, 1107, 1097, 1220, 1237, 1485, 1473, 1900), true);

//        dataOwner.getUpdToken(2, Arrays.asList(), true);
        dataOwner.getUpdToken(2, Arrays.asList(2, 499, 874), true);
//        dataOwner.getUpdToken(2, Arrays.asList(2, 499, 874, 1119), true);
//        dataOwner.getUpdToken(2, Arrays.asList(2, 499, 874, 1119, 1384, 1464), true);
//        dataOwner.getUpdToken(2, Arrays.asList(2, 499, 874, 1119, 1384, 1464, 1587), true);
//        dataOwner.getUpdToken(2, Arrays.asList(2, 499, 874, 1119, 1384, 1464, 1587), true);
//        dataOwner.getUpdToken(2, Arrays.asList(2, 499, 874, 1119, 1384, 1464, 1587,1908 ,1929 ,1946 , 1934), true);


        dataOwner.getUpdToken(3, Arrays.asList(775, 711), true);
//        dataOwner.getUpdToken(3, Arrays.asList(3, 356, 197, 459, 525, 829), true);
//        dataOwner.getUpdToken(3, Arrays.asList(3, 356, 197, 459, 525, 829, 1057, 1080), true);
//        dataOwner.getUpdToken(3, Arrays.asList(3, 356, 197, 459, 525, 829, 1057, 1080), true);
//        dataOwner.getUpdToken(3, Arrays.asList(3, 356, 197, 459, 525, 829, 1057, 1080, 1421), true);
//        dataOwner.getUpdToken(3, Arrays.asList(3, 356, 197, 459, 525, 829, 1057, 1080, 1421, 1618,1636), true);
//        dataOwner.getUpdToken(3, Arrays.asList(3, 356, 197, 459, 525, 829, 1057, 1080, 1421, 1618,1636, 1900), true);


//        dataOwner.getUpdToken(4, Arrays.asList(), true);
        dataOwner.getUpdToken(4, Arrays.asList(875), true);
//        dataOwner.getUpdToken(4, Arrays.asList(875, 1048, 1065, 1178, 1119), true);
//        dataOwner.getUpdToken(4, Arrays.asList(875, 1048, 1065, 1178, 1119, 1381), true);
//        dataOwner.getUpdToken(4, Arrays.asList(875, 1048, 1065, 1178, 1119, 1381, 1403, 1458), true);
//        dataOwner.getUpdToken(4, Arrays.asList(875, 1048, 1065, 1178, 1119, 1381, 1403, 1458, 1617), true);
//        dataOwner.getUpdToken(4, Arrays.asList(875, 1048, 1065, 1178, 1119, 1381, 1403, 1458, 1617, 1867), true);
//        for (int i = 0; i < 6 + 5 + 2 + 5 + 1 + 10 + 5 + 7 + 4 + 7; ++i) {
//            dataOwner.getUpdToken(10, 206 + i, "a");
//        }
        long e = System.currentTimeMillis();
        System.out.println("更新令牌计算时间：" + (e - l) / 5  + "ms");



//        SP serviceProvider = new SP(dataOwner.getPk(), dataOwner.getPai_accMap(), dataOwner.getSecureIndex());
//        HashMap<Integer, List<Integer>> allForwardIndex = readForwardData(forwardPath);
        //查询关键字
//        System.out.println("test-------------------------------------");
//        int[] keywords2 = {1,2};
//        int[] keywords3 = {1,2,3};
//        int[] keywords4 = {1,2,3,4};
//        int[] keywords5 = {1,2,3,4,5};
//        int[] keywords6 = {1,2,3,4,5,6};
//        int[] keywords7 = {1,2,3,4,5,6,7};
//        int[] keywords8 = {1,2,3,4,5,6,7,8};
//        int[] keywords9 = {1,2,3,4,5,6,7,8,9};
//        int[] keywords10 = {1,2,3,4,5,6,7,8,9,10};
//
//        for (int i = 0; i < 10; ++i) {
//            dataOwner.getSearchToken(keywords10);
//        }
//
//        int allTime = 0;
//        for (int i = 0; i < 10; ++i) {
//            long s1 = System.currentTimeMillis();
//            dataOwner.getSearchToken(keywords2);
//            long s2 = System.currentTimeMillis();
//            allTime += s2 - s1;
//        }
//        System.out.println("2个关键字令牌生成时间：" + allTime / 10.0 + "ns");
//
//        allTime = 0;
//        for (int i = 0; i < 10; ++i) {
//            long s1 = System.nanoTime();
//            dataOwner.getSearchToken(keywords3);
//            long s2 = System.nanoTime();
//            allTime += s2 - s1;
//        }
//        System.out.println("3个关键字令牌生成时间：" + allTime / 10.0 + "ns");
//
//        allTime = 0;
//        for (int i = 0; i < 10; ++i) {
//            long s1 = System.nanoTime();
//            dataOwner.getSearchToken(keywords4);
//            long s2 = System.nanoTime();
//            allTime += s2 - s1;
//        }
//        System.out.println("4个关键字令牌生成时间：" + allTime / 10.0 + "ns");
//
//        allTime = 0;
//        for (int i = 0; i < 10; ++i) {
//            long s1 = System.nanoTime();
//            dataOwner.getSearchToken(keywords5);
//            long s2 = System.nanoTime();
//            allTime += s2 - s1;
//        }
//        System.out.println("5个关键字令牌生成时间：" + allTime / 10.0 + "ns");
//
//        allTime = 0;
//        for (int i = 0; i < 10; ++i) {
//            long s1 = System.nanoTime();
//            dataOwner.getSearchToken(keywords6);
//            long s2 = System.nanoTime();
//            allTime += s2 - s1;
//        }
//        System.out.println("6个关键字令牌生成时间：" + allTime / 10.0 + "ns");
//
//        allTime = 0;
//        for (int i = 0; i < 10; ++i) {
//            long s1 = System.nanoTime();
//            dataOwner.getSearchToken(keywords7);
//            long s2 = System.nanoTime();
//            allTime += s2 - s1;
//        }
//        System.out.println("7个关键字令牌生成时间：" + allTime / 10.0 + "ns");
//
//        allTime = 0;
//        for (int i = 0; i < 10; ++i) {
//            long s1 = System.nanoTime();
//            dataOwner.getSearchToken(keywords8);
//            long s2 = System.nanoTime();
//            allTime += s2 - s1;
//        }
//        System.out.println("8个关键字令牌生成时间：" + allTime / 10.0 + "ns");
//
//        allTime = 0;
//        for (int i = 0; i < 10; ++i) {
//            long s1 = System.nanoTime();
//            dataOwner.getSearchToken(keywords9);
//            long s2 = System.nanoTime();
//            allTime += s2 - s1;
//        }
//        System.out.println("9个关键字令牌生成时间：" + allTime / 10.0 + "ns");
//
//        allTime = 0;
//        for (int i = 0; i < 10; ++i) {
//            long s1 = System.nanoTime();
//            dataOwner.getSearchToken(keywords10);
//            long s2 = System.nanoTime();
//            allTime += s2 - s1;
//        }
//        System.out.println("10个关键字令牌生成时间：" + allTime / 10.0 + "ns");

//        printTime(2, 2, allForwardIndex, dataOwner, serviceProvider);
//        System.out.println("real-------------------------------------");
//        printTime(4, 2, allForwardIndex, dataOwner, serviceProvider);
//        printTime(4, 3, allForwardIndex, dataOwner, serviceProvider);
//        printTime(4, 4, allForwardIndex, dataOwner, serviceProvider);
//        printTime(4, 5, allForwardIndex, dataOwner, serviceProvider);
//        printTime(4, 6, allForwardIndex, dataOwner, serviceProvider);
//        printTime(4, 7, allForwardIndex, dataOwner, serviceProvider);
//        printTime(4, 8, allForwardIndex, dataOwner, serviceProvider);
//        printTime(4, 9, allForwardIndex, dataOwner, serviceProvider);
//        printTime(4, 10, allForwardIndex, dataOwner, serviceProvider);

//        queryAndVerifyRes[] query2 = new queryAndVerifyRes[4];
//        int[] keywords21 = new int[]{2688, 911};
//        query2[0] = queryAndVerifyTime(keywords21, dataOwner, serviceProvider);
//        int[] keywords22 = new int[]{1049, 4152};
//        query2[1] = queryAndVerifyTime(keywords22, dataOwner, serviceProvider);
//        int[] keywords23 = new int[]{91, 92};
//        query2[2] = queryAndVerifyTime(keywords23, dataOwner, serviceProvider);
//        int[] keywords24 = new int[]{75, 76};
//        query2[3] = queryAndVerifyTime(keywords24, dataOwner, serviceProvider);
//        System.out.print("查询2关键字:");
//        printTime(query2);
//
//        queryAndVerifyRes[] query3 = new queryAndVerifyRes[4];
//        int[] keywords31 = new int[]{2298, 3241, 1988};
//        query3[0] = queryAndVerifyTime(keywords31, dataOwner, serviceProvider);
//        int[] keywords32 = new int[]{4326, 3002, 3214};
//        query3[1] = queryAndVerifyTime(keywords32, dataOwner, serviceProvider);
//        int[] keywords33 = new int[]{482, 44, 443};
//        query3[2] = queryAndVerifyTime(keywords33, dataOwner, serviceProvider);
//        int[] keywords34 = new int[]{705, 706, 1002};
//        query3[3] = queryAndVerifyTime(keywords34, dataOwner, serviceProvider);
//        System.out.print("查询3关键字:");
//        printTime(query3);
//
//        queryAndVerifyRes[] query4 = new queryAndVerifyRes[4];
//        int[] keywords41 = new int[]{1115, 775, 711, 2662};
//        query4[0] = queryAndVerifyTime(keywords41, dataOwner, serviceProvider);
//        int[] keywords42 = new int[]{2115, 4339, 400, 4538};
//        query4[1] = queryAndVerifyTime(keywords42, dataOwner, serviceProvider);
//        int[] keywords43 = new int[]{263, 44, 20, 11};
//        query4[2] = queryAndVerifyTime(keywords43, dataOwner, serviceProvider);
//        int[] keywords44 = new int[]{262, 96, 207, 12};
//        query4[3] = queryAndVerifyTime(keywords44, dataOwner, serviceProvider);
//        System.out.print("查询4关键字:");
//        printTime(query4);
//
//        queryAndVerifyRes[] query5 = new queryAndVerifyRes[4];
//        int[] keywords51 = new int[]{4450, 375, 3947, 1801, 395};
//        query5[0] = queryAndVerifyTime(keywords51, dataOwner, serviceProvider);
//        int[] keywords52 = new int[]{4942, 2102, 3441, 2270, 423};
//        query5[1] = queryAndVerifyTime(keywords52, dataOwner, serviceProvider);
//        int[] keywords53 = new int[]{263, 3044, 2030, 1401, 120};
//        query5[2] = queryAndVerifyTime(keywords53, dataOwner, serviceProvider);
//        int[] keywords54 = new int[]{262, 946, 2307, 1042,103};
//        query5[3] = queryAndVerifyTime(keywords54, dataOwner, serviceProvider);
//        System.out.print("查询5关键字:");
//        printTime(query5);
//
//        queryAndVerifyRes[] query6 = new queryAndVerifyRes[4];
//        int[] keywords61 = new int[]{4581, 308, 3459, 2267, 4676, 4839};
//        query6[0] = queryAndVerifyTime(keywords61, dataOwner, serviceProvider);
//        int[] keywords62 = new int[]{791, 3637, 989, 1883, 994, 2174};
//        query6[1] = queryAndVerifyTime(keywords62, dataOwner, serviceProvider);
//        int[] keywords63 = new int[]{1700, 1001, 1102, 1106, 1039, 1600};
//        query6[2] = queryAndVerifyTime(keywords63, dataOwner, serviceProvider);
//        int[] keywords64 = new int[]{4000, 1106, 200, 1100, 4700, 2102};//
//        query6[3] = queryAndVerifyTime(keywords64, dataOwner, serviceProvider);
//        System.out.print("查询6关键字:");
//        printTime(query6);
//
//        queryAndVerifyRes[] query7 = new queryAndVerifyRes[4];
//        int[] keywords71 = new int[]{4439, 2492, 1075, 4509, 3188, 838, 939};
//        query7[0] = queryAndVerifyTime(keywords71, dataOwner, serviceProvider);
//        int[] keywords72 = new int[]{145, 1634, 3420, 2670, 104, 2646, 4489};
//        query7[1] = queryAndVerifyTime(keywords72, dataOwner, serviceProvider);
//        int[] keywords73 = new int[]{279, 4872, 4971, 747, 3700, 2726, 3079};//
//        query7[2] = queryAndVerifyTime(keywords73, dataOwner, serviceProvider);
//        int[] keywords74 = new int[]{4000, 1016, 2020, 1010, 4700, 2012, 153};
//        query7[3] = queryAndVerifyTime(keywords74, dataOwner, serviceProvider);
//        System.out.print("查询7关键字:");
//        printTime(query7);
//
//        queryAndVerifyRes[] query8 = new queryAndVerifyRes[4];
//        int[] keywords81 = new int[]{933, 3101, 880, 4394, 2081, 2696, 3933, 919};
//        query8[0] = queryAndVerifyTime(keywords81, dataOwner, serviceProvider);
//        int[] keywords82 = new int[]{4737, 727, 1334, 3663, 1242, 1724, 4126, 1159};
//        query8[1] = queryAndVerifyTime(keywords82, dataOwner, serviceProvider);
//        int[] keywords83 = new int[]{1003, 1018, 2076, 1300, 1270, 700, 302, 606};
//        query8[2] = queryAndVerifyTime(keywords83, dataOwner, serviceProvider);
//        int[] keywords84 = new int[]{4190, 1016, 2172, 1700, 805, 4230, 4020, 807};
//        query8[3] = queryAndVerifyTime(keywords84, dataOwner, serviceProvider);
//        System.out.print("查询8关键字:");
//        printTime(query8);
//
//        queryAndVerifyRes[] query9 = new queryAndVerifyRes[4];
//        int[] keywords91 = new int[]{1352, 3623, 4779, 4982, 4648, 3365, 634, 2998, 2414};
//        query9[0] = queryAndVerifyTime(keywords91, dataOwner, serviceProvider);
//        int[] keywords92 = new int[]{4159, 4421, 4291, 2201, 981, 490, 1951, 3122, 1759};
//        query9[1] = queryAndVerifyTime(keywords92, dataOwner, serviceProvider);
//        int[] keywords93 = new int[]{123, 1128, 572, 1320, 1272, 722, 322, 3226, 122};
//        query9[2] = queryAndVerifyTime(keywords93, dataOwner, serviceProvider);
//        int[] keywords94 = new int[]{410, 1256, 2272, 2200, 1925, 4227, 4222, 827, 2222};
//        query9[3] = queryAndVerifyTime(keywords94, dataOwner, serviceProvider);
//        System.out.print("查询9关键字:");
//        printTime(query9);
//
//        queryAndVerifyRes[] query0 = new queryAndVerifyRes[4];
//        int[] keywords01 = new int[]{2190, 4175, 4530, 2432, 4659, 1938, 2855, 1956, 1364, 2318};
//        query0[0] = queryAndVerifyTime(keywords01, dataOwner, serviceProvider);
//        int[] keywords02 = new int[]{2900, 3508, 2055, 3641, 2355, 2937, 3532, 2164, 823, 3375};
//        query0[1] = queryAndVerifyTime(keywords02, dataOwner, serviceProvider);
//        int[] keywords03 = new int[]{1222, 138, 2330, 1340, 3110, 3398, 3302, 3857, 3340, 3334};
//        query0[2] = queryAndVerifyTime(keywords03, dataOwner, serviceProvider);
//        int[] keywords04 = new int[]{2401, 1148, 3540, 1420, 3410, 4398, 4212, 847, 3440, 4934};
//        query0[3] = queryAndVerifyTime(keywords04, dataOwner, serviceProvider);
//        System.out.print("查询10关键字:");
//        printTime(query0);
    }


}

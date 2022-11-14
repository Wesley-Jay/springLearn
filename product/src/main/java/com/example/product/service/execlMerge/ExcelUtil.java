package com.example.product.service.execlMerge;

import org.apache.commons.collections4.ListUtils;
import org.apache.poi.util.StringUtil;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * @author wsj
 * @implNote execl公用类
 * @date 2022/10/27
 */
public class ExcelUtil {

    public static InputStream getResourcesFileInputStream(String fileName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("" + fileName);
    }

    public static String getPath() {
        return Objects.requireNonNull(ExcelUtil.class.getResource("/")).getPath();
    }

    public static File createNewFile(String pathName) {
        File file = new File(getPath() + pathName);
        if (file.exists()) {
            if (file.delete()) {
                throw new RuntimeException("文件删除失败");
            }
        } else {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        }
        return file;
    }

    public static File readFile(String pathName) {
        return new File(getPath() + pathName);
    }

    public static File readUserHomeFile(String pathName) {
        return new File(System.getProperty("user.home") + File.separator + pathName);
    }

    public List<List<String>> data2() {
        List<List<String>> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(Arrays.asList(getRandomNumbers(), getRandomNumbers(),getRandomNumbers(), getRandomNumbers(), getRandomNumbers()));
        }
        return list;
    }


    private String getRandomNumbers(){
        int number =(int)(Math.random()*100+1);
        return String.valueOf(number) ;
    }

    private List<String> makeUpTitle(List<String> titleList){
        List<String> makeUpList = new ArrayList<>();
        String temp = "";
        for (String e : ListUtils.emptyIfNull(titleList)) {
            if (Objects.nonNull(e)) {
                temp = e;
            }
            makeUpList.add(temp);
        }

        return makeUpList;
    }

    @Test
    public void testMakeUpHeader() {
        List<List<String>> titleList = new ArrayList<>();
        List<String> titleOne = Arrays.asList("Plant","CAMA Line","Line Qty","2021","2021","2021","2021","2022",
                "2022","2022","2022","2023","2023","2023","2023","2024","2024","2024","2024","2025","2025",
                "2025","2025","2026","2026","2026","2026","2027","2027","2027","2027");
        List<String> titleTwo =Arrays.asList("Plant","CAMA Line","Line Qty",
                "Forecasted annual demand(K)","Yearly Increase (vs Year - 1)","Yearly Capacity(K)","LOAD RATE Annual Average (Standard)",
                "Forecasted annual demand(K)","Yearly Increase (vs Year - 1)","Yearly Capacity(K)","LOAD RATE Annual Average (Standard)",
                "Forecasted annual demand(K)","Yearly Increase (vs Year - 1)","Yearly Capacity(K)","LOAD RATE Annual Average (Standard)",
                "Forecasted annual demand(K)","Yearly Increase (vs Year - 1)","Yearly Capacity(K)","LOAD RATE Annual Average (Standard)");
        titleList.add(makeUpTitle(titleOne));
        titleList.add(makeUpTitle(titleTwo));
        System.out.println("make up title: "+titleList.toString() );
    }

    private List<List<String>> rotateHeadFields(List<List<String>> headFields) {
        List<List<String>> result = new ArrayList<>();
        for (List<String> row : headFields) {
            for (int j = 0; j < row.size(); j++) {
                if (result.size() > j) {
                    // 往对应第j个List<String> 添加添加值
                    result.get(j).add(row.get(j));
                } else {
                    // 分割成单个List<String>
                    result.add(new ArrayList<>(Collections.singletonList(row.get(j))));
                }
            }
        }
        return result;
    }
}

package com.example.product.service.costomExport;

import com.alibaba.excel.util.DateUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.*;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * @author wsj
 * @implNote 用于读取模板，向模板中写入数据，另存为到新文件。
 * @date 2022/11/10
 */
public class FormTemplateExcel {
    Logger log = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        new FormTemplateExcel().generateRiskReport();
    }

    private void generateRiskReport(){
        InputStream in = null;
        XSSFWorkbook wb = null;
        try {
            //***读取Excel模板
            String filePath = "/Users/wesley/Downloads/SynthesisExport.xlsx";
            File file = new File(filePath);
            if (!file.exists()) {
                throw new RuntimeException("模版文件不存在");
            }
            in = new FileInputStream(file);
            wb = new XSSFWorkbook(in);
            //****填入数据************************************/
            generateReport(wb);
            //***另存为新文件*********************************/
            LocalDate localDate = LocalDate.now();
            String filename = "Synthesis" + System.currentTimeMillis() + ".xlsx";
            saveExcelToDisk(wb, "/Users/wesley/Downloads/" + filename);

        } catch (IOException e) {
            log.error("error", e);
        } finally {
            try {if(wb!=null) {
                wb.close();
            }
            } catch (IOException e) { log.error("error", e);}
            try {if(in!=null) {
                in.close();
            }
            } catch (IOException e) { log.error("error", e);}
        }
    }

    private void generateReport(XSSFWorkbook wb) {
        XSSFSheet sheet1 = wb.getSheetAt(0);
        //XSSFSheet sheet2 = wb.getSheetAt(1);
        // 设置公式自动读取，没有这行代码，excel模板中的公式不会自动计算
        sheet1.setForceFormulaRecalculation(true);
        //sheet2.setForceFormulaRecalculation(true);

        //设置单个单元格内容
        FormExcelUtil.setCellData(sheet1, "2022-11", 3, 1);
        FormExcelUtil.setCellData(sheet1, "GTN", 4, 2);
        FormExcelUtil.setCellData(sheet1, "10086", 5, 2);
        //左边表格
        ExampleData ea = new ExampleData();
        List<List<Object>> firstList = ea.getData1();
        int addRows=0;
        //动态插入行
        //FormExcelUtil.insertRowsStyleBatch(sheet, startNum, insertRows, styleRow, styleColStart, styleColEnd)
        //按照styleRow行的格式，在startNum行后添加insertRows行，并且针对styleColStart~ styleColEnd列同步模板行styleRow的格式
        //FormExcelUtil.insertRowsStyleBatch(sheet1, 4+addRows, data1.size()-2, 4, 1, 4);
        //FormExcelUtil.setTableData(sheet1, data1, 4+addRows, 1);

        FormExcelUtil.copyRowsStyle(sheet1, 12+addRows, firstList.size()-1, 12, 1, 8);
        //右边表格
        List<List<Object>> data2 = ea.getData1();
        FormExcelUtil.rightGirdStyle(sheet1, 12, data2.size()-1, 12, 10, 17);
        FormExcelUtil.setTableData(sheet1, firstList, 12+addRows, 1);
        FormExcelUtil.setTableData(sheet1, data2, 12, 10);
        //用于下方还有表格时记录的游标
        //addRows += firstList.size()-2;

        //addRows += data2.size()-2;
        ///***第三个表格*********************************/
        //List<List<Object>> data3 = ea.getData3();
        //FormExcelUtil.setTableData(sheet2, data3, 3, 1);

    }

    private void saveExcelToDisk(XSSFWorkbook wb, String filePath){
        File file = new File(filePath);
        OutputStream os=null;
        try {
            os = new FileOutputStream(file);
            wb.write(os);
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(os!=null){
                    os.close();
                }
            } catch (IOException e) {
                log.error("error", e);
            }
        }
    }
}
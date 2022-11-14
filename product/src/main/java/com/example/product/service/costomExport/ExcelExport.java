package com.example.product.service.costomExport;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.yaml.snakeyaml.events.Event;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wsj
 * @implNote 自定义导出
 * @date 2022/11/7
 */
public class ExcelExport {
    private SXSSFWorkbook sxssfWorkbook;
    private SXSSFSheet sheet;
    private String bDate;
    private int year;

    /**
     * 创建行元素
     * @param style    样式
     * @param height   行高
     * @param value    行显示的内容
     * @param row1     起始行
     * @param row2     结束行
     * @param col1     起始列
     * @param col2     结束列
     */
    private void createRow(CellStyle style, int height, String value, int row1, int row2, int col1, int col2){

        sheet.addMergedRegion(new CellRangeAddress(row1, row2, col1, col2));  //设置从第row1行合并到第row2行，第col1列合并到col2列
        Row rows = sheet.createRow(row1);        //设置第几行
        rows.setHeight((short) height);              //设置行高
        Cell cell = rows.createCell(col1);       //设置内容开始的列
        cell.setCellStyle(style);                    //设置样式
        cell.setCellValue(value);                    //设置该行的值
    }

    /**
     * 创建样式
     * @param fontSize   字体大小
     * @param align  水平位置  左右居中2 居右3 默认居左 垂直均为居中
     * @param bold   是否加粗
     * @return
     */
    private CellStyle getStyle(int fontSize,int align,boolean bold,boolean border){
        Font font = sxssfWorkbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) fontSize);// 字体大小
        font.setBold(bold);
        CellStyle style = sxssfWorkbook.createCellStyle();
        style.setFont(font);                         //设置字体
        return style;
    }

    /**
     * 根据数据集生成Excel，并返回Excel文件流
     * @param data 数据集
     * @param sheetName Excel中sheet单元名称
     * @param headNames 列表头名称数组
     * @param colKeys 列key,数据集根据该key进行按顺序取值
     * @return
     * @throws IOException
     */
    public SXSSFWorkbook getExcelFile(List<Map> data, String sheetName, String[] headNames,
                                    String[] colKeys, int colWidths[], String bDate) throws IOException {
        this.bDate = bDate;
        sxssfWorkbook = new SXSSFWorkbook();
        sheet = sxssfWorkbook.createSheet(sheetName);
        // 创建表头 startRow代表表体开始的行
        int startRow = createHeadCell(headNames);

        // 创建表体数据
        CellStyle cellStyle = getStyle(14,2,false,true); // 建立新的cell样式
        setCellData(data, cellStyle, startRow, colKeys);

        return sxssfWorkbook;
    }

    /**
     * 创建表头
     *
     * @param headNames
     */
    private int createHeadCell( String[] headNames) {
        int dataLength = headNames.length;
        int totalLength = (headNames.length) * 2;
        CellStyle unitStyle = getStyle(12,1,true,false);
        createRow(unitStyle,0x190,"Synthesis:" + bDate,0,0,0,totalLength);
        CellStyle towStyle = getStyle(12,1,true,false);
        createRow(towStyle,0x190,"GSC Code Plant:",1,1,0,totalLength);
        //第三行左边部分
        year = Integer.parseInt(bDate.substring(0,4));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, dataLength-1));
        Row row = sheet.createRow(2);
        row.setHeight((short) 0x190);
        Cell cell = row.createCell(0);
        cell.setCellStyle(getStyle(12,1,true,false));
        cell.setCellValue("第一年");

        //第三行右边部分
        sheet.addMergedRegion(new CellRangeAddress(2, 2, dataLength+1, totalLength));
        Cell cell2 = row.createCell(dataLength+1);
        cell2.setCellStyle(getStyle(12,3,true,false));
        cell2.setCellValue("第二年");

        //第四行表头
        boolean b = headNames.length > 0;
        if (b) {
            Row row2 = sheet.createRow(3);
            row2.setHeight((short) 0x289);
            int index = 0;
            Cell gridCell;
            for (int i = 0; i < headNames.length; i++) {
                gridCell = row2.createCell(index);
                gridCell.setCellValue(headNames[i]);
                sheet.setColumnWidth(index, 200);
                index ++;
            }
            if (totalLength > index ) {
                //渲染第二个表格
                index ++ ;
                gridCell = row2.createCell(index);
                gridCell.setCellValue("");
                sheet.setColumnWidth(index, 200);
                index ++;
                for (int i = 0; i < headNames.length; i++) {
                    gridCell = row2.createCell(index);
                    gridCell.setCellValue(headNames[i]);
                    sheet.setColumnWidth(index, 200);
                    index ++;
                }
            }
        }
        return b ? 4 : 3;  //从哪一行开始渲染表体
    }

    /**
     * 创建表体数据
     * @param data           表体数据
     * @param cellStyle      样式
     * @param startRow       开始行
     * @param colKeys        值对应map的key
     */
    private void setCellData(List<Map> data, CellStyle cellStyle, int startRow,
                             String[] colKeys) {
        // 创建数据
        Row row = null;
        Cell cell = null;
        int i = startRow;

        if (data != null && data.size() > 0) {
            DecimalFormat df = new DecimalFormat("#0.00");
            for (Map<String, Object> rowData : data) {
                row = sheet.createRow(i);
                row.setHeight((short) 0x279);
                int j = 0;
                for (String key : colKeys) {
                    Object colValue = rowData.get(key);
                    if (key.equalsIgnoreCase("CITYNAME")){
                        colValue = colValue+"XX科技有限公司";
                    }else if (key.equalsIgnoreCase("ORDERSUM")||key.equalsIgnoreCase("TRANSFEE")||key.equalsIgnoreCase("ORDREALSUM")){
                        colValue = df.format(colValue);
                    }
                    cell = row.createCell(j);
                    cell.setCellStyle(cellStyle);
                    if (colValue != null) {
                        cell.setCellType(CellType.STRING);
                        cell.setCellValue(colValue.toString());
                    }
                    j++;
                }
                i++;
            }
        }
    }

    /**
     * 创建表尾
     * @param size
     * @param length
     */
    private void createTailCell(int size, int length) {
        CellStyle remarkStyle1 = getStyle(11,1,false,false);
        createRow(remarkStyle1,0x190,"经核对，确认以上数据真实无误。",size,size,0,length-2);

        CellStyle remarkStyle2 = getStyle(10,1,false,false);
        createRow(remarkStyle2,0x160,"(联系人：XXX；联系电话：13xxxxxxxx；邮箱:123456789@qq.com)",size+1,size+1,0,length-2);

        Row row3 = sheet.createRow(size+2);
        row3.setHeight((short) 0x379);

        sheet.addMergedRegion(new CellRangeAddress(size+3, size+3, 0, 1));
        Row row4 = sheet.createRow(size+3);
        row4.setHeight((short) 0x190);
        Cell cell4 = row4.createCell(0);
        cell4.setCellStyle(getStyle(11,1,false,false));
        cell4.setCellValue("单位核对人：");

        sheet.addMergedRegion(new CellRangeAddress(size+3, size+3, 2, 4));
        Cell cell15 = row4.createCell(2);
        cell15.setCellStyle(getStyle(11,1,false,false));
        cell15.setCellValue("单位制表人：");

        CellStyle dateStyle = getStyle(10,3,false,false);
        createRow(dateStyle,0x150,"公司公章                     ",size+8,size+8,0,length-2);

        createRow(dateStyle,0x150,year+"年  月   日",size+9,size+9,0,length-2);

    }


    // 测试
    public static void main(String[] args) throws IOException {
        ExcelExport excel = new ExcelExport();
        List<Map> data = new ArrayList<>();

        LinkedHashMap<String, Object> e = new LinkedHashMap<>();

        e.put("Line or Cell Name", "北京");
        e.put("NEE/KER", "65");
        e.put("Load ratio with current organization", 930.38);
        e.put("Load ratio SPS", 2.28);
        e.put("Load ratio Annual Average", 928.10);
        e.put("Process Bottleneck", 4);
        e.put("Main Action Items or Comments", 5);
        e.put("Load Ratio Target", 6);
        data.add(e);
        e = new LinkedHashMap<>();
        e.put("Line or Cell Name", "上海");
        e.put("NEE/KER", "73");
        e.put("Load ratio with current organization", 1930.38);
        e.put("Load ratio SPS", 32.28);
        e.put("Load ratio Annual Average", 4928.10);
        e.put("Process Bottleneck", 1);
        e.put("Main Action Items or Comments", 2);
        e.put("Load Ratio Target", 3);
        data.add(e);


        String[] headNames =  {"Line or Cell Name","NEE/KER","Load ratio with current organization",
                "Load ratio SPS","Load ratio Annual Average","Process Bottleneck","Main Action Items or Comments","Load Ratio Target"};
        int colWidths[] = { 300, 200, 200, 200, 200,300 };

        String bDate = "201708";
        SXSSFWorkbook book = excel.getExcelFile(data, "单位", headNames, headNames, colWidths,bDate);

        File f = new File("/Users/wesley/Downloads/excel.xlsx");
        if (f.exists()){
            f.delete();
        }
        f.createNewFile();
        FileOutputStream out = new FileOutputStream(f);
        BufferedOutputStream outputStream = new BufferedOutputStream(out);
        book.write(outputStream);
        out.flush();
        outputStream.flush();
        book.dispose();
    }


    /**
     * 替换Excel模板文件内容
     *
     * @param map     需要替换的标签建筑队形式
     * @param intPath Excel模板文件路径
     * @param outPath Excel生成文件路径
     */
    public static void replaceSheetsModel(Map map,String intPath,String outPath) {
        try {
            FileInputStream fs = new FileInputStream(intPath);
            XSSFWorkbook workbook = new XSSFWorkbook(fs);
            XSSFWorkbook wb = (XSSFWorkbook) workbook;
            XSSFSheet sheet;
            for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                sheet = workbook.getSheetAt(j);
                Iterator rows = sheet.rowIterator();
                while (rows.hasNext()) {
                    XSSFRow row = (XSSFRow) rows.next();
                    if (row != null) {
                        int num = row.getLastCellNum();
                        for (int i = 0; i < num; i++) {
                            XSSFCell cell = row.getCell(i);
                            if (cell != null) {
                                cell.setCellType(CellType.BLANK);
                            }
                            if (cell == null || cell.getStringCellValue() == null) {
                                continue;
                            }
                            String value = cell.getStringCellValue();
                            if (!"".equals(value)) {
                                Set<String> keySet = map.keySet();
                                Iterator<String> it = keySet.iterator();
                                while (it.hasNext()) {
                                    String text = it.next();
                                    if (value.equalsIgnoreCase(text)) {
                                        cell.setCellValue((String) map.get(text));
                                        break;
                                    }
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        }
                    }
                }
            }
            FileOutputStream fileOut = new FileOutputStream(outPath);
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}

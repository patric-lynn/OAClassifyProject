package Inversion;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArffToExcel {
    /**
     * 将Arff文件写入Excel表中
     *
     * @param arffFilePath
     * @param excelFilePath
     */
    public static void ArffToExcel(String arffFilePath,String  excelFilePath){
        System.out.println("AAA");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("重要分类表");
        HSSFRow row ;
        //创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        List<String> res = readArff(arffFilePath);
        if(res==null || res.size()==0) return ;
        int start=0,end=res.size();
        while(start<res.size() &&(res.get(start).trim().startsWith("@")||res.get(start).trim().equals("")))
            start++;

        //列标题
//        String[] cellArray = res.get(start++).split(",");
//        for(int i=0;i<cellArray.length;i++){
//            HSSFCell cell=row.createCell(i);
//            cell.setCellValue(cellArray[i]);
//            cell.setCellStyle(style);
//        }

//        勿删
//        HSSFCell cell = row.createCell(0);
//        cell.setCellValue("重要程度");
//        cell.setCellStyle(style);
//        cell = row.createCell(1);
//        cell.setCellValue("来文字号");
//        cell.setCellStyle(style);
//        cell = row.createCell(2);
//        cell.setCellValue("来文事由");
//        cell.setCellStyle(style);
//        cell = row.createCell(3);
//        cell.setCellValue("来文单位");
//        cell.setCellStyle(style);
//        cell = row.createCell(4);
//        cell.setCellValue("流程名");
//        cell.setCellStyle(style);
//        cell = row.createCell(5);
//        cell.setCellValue("文种");
//        cell.setCellStyle(style);
//        cell = row.createCell(6);
//        cell.setCellValue("年份");
//        cell.setCellStyle(style);
//        cell = row.createCell(7);
//        cell.setCellValue("文号");
//        cell.setCellStyle(style);
//        cell = row.createCell(8);
//        cell.setCellValue("交办日期");
//        cell.setCellStyle(style);
//        cell = row.createCell(9);
//        cell.setCellValue("承办期限");
//        cell.setCellStyle(style);
//        cell = row.createCell(10);
//        cell.setCellValue("主办");
//        cell.setCellStyle(style);
//        cell = row.createCell(11);
//        cell.setCellValue("主办2");
//        cell.setCellStyle(style);
//        cell = row.createCell(12);
//        cell.setCellValue("文号");
//        cell.setCellStyle(style);
//        cell = row.createCell(13);
//        cell.setCellValue("创建日期");
//        cell.setCellStyle(style);
//        cell = row.createCell(14);
//        cell.setCellValue("创建时间");
//        cell.setCellStyle(style);

        for (int i=0; start < end; start++,i++)
        {
            row = sheet.createRow((int) i );
            String[] rows=res.get(start).split(",");
            for(int j=0;j<rows.length;j++){
                row.createCell(j).setCellValue(rows[j]);
            }
        }
        //将文件存到指定位置
        try
        {
            FileOutputStream fout = new FileOutputStream(excelFilePath);
            workbook.write(fout);
            fout.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 将Arff文件写入List中
     *
     * @param arffFilePath
     */
    public static List<String> readArff(String arffFilePath){
        // 使用ArrayList来存储每行读取到的字符串
        List<String> arrayList = new ArrayList<String>();
        try {
            File file = new File(arffFilePath);
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bf = new BufferedReader(inputReader);
            // 按行读取字符串
            String str;
            while ((str = bf.readLine()) != null) {
                arrayList.add(str);
            }
            bf.close();
            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
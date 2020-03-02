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
        System.out.println("开始注入excel");
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

        for (int i=0; start < end; start++,i++)
        {
            row = sheet.createRow((int) i );
            String[] rows=res.get(start).split(",");
            for(int j=0,m=0;j<rows.length;m++){
                if(m==1 && ExcelToCsv.requstIdList!=null && ExcelToCsv.requstIdList.size()>0){
                    row.createCell(m).setCellValue(Integer.toString(ExcelToCsv.requstIdList.remove(0)));
                    continue;
                }
                row.createCell(m).setCellValue(rows[j++]);
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
            InputStream inputStream=CsvToArff.class.getResourceAsStream(arffFilePath);
            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
            // 按行读取字符串
            String str;
            while ((str = bf.readLine()) != null) {
                arrayList.add(str);
            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
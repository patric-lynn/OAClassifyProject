package Inversion;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelToCsv {


    /**
     * 将excel表格转成csv格式
     *
     * @param oldFilePath
     * @param newFilePath
     */
    public static void excelToCsv(String oldFilePath, String newFilePath) {
        System.out.println("start invert...");
        String buffer = "";
        Workbook wb = null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String, String>> list = null;
        String cellData = null;
        String filePath = oldFilePath;

        wb = readExcel(filePath);
        if (wb != null) {
            //用来存放表中数据
            list = new ArrayList<Map<String, String>>();
            //获取第一个sheet
            sheet = wb.getSheetAt(0);
            //获取最大行数
            int rownum = sheet.getPhysicalNumberOfRows();
            //获取第一行
            row = sheet.getRow(0);
            //获取最大列数
            int colnum = row.getPhysicalNumberOfCells();
            for (int i = 0; i < rownum; i++) {
                row = sheet.getRow(i);
                for (int j = 0; j < colnum; j++) {

                    cellData = ((String) getCellFormatValue(row.getCell(j)));

                    if(cellData.equals("")|| cellData==null){
                        cellData="?,";
                    }
                    buffer += cellData;
                }
                buffer = buffer.substring(0, buffer.lastIndexOf(",")).toString();
                buffer += "\n";

            }

            String savePath = newFilePath;
            File saveCSV = new File(savePath);
            try {
                if (!saveCSV.exists())
                    saveCSV.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(saveCSV));
                writer.write(buffer);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    //读取excel
    public static Workbook readExcel(String filePath) {
        System.out.println("start read...");
        Workbook wb = null;
        if (filePath == null) {
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if (".xls".equals(extString)) {
                return wb = new HSSFWorkbook(is);
            } else if (".xlsx".equals(extString)) {
                return wb = new XSSFWorkbook(is);
            } else {
                return wb = null;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }

    public static Object getCellFormatValue(Cell cell) {
        System.out.println("start get...");
        Object cellValue = null;
        if (cell != null) {
            //判断cell类型
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC: {
                    cellValue = String.valueOf(cell.getNumericCellValue()).replaceAll("\n", " ") + ",";
                    break;
                }
                case Cell.CELL_TYPE_FORMULA: {
                    //判断cell是否为日期格式
                    if (DateUtil.isCellDateFormatted(cell)) {
                        //转换为日期格式YYYY-mm-dd
                        cellValue = String.valueOf(cell.getDateCellValue()).replaceAll("\n", " ") + ",";
                        ;
                    } else {
                        //数字
                        cellValue = String.valueOf(cell.getNumericCellValue()).replaceAll("\n", " ") + ",";
                        ;
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING: {
                    cellValue = cell.getRichStringCellValue().getString().replaceAll(" ","").replaceAll("\n", " ").replaceAll(",","") + ",";
                    ;
                    break;
                }
                default:
                    cellValue = "";
            }
        } else {
            cellValue = "";
        }
        return cellValue;
    }
}
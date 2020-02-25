package Inversion;


public class Conventer {
    private static String sourceFile = "D:/Documents/WeChat Files/haozichen549787212/FileStorage/File/2020-02/zmz-重点文件-标红.xls";
    private static String transFile = "D:/Documents/WeChat Files/haozichen549787212/FileStorage/File/2020-02/zmz/biao.arff";
    private static String csvFile = "D:/Documents/WeChat Files/haozichen549787212/FileStorage/File/2020-02/zmz/biaoCsv.csv";
    private static String classiFile = "D:/Documents/WeChat Files/haozichen549787212/FileStorage/File/2020-02/zmz/biao_classfiy.arff";
    private static String targetFile = "D:/Documents/WeChat Files/haozichen549787212/FileStorage/File/2020-02/zmz/targetFile.xlsx";


    public static void main(String[] args) {
        ExcelToCsv.excelToCsv(sourceFile,csvFile);
        System.out.println("success");
        CsvToArff.arff(csvFile,transFile);
        new ArffToExcel().ArffToExcel(transFile,targetFile);
        System.out.println("success");
    }

}

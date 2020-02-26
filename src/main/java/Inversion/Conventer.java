package Inversion;


public class Conventer {
    private static String sourceFile = "src\\main\\java\\data\\conventer\\zmz-重点文件-标红.xls";
    private static String csvFile = "src\\main\\java\\data\\conventer\\biaoCsv.csv";
    private static String transFile = "src\\main\\java\\data\\conventer\\transFile.arff";
    private static String classiFile = "src\\main\\java\\data\\conventer\\classiFile.arff";
    private static String targetFile = "src\\main\\java\\data\\conventer\\targetFile.xlsx";


    public static void main(String[] args) {
        ExcelToCsv.excelToCsv(sourceFile,csvFile);
        System.out.println("success");
        CsvToArff.arff(csvFile,transFile);
        new ArffToExcel().ArffToExcel(classiFile,targetFile);
        System.out.println("success");
    }

}

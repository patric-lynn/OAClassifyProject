package Inversion;

public class Conventer {
    private static String sourceFile = "data/conventer/sourceFile.xlsx";
    private static String transFile = "data/conventer/transFile.arff";
    private static String classiFile = "data/conventer/classiFile.arff";
    private static String targetFile = "data/conventer/targetFile.xlsx";


    public static void main(String[] args) {
        ExcelToCsv.readExcel(sourceFile);
        System.out.println("success");
        ExcelToCsv.excelToCsv(sourceFile,transFile);
        System.out.println("success");
        CsvToArff.arff(classiFile,targetFile);
        System.out.println("success");
    }

}

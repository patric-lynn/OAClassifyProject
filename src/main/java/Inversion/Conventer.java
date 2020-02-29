package Inversion;

import Algorithm.OAClassifyApplication;
import weka.classifiers.Classifier;
import weka.core.SerializationHelper;


public class Conventer {

    private static String sourceFile = "src\\main\\java\\data\\conventer\\pre.xls";
    private static String csvFile = "src\\main\\java\\data\\conventer\\inter.csv";
    private static String classifyRealFile = "src\\main\\java\\data\\conventer\\classifyFile.arff";
    private static String classifiedRealFile = "src\\main\\java\\data\\conventer\\classifiedFile.arff";
    private static String targetFile = "src\\main\\java\\data\\conventer\\post.xls";

    private static String IBk = "target\\IBk.model";
    private static String Logistic = "target\\Logistic.model";
    private static String RandomForest = "target\\RandomForest.model";
    private static String J48 = "target\\J48.model";
    private static String MultilayerPerceptron = "target\\MultilayerPerceptron.model";


    public static void main(String[] args) throws Exception{
        try {
            ExcelToCsv.excelToCsv(sourceFile, csvFile);
            //已转换
            System.out.println("已转换CSV");

            CsvToArff.arff(csvFile, classifyRealFile);
            //已转换
            System.out.println("已转换arff");

            //进行实际分类
            //Classifier i_classifier = (Classifier) SerializationHelper.read(IBk);
            Classifier l_classifier = (Classifier) SerializationHelper.read(Logistic);
            //Classifier r_classifier = (Classifier) SerializationHelper.read(RandomForest);
            //Classifier j_classifier = (Classifier) SerializationHelper.read(J48);
            //trainModel(instancesTrain, instancesTest, m_classifier, "MultilayerPerceptron");

            OAClassifyApplication.classifyFile(l_classifier, classifyRealFile, classifiedRealFile);

            //转换完成，准备写入Excel
            System.out.println("文件分类完成，准备写入Excel");

            //转换Excel
            new ArffToExcel().ArffToExcel(classifiedRealFile, targetFile);

            //已写入Excel
            System.out.println("已写入Excel");
        }catch(Exception e){
            e.printStackTrace();

        }

    }

}

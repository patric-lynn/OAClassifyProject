package Algorithm;

import java.io.*;
import java.util.Random;

import Inversion.ArffToExcel;
import Inversion.CsvToArff;
import Inversion.ExcelToCsv;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.*;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.supervised.instance.ClassBalancer;
import weka.filters.unsupervised.attribute.*;

public class OAClassifyApplication {
    //测试与训练文件
    private static String trainName = "/data/oa.arff";
//    private static String testName = "/data/oa_test.arff";
//    private static String classifyFile = "src\\main\\java\\data\\oa_classify.arff";
//    private static String classifiedFile = "src\\main\\java\\data\\oa_classified.arff";
    //实际分类文件
//    private static String sourceFile = "src\\main\\java\\data\\conventer\\pre.xls";
//    private static String targetFile = "src\\main\\java\\data\\conventer\\post.xls";
    private static String csvFile = "/data/conventer/inter.csv";
    public static String classifyRealFile = "/data/conventer/classifyFile.arff";
    private static String classifiedRealFile = "/data/conventer/classifiedFile.arff";


    //读取生arff文件,将内容传入实例instances
    public static Instances getRawInstancesByFilename(String filename) throws IOException {
        Instances instances = null;
        try {
            InputStream in =OAClassifyApplication.class.getResourceAsStream(filename);
            ArffLoader arffLoader = new ArffLoader();
            arffLoader.setSource(in);
            instances = arffLoader.getDataSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instances;
    }

    public static Instances getRawInstancesByFilename2(String filename) throws IOException {
        Instances instances = null;
        try {
            File file=new File(filename);
            ArffLoader arffLoader = new ArffLoader();
            arffLoader.setFile(file);
            instances = arffLoader.getDataSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instances;
    }

    //处理instances格式，使得符合classifier的requisition
    public static Instances getOldInstancesByRaw(Instances ins) throws Exception {
        try {
            //将字符串属性变为wordtovector
            StringToWordVector filter = new StringToWordVector();
            filter.setIDFTransform(true);
            filter.setTFTransform(true);
            filter.setInputFormat(ins);
            ins = Filter.useFilter(ins, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ins;
    }


    //训练并保存模型
    public static Classifier trainModel(Instances instancesTrain, Classifier classifier, String modelname) throws Exception {//, Instances instancesTest
        try {
            instancesTrain.setClassIndex(0);
            //instancesTest.setClassIndex(0);

            //平衡类属性度量
            ClassBalancer filter = new ClassBalancer();
            filter.setInputFormat(instancesTrain);
            instancesTrain = Filter.useFilter(instancesTrain, filter);

            //交叉验证训练模型:数据准备
            int seed = 1000;
            int folds = 3;
            Random rand = new Random(seed);
            instancesTrain = new Instances(instancesTrain);
            instancesTrain.randomize(rand);
            if (instancesTrain.classAttribute().isNominal()) {
                instancesTrain.stratify(folds);
            }

            //交叉验证模型
            Evaluation evaluation = new Evaluation(instancesTrain);
            for (int i = 1; i < folds; i++) {
                Instances train = instancesTrain.trainCV(folds, i);
                Instances test = instancesTrain.testCV(folds, i);
                classifier.buildClassifier(train);
                classifier = AbstractClassifier.makeCopy(classifier);
                evaluation.evaluateModel(classifier, test);
            }
//            System.out.println(evaluation.toMatrixString());
//            System.out.println("class important  precision:" + evaluation.precision(0));
//            System.out.println("class important     Recall:" + evaluation.recall(0));
//            System.out.println("class important F1-Measure:" + evaluation.fMeasure(0));


            //保存模型
            //SerializationHelper.write("target/" + modelname + ".model", classifier);
            System.out.println("已生成模型");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return classifier;
    }

    //分类
    public static void classifyFile(Classifier classifier, String classifyFile, String classifiedFile) throws Exception {
        try {
            Instances instancesRaw = getRawInstancesByFilename2(classifyFile);
            Instances instances = getOldInstancesByRaw(instancesRaw);
            instancesRaw.setClassIndex(0);
            instances.setClassIndex(0);
            //System.out.println("第1个实例的转换后表示为 "+instances.instance(0));//测试

            int sum = instances.numInstances();
            System.out.println("待分类实例个数为"+sum);
            for (int i = 0; i < sum; i++) {
                double result = classifier.classifyInstance(instances.instance(i));
                instances.instance(i).setClassValue(result);
                //System.out.println("第"+i+"个实例已分类");//测试
                instancesRaw.instance(i).setClassValue(instances.instance(i).classValue());
                System.out.println("第"+i+"个实例已分类为"+instancesRaw.instance(i).classValue());
            }
            System.out.println(instancesRaw);
            //写回文件
            ArffSaver saver = new ArffSaver();
            saver.setInstances(instancesRaw);
            saver.setFile(new File(classifiedFile));
            saver.writeBatch();
            System.out.println("成功对样本文件分类，准备读取结果");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void exec(Classifier classifier,String preFile,String postFile)throws Exception{//
        try {
            ExcelToCsv.excelToCsv(preFile, csvFile);

            System.out.println("源文件xls格式已转换为CSV格式");

            CsvToArff.arff(csvFile, classifyRealFile);

            System.out.println("已转换为arff格式，准备分类");

            classifyFile(classifier, classifyRealFile, classifiedRealFile);

            System.out.println("文件分类完成，准备注入Excel");

            new ArffToExcel().ArffToExcel(classifiedRealFile, postFile);

            System.out.println("已写入Excel,请查看结果，文档第一列为类标签");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  static void classifyByFile(String preFile, String postFile)throws Exception{
        try {
            //初始化文档路径
            initPath();

            Classifier i_classifier = new IBk(5);

            //获取并处理训练文件：StringtoWordVector
            Instances instancesTrainRaw = getRawInstancesByFilename(trainName);
            Instances instancesTrain = getOldInstancesByRaw(instancesTrainRaw);

            Classifier classifier = trainModel(instancesTrain, i_classifier, "IBK");

            ExcelToCsv.excelToCsv(preFile, csvFile);

            System.out.println("源文件xls格式已转换为CSV格式");

            CsvToArff.arff(csvFile, classifyRealFile);

            System.out.println("已转换为arff格式，准备分类");


            classifyFile(classifier, classifyRealFile, classifiedRealFile);

            System.out.println("文件分类完成，准备注入Excel");

            new ArffToExcel().ArffToExcel(classifiedRealFile, postFile);

            System.out.println("已写入Excel,请查看结果，文档第一列为类标签");
            //删除临时文件
            deleteTempALL();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void deleteTempALL() {
        deleteTemp(csvFile);
        deleteTemp(classifyRealFile);
        deleteTemp(classifiedRealFile);
    }

    private static void deleteTemp(String path) {
        File file=new File(path);
        if(file.exists()){
            file.delete();
        }
    }
    public static void initPath(){
        String path = OAClassifyApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File fie=new File(path);
        String temp=fie.getParent().replaceAll("\\\\","/");
        csvFile=temp+"/inter.csv";
        classifyRealFile=temp+"/classifyFile.arff";
        classifiedRealFile=temp+"/classifiedFile.arff";

    }
    public static void main(String[] args) throws Exception {
        String preFile = args[0];
        String postFile = args[1];

        initPath();

        //定义多个分类模型
        Classifier l_classifier = new Logistic();
        Classifier a_classifier = new AdaBoostM1();
        Classifier r_classifier = new RandomForest();
        Classifier rt_classifier = new RandomTree();
        Classifier i_classifier = new IBk(5);
        Classifier j_classifier = new J48();
        Classifier m_classifier = new MultilayerPerceptron();



        //获取并处理训练文件：StringtoWordVector
        Instances instancesTrainRaw = getRawInstancesByFilename(trainName);
        Instances instancesTrain = getOldInstancesByRaw(instancesTrainRaw);

//        //获取并处理测试文件：StringtoWordVector
//        Instances instancesTestRaw = getRawInstancesByFilename(testName);
//        Instances instancesTest = getOldInstancesByRaw(instancesTestRaw);

        //训练模型并输出模型及效果


//        Classifier classifier = trainModel(instancesTrain, l_classifier, "Logistic");//, instancesTest
//        Classifier classifier = trainModel(instancesTrain, rt_classifier, "RandomTree");
//        Classifier classifier = trainModel(instancesTrain, r_classifier, "RandomForest");
//        Classifier classifier = trainModel(instancesTrain, a_classifier, "Ada");
        Classifier classifier = trainModel(instancesTrain, i_classifier, "IBk");
//        Classifier classifier = trainModel(instancesTrain, m_classifier, "MultilayerPerceptron");

        //模型测试代码
//        classifyFile(classifier, classifyFile, classifiedFile);
//        System.out.println("测试文件已完成分类");

        exec(classifier,preFile,postFile);//
        System.out.println("文件已完成分类");
        deleteTempALL();
    }
}

package Algorithm;

import java.io.*;
import java.util.Random;

import Inversion.ArffToExcel;
import Inversion.CsvToArff;
import Inversion.ExcelToCsv;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.classifiers.functions.*;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.AdditiveRegression;
import weka.classifiers.meta.RandomCommittee;
import weka.classifiers.misc.SerializedClassifier;
import weka.classifiers.pmml.consumer.NeuralNetwork;
import weka.classifiers.pmml.consumer.SupportVectorMachineModel;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.supervised.attribute.ClassOrder;
import weka.filters.supervised.instance.ClassBalancer;
import weka.filters.unsupervised.attribute.*;

public class OAClassifyApplication {

    private static String trainName = "src\\main\\java\\data\\labor.arff";
    private static String testName = "src\\main\\java\\data\\labor_test.arff";
    private static String classifyFile = "src\\main\\java\\data\\labor_classify.arff";
    private static String classifiedFile = "src\\main\\java\\data\\labor_classified.arff";

    private static String sourceFile = "src\\main\\java\\data\\conventer\\pre.xls";
    private static String csvFile = "src\\main\\java\\data\\conventer\\inter.csv";
    private static String classifyRealFile = "src\\main\\java\\data\\conventer\\classifyFile.arff";
    private static String classifiedRealFile = "src\\main\\java\\data\\conventer\\classifiedFile.arff";
    private static String targetFile = "src\\main\\java\\data\\conventer\\post.xls";

    //读取生arff文件,将内容传入实例instances
    public static Instances getRawInstancesByFilename(String filename) throws IOException {
        Instances instances = null;
        try {
            File file = new File(filename);
            ArffLoader arffLoader = new ArffLoader();
            arffLoader.setFile(file);
            instances = arffLoader.getDataSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instances;
    }

    //处理instances格式，使得符合classifier的requisition
    public static Instances getOldInstancesByRaw(Instances ins) throws Exception {  // throws IOException
        try {
//            //将date属性变为numeric
//            DateToNumeric dateToNumeric = new DateToNumeric();
//            dateToNumeric.setInputFormat(ins);
//            ins = Filter.useFilter(ins, dateToNumeric);

//            //属性正则化
//            Standardize normalize = new Standardize();
//            normalize.setInputFormat(ins);
//            ins = Filter.useFilter(ins, normalize);

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
    public static Classifier trainModel(Instances instancesTrain, Instances instancesTest, Classifier classifier, String modelname) throws Exception {
        try {
            instancesTrain.setClassIndex(0);
            //instancesTest.setClassIndex(0);

            //平衡类属性度量
            ClassBalancer filter = new ClassBalancer();
            filter.setInputFormat(instancesTrain);
            instancesTrain = Filter.useFilter(instancesTrain, filter);

            //交叉验证训练模型:数据准备
            int seed = 1000;
            int folds = 2;
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
            System.out.println(evaluation.toMatrixString());
            System.out.println("正例的precision值为:" + evaluation.precision(0));
            System.out.println("正例的Recall值为:   " + evaluation.recall(0));
            System.out.println("正例的F1值为:       " + evaluation.fMeasure(0));


            //保存模型
            SerializationHelper.write("target/" + modelname + ".model", classifier);
            System.out.println("已生成模型");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return classifier;
    }

    //分类
    public static void classifyFile(Classifier classifier, String classifyFile, String classifiedFile) throws Exception {
        try {
            Instances instancesRaw = getRawInstancesByFilename(classifyFile);
            Instances instances = getOldInstancesByRaw(instancesRaw);
            instancesRaw.setClassIndex(0);
            instances.setClassIndex(0);
            System.out.println("第1个实例的转换后表示为 "+instances.instance(0));

            int sum = instances.numInstances();
            System.out.println("待分类实例个数为"+sum);
            for (int i = 0; i < sum; i++) {
//                double[] distribution = classifier.distributionForInstance(instances.instance(i));
//                System.out.println(distribution);
                double result = classifier.classifyInstance(instances.instance(i));
                instances.instance(i).setClassValue(result);
                System.out.println("第"+i+"个实例已分类");
                instancesRaw.instance(i).setClassValue(instances.instance(i).classValue());
                System.out.println("第"+i+"个实例的类别为"+instancesRaw.instance(i).classValue());
            }
            System.out.println(instancesRaw);
            //写回文件
            ArffSaver saver = new ArffSaver();
            saver.setInstances(instancesRaw);
            saver.setFile(new File(classifiedFile));
            saver.writeBatch();
            System.out.println("成功分类，结果已保存于" + classifiedFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void exec(Classifier classifier)throws Exception{
        try {
            ExcelToCsv.excelToCsv(sourceFile, csvFile);
            //已转换
            System.out.println("已转换CSV");

            CsvToArff.arff(csvFile, classifyRealFile);
            //已转换
            System.out.println("已转换arff");

            classifyFile(classifier, classifyRealFile, classifiedRealFile);

            System.out.println("已写入");

            //转换完成，准备写入Excel
            System.out.println("文件分类完成，准备写入Excel");

            //转换Excel
            new ArffToExcel().ArffToExcel(classifiedRealFile, targetFile);

            //已写入Excel
            System.out.println("已写入Excel");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {

        //定义多个分类模型
        Classifier a_classifier = new AdaBoostM1();
        Classifier i_classifier = new IBk(5);
        Classifier r_classifier = new RandomForest();
        Classifier rt_classifier = new RandomTree();


        Classifier l_classifier = new Logistic();
        Classifier j_classifier = new J48();
        Classifier m_classifier = new MultilayerPerceptron();
        Classifier s_classifier = new SMO();



        //获取并处理训练文件：StringtoWordVector
        Instances instancesTrainRaw = getRawInstancesByFilename(trainName);
        Instances instancesTrain = getOldInstancesByRaw(instancesTrainRaw);

        //获取并处理测试文件：StringtoWordVector
        Instances instancesTestRaw = getRawInstancesByFilename(testName);
        Instances instancesTest = getOldInstancesByRaw(instancesTestRaw);

        //训练模型并输出模型及效果


        Classifier classifier = trainModel(instancesTrain, instancesTest, rt_classifier, "RandomTree");
//        Classifier classifier = trainModel(instancesTrain, instancesTest, r_classifier, "RandomForest");
//        Classifier classifier = trainModel(instancesTrain, instancesTest, a_classifier, "Ada");
//        Classifier classifier = trainModel(instancesTrain, instancesTest, i_classifier, "IBk");

        //以下分类器会报错，不建议使用
//        Classifier classifier = trainModel(instancesTrain, instancesTest, l_classifier, "Logistic");
//        Classifier classifier = trainModel(instancesTrain, instancesTest, j_classifier, "J48");
//        Classifier classifier = trainModel(instancesTrain, instancesTest, m_classifier, "MultilayerPerceptron");
//        Classifier classifier = trainModel(instancesTrain, instancesTest, s_classifier, "smo");

//        classifyFile(classifier, classifyFile, classifiedFile);
//        System.out.println("测试文件已完成分类");

        exec(classifier);
        System.out.println("文件已完成分类");
    }
}

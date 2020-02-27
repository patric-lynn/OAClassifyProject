package Algorithm;

import java.io.*;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.supervised.attribute.ClassOrder;
import weka.filters.supervised.instance.ClassBalancer;
import weka.filters.unsupervised.attribute.*;

public class OAClassifyApplication {

    private static String trainName = "src\\main\\java\\data\\oa.arff";
    private static String testName = "src\\main\\java\\data\\labor_test.arff";
    private static String classifyFile = "src\\main\\java\\data\\labor_classify.arff";
    private static String classifiedFile = "src\\main\\java\\data\\labor_classified.arff";

    //读取生arff文件,将内容传入实例instances
    public static Instances getRawInstancesByFilename(String filename) throws IOException {
        Instances instances = null;
        try {
            File file = new File(filename);
            ArffLoader arffLoader = new ArffLoader();
            arffLoader.setFile(file);
            instances = arffLoader.getDataSet();
            instances.setClassIndex(0);
        } catch (Exception e) {
            e.toString();
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
            e.toString();
        }
        return ins;
    }


    //训练并保存模型
    public static void trainModel(Instances instancesTrain, Instances instancesTest, Classifier classifier, String modelname) throws Exception {
        try {

            //平衡类属性度量
            ClassBalancer filter0 = new ClassBalancer();
            filter0.setInputFormat(instancesTrain);
            instancesTrain = Filter.useFilter(instancesTrain, filter0);

            //训练模型
            classifier.buildClassifier(instancesTrain);

            //交叉验证模型
            Evaluation evaluation = new Evaluation(instancesTrain);
            evaluation.crossValidateModel(classifier, instancesTrain, 10, new Random(1234));
            System.out.println(evaluation.toMatrixString());
            System.out.println("正例的Recall值为"+evaluation.recall(0));
            System.out.println("正例的F1值为"+evaluation.fMeasure(0));
            System.out.println(evaluation.toSummaryString());

            //保存模型
            SerializationHelper.write("target/" + modelname + ".model", classifier);
            System.out.println("已生成模型");
//            classifier = (Classifier) SerializationHelper.read("target/" + modelname + ".model");
//
//            //测试：测试集构建
//            double sum = instancesTest.numInstances();//测试语料实例数
//            double right = 0.0f;
//            // 获取上面保存的模型
//            for (int i = 0; i < sum; i++)//测试分类结果  1
//            {
//                if (classifier.classifyInstance(instancesTest.instance(i)) == instancesTest.instance(i).classValue())//如果预测值和答案值相等（测试语料中的分类列提供的须为正确答案，结果才有意义）
//                {
//                    right++;//正确值加1
//                }
//            }
//            System.out.println(right);
//            System.out.println(sum);
//            System.out.println(modelname + "classification precision:" + (right / sum));
        } catch (Exception e) {
            e.toString();
        }
    }

    //分类
    public static void classifyFile(Classifier classifier, String classifyFile, String classifiedFile) throws Exception {
        try {//
        Instances instancesRaw = getRawInstancesByFilename(classifyFile);
        Instances instances = getOldInstancesByRaw(instancesRaw);

        int sum = instances.numInstances();
        for (int i = 0; i < sum; i++) {
            instances.instance(i).setClassValue(classifier.classifyInstance(instances.instance(i)));
            instancesRaw.instance(i).setClassValue(instances.instance(i).classValue());
        }
        System.out.println(instancesRaw);
        //写回文件
        ArffSaver saver = new ArffSaver();
        saver.setInstances(instancesRaw);
        saver.setFile(new File(classifiedFile));
        saver.writeBatch();
        System.out.println("成功分类，结果已保存于" + classifiedFile);
        } catch (Exception e) {
            e.toString();
        }

    }


    public static void main(String[] args) throws Exception {

        //定义多个分类模型
        Classifier m_classifier = new MultilayerPerceptron();
        Classifier r_classifier = new RandomForest();
        Classifier i_classifier = new IBk(1);
        Classifier l_classifier = new Logistic();
        Classifier j_classifier = new J48();


        //获取并处理训练文件：StringtoWordVector
        Instances instancesTrainRaw = getRawInstancesByFilename(trainName);
        Instances instancesTrain = getOldInstancesByRaw(instancesTrainRaw);

        //获取并处理测试文件：StringtoWordVector
        Instances instancesTestRaw = getRawInstancesByFilename(testName);
        Instances instancesTest = getOldInstancesByRaw(instancesTestRaw);

        //训练模型并输出模型及效果


        //trainModel(instancesTrain, instancesTest, i_classifier, "IBk");
        trainModel(instancesTrain, instancesTest, l_classifier, "Logistic");
        //trainModel(instancesTrain, instancesTest, r_classifier, "RandomForest");
        //trainModel(instancesTrain, instancesTest, j_classifier, "J48");
        //trainModel(instancesTrain, instancesTest, m_classifier, "MultilayerPerceptron");

        //进行实际分类
        //classifyFile(l_classifier, classifyFile, classifiedFile);
    }
}

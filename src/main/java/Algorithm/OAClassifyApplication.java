package Algorithm;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.pmml.consumer.NeuralNetwork;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;

public class OAClassifyApplication {

    private static String trainName = "/Users/xiaoxiangyuzhu/Desktop/project/testdata/oa.arff";
    private static String testName = "/Users/xiaoxiangyuzhu/Desktop/project/testdata/oa_test.arff";
    //读取arff文件,传入instances
    public static Instances getRawInstancesByFilename(String filename) throws IOException {
        Instances instances = null;
        try {
            File file = new File(filename);
            ArffLoader arffLoader = new ArffLoader();
            arffLoader.setFile(file);
            instances = arffLoader.getDataSet();
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
        }
        return instances;
    }
    //转换文件中的字符串类型为Nominal类型
    public static Instances getOldInstancesByRaw(Instances ins) throws Exception {  // throws IOException
        Instances instances = null;
        StringToNominal stringToNominal=new StringToNominal();
        try {
            stringToNominal.setInputFormat(ins);
            instances = Filter.useFilter(ins,stringToNominal);

        }catch (Exception e){
            System.err.println(e.getStackTrace());
        }
        return instances;
    }

    public static void main(String[] args) throws Exception {
        Classifier m_classifier = new NaiveBayes();//RandomForest()
//        File inputFile = new File("/Users/xiaoxiangyuzhu/Desktop/project/testdata/labor.arff");//训练语料文件
//        ArffLoader atf = new ArffLoader();
//        atf.setFile(inputFile);
//        Instances instancesTrain = atf.getDataSet(); // 读入训练文件
        Instances instancesTrain = getRawInstancesByFilename(trainName);
        instancesTrain = getOldInstancesByRaw(instancesTrain);
//        inputFile = new File("/Users/xiaoxiangyuzhu/Desktop/project/testdata/labor_test.arff");//测试语料文件
//        atf.setFile(inputFile);
//        Instances instancesTest = atf.getDataSet(); // 读入测试文件
        Instances instancesTest = getRawInstancesByFilename(testName);
        instancesTest = getOldInstancesByRaw(instancesTest);

        //训练模型
        instancesTrain.setClassIndex(0);
        m_classifier.buildClassifier(instancesTrain); //训练
        System.out.println(m_classifier);

        // 保存模型
        SerializationHelper.write("target/classifier.model", m_classifier);//参数一为模型保存文件，classifier4为要保存的模型

        //测试集属性
        instancesTest.setClassIndex(0); //设置分类属性所在行号（第一行为0号），属性总数instancesTrain.numAttributes()-1
        double sum = instancesTest.numInstances();//测试语料实例数
        double right = 0.0f;

        // 获取上面保存的模型
        for (int i = 0; i < sum; i++)//测试分类结果  1
        {
            if (m_classifier.classifyInstance(instancesTest.instance(i)) == instancesTest.instance(i).classValue())//如果预测值和答案值相等（测试语料中的分类列提供的须为正确答案，结果才有意义）
            {
                right++;//正确值加1
            }
        }
        System.out.println(right);
        System.out.println(sum);
        System.out.println("RandomForest classification precision:" + (right / sum));
    }
}

package Algorithm;

import java.io.File;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.pmml.consumer.NeuralNetwork;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;

public class OAClassifyApplication {
    public static void main(String[] args)throws Exception {
        Classifier m_classifier = new NaiveBayes();//RandomForest()
        File inputFile = new File("/Users/xiaoxiangyuzhu/Desktop/project/testdata/labor.arff");//训练语料文件
        ArffLoader atf = new ArffLoader();
        atf.setFile(inputFile);
        Instances instancesTrain = atf.getDataSet(); // 读入训练文件
        inputFile = new File("/Users/xiaoxiangyuzhu/Desktop/project/testdata/labor_test.arff");//测试语料文件
        atf.setFile(inputFile);
        Instances instancesTest = atf.getDataSet(); // 读入测试文件
        instancesTest.setClassIndex(instancesTrain.numAttributes()-1); //设置分类属性所在行号（第一行为0号），属性总数instancesTrain.numAttributes()-1
        double sum = instancesTest.numInstances();//测试语料实例数
        double right = 0.0f;
        instancesTrain.setClassIndex(instancesTrain.numAttributes()-1);
        m_classifier.buildClassifier(instancesTrain); //训练
        System.out.println(m_classifier);

        // 保存模型
        SerializationHelper.write("target/classifier.model", m_classifier);//参数一为模型保存文件，classifier4为要保存的模型
        // 获取上面保存的模型
        for(int  i = 0;i<sum;i++)//测试分类结果  1
        {
            if(m_classifier.classifyInstance(instancesTest.instance(i))==instancesTest.instance(i).classValue())//如果预测值和答案值相等（测试语料中的分类列提供的须为正确答案，结果才有意义）
            {
                right++;//正确值加1
            }
        }
        System.out.println(right);
        System.out.println(sum);
        System.out.println("RandomForest classification precision:"+(right/sum));
    }
}

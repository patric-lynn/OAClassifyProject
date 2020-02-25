package Algorithm;

import java.io.File;
import java.io.IOException;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class OAClassifyApplication {

    private static String trainName = "data/labor.arff";
    private static String testName = "data/labor_test.arff";
    private static String fileName = "data/labor_classify.arff";

    //读取生arff文件,将内容传入实例instances
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

    //转换实例文件中的字符串类型为wordtovector，使得符合classifier的requisition
    public static Instances getOldInstancesByRaw(Instances ins) throws Exception {  // throws IOException
        Instances instances = null;
        StringToWordVector filter = new StringToWordVector();
        try {
            filter.setIDFTransform(true);
            filter.setTFTransform(true);
            filter.setInputFormat(ins);
            instances = Filter.useFilter(ins, filter);
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
        }
        return instances;
    }

    //训练并保存模型
    public static void trainModel(Instances instancesTrain, Instances instancesTest, Classifier classifier, String modelname) throws Exception {
        try {
            //训练：设置类标位置
            instancesTrain.setClassIndex(0);
            instancesTest.setClassIndex(0); //设置分类属性所在行号（第一行为0号），属性总数instancesTrain.numAttributes()-1
            //训练模型
            classifier.buildClassifier(instancesTrain);
            //保存模型
            SerializationHelper.write("target/" + modelname + ".model", classifier);

            //测试：测试集构建
            double sum = instancesTest.numInstances();//测试语料实例数
            double right = 0.0f;
            // 获取上面保存的模型
            for (int i = 0; i < sum; i++)//测试分类结果  1
            {
                if (classifier.classifyInstance(instancesTest.instance(i)) == instancesTest.instance(i).classValue())//如果预测值和答案值相等（测试语料中的分类列提供的须为正确答案，结果才有意义）
                {
                    right++;//正确值加1
                }
            }
            System.out.println(right);
            System.out.println(sum);
            System.out.println(modelname + "classification precision:" + (right / sum));
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
        }
    }

    //分类
    public static void classifyFile(Classifier classifier, String fileName) throws Exception {
        try {
            Instances instancesRaw = getRawInstancesByFilename(fileName);
            Instances instances = getOldInstancesByRaw(instancesRaw);
            instancesRaw.setClassIndex(0);
            instances.setClassIndex(0);
            int sum = instances.numInstances();
            for (int i = 0; i < sum; i++)//测试分类结果
            {
                instances.instance(i).setClassValue(classifier.classifyInstance(instances.instance(i)));
                instancesRaw.instance(i).setClassValue(instances.instance(i).classValue());
            }
            //System.out.println(instances);
            System.out.println(instancesRaw);
            ArffSaver saver = new ArffSaver();
            saver.setInstances(instancesRaw);
            saver.setFile(new File(fileName));
            saver.writeBatch();
            System.out.println("成功分类，结果已保存于"+fileName);
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
        }

    }


    public static void main(String[] args) throws Exception {

        //定义多个分类模型
        Classifier m_classifier = new MultilayerPerceptron();
        Classifier r_classifier = new RandomForest();
        Classifier i_classifier = new IBk(5);

        //获取并处理训练文件：StringtoWordVector
        Instances instancesTrainRaw = getRawInstancesByFilename(trainName);
        Instances instancesTrain = getOldInstancesByRaw(instancesTrainRaw);

        //获取并处理测试文件：StringtoWordVector
        Instances instancesTestRaw = getRawInstancesByFilename(testName);
        Instances instancesTest = getOldInstancesByRaw(instancesTestRaw);

        //训练模型并输出模型及效果
        //trainModel(instancesTrain, instancesTest, m_classifier, "MultilayerPerceptron");
        trainModel(instancesTrain, instancesTest, r_classifier, "RandomForest");
        //trainModel(instancesTrain, instancesTest, i_classifier, "IBk");

        //进行实际分类
        classifyFile(r_classifier, fileName);
    }
}

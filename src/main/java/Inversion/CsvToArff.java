package Inversion;

import java.io.*;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class CsvToArff {

    public static void arff(String sourceFile, String targetFile, int column)
    {
        try
        {
            BufferedReader in;
            BufferedWriter out;
            String temp;
            out = new BufferedWriter(new FileWriter(targetFile, false));
            //关系声明
            out.write("@relation" + "  oa-importance");
            out.newLine();
            //属性声明
            out.write("@attribute" + " class"+" {'general','important'}");
            out.newLine();
            out.write("@attribute" + " from_code"+" string");
            out.newLine();
            out.write("@attribute" + " from_agency"+" string");
            out.newLine();
            out.write("@attribute" + " name"+" string");
            out.newLine();
            out.write("@attribute" + " wenzhong"+" numeric");
            out.newLine();
            out.write("@attribute" + " wenhao"+" numeric");
            out.newLine();
            out.write("@attribute" + " date1"+" date"+" yyyy-MM-dd");
            out.newLine();
            out.write("@attribute" + " date2"+" date"+" yyyy-MM-dd");
            out.newLine();
            out.write("@attribute" + " to_1"+" string");
            out.newLine();
            out.write("@attribute" + " to_2"+" string");
            out.newLine();
            out.write("@attribute" + " date0_day"+" date"+" yyyy-MM-dd");
            out.newLine();
            out.write("@attribute" + " date0_time"+" date"+" HH:mm");
            out.newLine();
            //数据声明
            out.write("@data");
            out.newLine();
            //读CSV文件
            in = new BufferedReader(new FileReader(sourceFile));
            temp = in.readLine();
            while (temp != null)
            {
                out.write(temp);
                out.newLine();
                temp = in.readLine();
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) 	{
            e.printStackTrace();
        }
    }
}

package utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.*;
import org.apache.commons.math3.stat.inference.TestUtils;

import sun.misc.Regexp;

public class Utils 
{
	static public File getFile(String fileName) throws IOException
	{
		File newFile = new File(fileName);
		if (!newFile.exists())
		{
			newFile.createNewFile();
		}
		return newFile;
	}
	
	static public File getDir(String dirName) throws Exception
	{
		File dir = new File(dirName);

		if (!dir.exists())
		{
			dir.mkdir();
		}
		if (!dir.isDirectory()) {
			System.err.println("Directory " + dirName + " doesn't exist");
			throw new Exception("Directory " + dirName + " doesn't exist");
		}
		
		return dir;
	}
	
	public static void addToMap(Map<String,Double> map, String key, int val)
	{
		Double currVal = map.get(key);
		currVal = (null == currVal || 0 == currVal) ? val : val + currVal;
		map.put(key, Double.valueOf(currVal));
	}
	
	public static void _incMap(Map<String,Double> map, String key)
	{
		addToMap(map, key, 1);
	}
	
	public static void incMap(Map<Integer,Integer> map, Integer key)
	{
		addToMap(map, key, 1);
	}
	
	public static void incStringIntegerMap(Map<String, Integer> map, String key)
	{
		addToStringIntegerMap(map, key, 1);
		/*Integer currVal = map.get(key);
		currVal = (null == currVal || 0 == currVal) ? 1 : 1 + currVal;
		map.put(key, currVal);*/
	}
	
	public static void addToMap(Map<Integer,Integer> map, Integer key, int val)
	{
		Integer currVal = map.get(key);
		currVal = (null == currVal || 0 == currVal) ? val : val + currVal;
		map.put(key, currVal);
	}
	
	public static void addToStringIntegerMap(Map<String,Integer> map, String key, int val)
	{
		Integer currVal = map.get(key);
		currVal = (null == currVal || 0 == currVal) ? val : val + currVal;
		map.put(key, currVal);
	}
	
	public static Map<Object,List<Object>> inverseMap(Map srcMap)
	{
		Map<Object,List<Object>> destMap = new HashMap<Object,List<Object>>();
		for (Object entry : srcMap.entrySet())
		{
			List list = destMap.get(((Map.Entry)entry).getValue());
			if (list == null)
			{
				list = new ArrayList<Object>();
				destMap.put(((Map.Entry)entry).getValue(), list);
			}
			list.add(((Map.Entry)entry).getKey());
		}
		return destMap;
	}

	public static File createFile(String path) throws IOException
	{
		File file = new File(path);
		file.createNewFile();
		return file;
	}
	
	public static double log2(double x)
	{
		return Math.log(x)/Math.log(2.0);
	}
	
	public static double getVariance(Object[] data)
	{
		double[] primitiveData = new double[data.length];
		for (int i = 0; i < data.length; i++)
		{
			primitiveData[i] = ((Double)data[i]).doubleValue();
			//System.out.println("val " + i + ": " + primitiveData[i]);
		}
		
		DescriptiveStatistics ds = new DescriptiveStatistics(primitiveData);
		return ds.getPopulationVariance();
	}
	
	public static double getVariance(double[] data)
	{
		DescriptiveStatistics ds = new DescriptiveStatistics(data);
		return ds.getVariance();
	}
	
	public static boolean getTTest(double[] sample1, double[] sample2)
	{
		return TestUtils.tTest(sample1, sample2, 0.05);
	}
	
	public static double getDouble(double num)
	{
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.parseDouble(df.format(num));
	}
	
	public static void toHex(String str) 
	{
		StringBuffer hex = new StringBuffer();
		char[] raw = str.toCharArray();
		for (int i=0;i<raw.length;i++) {
		    if (raw[i] <= 9)
		        hex.append('0');
		    hex.append(Integer.toHexString(raw[i]));
		}
		System.out.println("token = " + str + ", hex = " + hex.toString());
	}
	
}

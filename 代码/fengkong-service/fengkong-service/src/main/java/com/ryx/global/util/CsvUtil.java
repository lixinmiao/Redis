package com.ryx.global.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
/**
 * 
 * @author 王赛超
 * 解析csv文件的工具类
 *
 */
public class CsvUtil {
	
	//文件路径
	private String filePath = null;
	
	//csv文件读取
	private CsvReader csvReader = null;
	
	//文件的内容
	private List<String> list = new ArrayList<String>();
	
	public CsvUtil() {
		
	}
	
	public CsvUtil(String filePath) {
		this.filePath=filePath;	
		// 创建CSV读对象
		try {
			csvReader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
			// 这里需要读表头
            //csvReader.readHeaders();
            while (csvReader.readRecord()){
            	//读取第一行内容(除表头)
            	String rawRecord = csvReader.getRawRecord();
            	list.add(rawRecord);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取表头信息
	 * @return
	 */
	public String readHeaders(){
		if(list.size()==0){
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 根据参数读取某一行,返回String
	 * @param rowNumber 要读的行数,从1开始
	 * @return
	 */
	public String readRowReturnString(Integer rowNumber){
		
		if(list.size()<=rowNumber||rowNumber==0){
			return null;
		}
		return list.get(rowNumber);
	}
	
	/**
	 * 根据参数读取某一行,返回Json
	 * @param rowNumber 要读的行数,从1开始
	 * @return
	 */
	public JSONObject readRowReturnJson(Integer rowNumber){
		if(list.size()<=rowNumber||rowNumber==0){
			return null;
		}
		String[] headers=list.get(0)==null?null:list.get(0).split(",");
		String[] rows=list.get(rowNumber)==null?null:list.get(rowNumber).split(",");
		//如果 列数不相等,返回null
		if(headers.length!=rows.length){
			return null; 
		}
		JSONObject jsonObject=new JSONObject();
		for (int i = 0; i < headers.length; i++) {
			jsonObject.put(headers[i], rows[i]);
		}
		return jsonObject;
	}
	
	/**
	 * 读取某一行的某一个字段
	 * @param rowNumber 行数 从1开始
	 * @param field 字段属性的名称
	 * @return
	 */
	public Object readRowField(Integer rowNumber,String field){
		if(list.size()<=rowNumber||rowNumber==0){
			return null;
		}
		Object value=null;
		try {
			JSONObject jsonObject = readRowReturnJson(rowNumber);
			value=jsonObject.get(field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	/**
	 * 将所有的记录转换为json
	 * @return
	 */
	public JSONArray readRowReturnJson(){
		String[] headers=list.get(0)==null?null:list.get(0).split(",");
		JSONArray jsonArray=new JSONArray();
		for (int i = 1; i < list.size(); i++) {
			String[] rows=list.get(i)==null?null:list.get(i).split(",");
			//如果 列数不相等,返回null
			if(headers.length!=rows.length){
				return null; 
			}
			
			JSONObject jsonObject=new JSONObject();
			for (int j = 0; j < headers.length; j++) {
				jsonObject.put(headers[j], rows[j]);
			}
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}
	
	/**
	 * 获取某一列的所有值
	 * @param columnName 列名
	 * @return
	 */
	public List<Object> readColumn(String columnName){
		
		CsvReader csvReader=null;
		try {
			csvReader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
			csvReader.readHeaders();
			List<Object> list=new ArrayList<>();
			while (csvReader.readRecord()){
				csvReader.getRawRecord();
				list.add(csvReader.get(columnName));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(csvReader!=null){
				csvReader.close();
			}
		}
	}
	
	//====================================华丽的分割线==========================================
	
	/**
	 * 输出csv文件
	 * @param writePath 要输出的路径
	 * @param headers csv表头信息
	 * @param content csv 内容信息
	 */
	public static void write(String writePath,String[] headers,String[] content){
		
		OutputStream outputStream=null;
		OutputStreamWriter ow = null;
		CsvWriter csvWriter = null;
        try {
        	outputStream=new FileOutputStream(writePath);
        	//该处直接对文件写入流进行编码
            ow = new OutputStreamWriter(outputStream, "UTF-8");
            // 创建CSV写对象
            csvWriter = new CsvWriter(ow,',');
            csvWriter.writeRecord(headers);
            csvWriter.writeRecord(content);
            //BOM签名,防止出现乱码
            outputStream.write(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF });
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
        	if(csvWriter!=null){
        		csvWriter.flush();
        		csvWriter.close();
        	}
        	if(ow!=null){
        		try {
					ow.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	if(outputStream!=null){
        		try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
    }
	/**
	 * 输出csv文件
	 * @param writePath 要输出的路径
	 * @param headers csv表头信息
	 * @param content csv 内容信息
	 */
	public static void write(String writePath,String[] headers,String[][] content){
		
		OutputStream outputStream=null;
		OutputStreamWriter ow = null;
		CsvWriter csvWriter = null;
		try {
			outputStream=new FileOutputStream(writePath);
			//该处直接对文件写入流进行编码
			ow = new OutputStreamWriter(outputStream, "UTF-8");
			// 创建CSV写对象
			csvWriter = new CsvWriter(ow,',');
			csvWriter.writeRecord(headers);
			for (int i = 0; i < content.length; i++) {
				csvWriter.writeRecord(content[i]);
			}
			
			//BOM签名,防止出现乱码
			outputStream.write(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF });
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(csvWriter!=null){
				csvWriter.flush();
				csvWriter.close();
			}
			if(ow!=null){
				try {
					ow.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(outputStream!=null){
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 输出csv文件
	 * @param writePath 要输出的路径
	 * @param headers 表头信息
	 * @param content 内容信息
	 */
	public static void write(String writePath,List<String> headers,List<String> content){
		if(headers.size()!=content.size()||headers.size()==0||content.size()==0){
			throw new RuntimeException("表头和内容长度要相同,并且都不能为0");
		}
		
		int headerSize = headers.size();
		String[] headers2 = (String[])headers.toArray(new String[headerSize]);
		
		int contentSize = content.size();
		String[] content2 = (String[])content.toArray(new String[contentSize]);
		
		write(writePath, headers2, content2);
	}
	
	/**
	 * 输出csv文件
	 * @param writePath 输出的路径
	 * @param jsonObject jsonobject格式固定如{"姓名":"王赛超","年龄":"24","地址":"河北邯郸"}
	 */
	public static void write(String writePath,JSONObject jsonObject){
		Set<Entry<String,Object>> entrySet = jsonObject.entrySet();
		if(!entrySet.isEmpty()){
			String[] headers=new String[entrySet.size()];
			String[] contents=new String[entrySet.size()];
			Iterator<Entry<String, Object>> iterator = entrySet.iterator();
			for (int i = 0; i < entrySet.size(); i++) {
				Entry<String, Object> entry = iterator.next();
				headers[i]=entry.getKey();
				contents[i]=(String) entry.getValue();
			}
			write(writePath, headers, contents);
		}else{
			throw new RuntimeException("jsonObject不能为空");
		}
	}
	
	/**
	 * 输出csv文件
	 * @param writePath 输出的路径
	 * @param jsonArray jsonArray格式固定如
	 * 	[
	 * 		{"姓名":"李四","地址":"河北邯郸","年龄":"24"},
	 * 		{"姓名":"李四","地址":"河北邯郸","年龄":"24"},
	 * 		{"姓名":"李四","地址":"河北邯郸","年龄":"24"}
	 * 	]
	 */
	public static void write(String writePath,JSONArray jsonArray){
		
		if(jsonArray.isEmpty()){
			throw new RuntimeException("json数组不能为空");
		}
		
		Iterator<Object> iterator2 = jsonArray.iterator();
		String[] headers=null;
		String[][] contents=null;
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = (JSONObject) iterator2.next();
			Set<Entry<String,Object>> entrySet = jsonObject.entrySet();
			if(!entrySet.isEmpty()){
				
				if(i==0){
					headers=new String[entrySet.size()];
					contents=new String[jsonArray.size()][entrySet.size()];
				}
				
				Iterator<Entry<String, Object>> iterator = entrySet.iterator();
				for (int j = 0; j < entrySet.size(); j++) {
					Entry<String, Object> entry = iterator.next();
					headers[j]=entry.getKey();
					contents[i][j]=(String) entry.getValue();
				}
			}else{
				throw new RuntimeException("jsonObject不能为空");
			}
		}
		
		write(writePath, headers, contents);

	}
	
	/**
	 * 向csv中添加内容
	 * @param writePath 要修改的文件路径
	 * @param data 要添加的数据
	 * @return
	 */
	public static boolean appendDate(String writePath, List<List<String>> data){         
        try {  
            File csvFile=new File(writePath);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile, true), "UTF-8"), 1024);  
            for(int i=0; i<data.size(); i++){  
                List<String> tempData = data.get(i);  
                StringBuffer sb = new StringBuffer();  
                for(int j=0; j<tempData.size(); j++){                      
                    if(j<tempData.size()-1)  
                        sb.append(tempData.get(j)+",");  
                    else  
                        sb.append(tempData.get(j)+"\r\n");                    
                }                 
                bw.write(sb.toString());  
                if(i%1000==0)  
                    bw.flush();  
            }             
            bw.flush();  
            bw.close();  
              
            return true;              
        } catch (Exception e) {  
            e.printStackTrace();  
        }                 
        return false;  
    }  

	public static void main(String[] args) throws IOException {
		
		String filePath = "D:\\Test\\aaa\\8000017062810253500185.csv";
		
		List<List<String>> list=new ArrayList<>();
		List<String> list2=new ArrayList<>();
		list2.add("王五");
		list2.add("20");
		list2.add("河北邢台");
		list.add(list2);
		CsvUtil.appendDate(filePath, list);
	}
	
} 
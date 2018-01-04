package Global;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Algorithms.*;
import GUI.*;
import Predicate.Filter;



public class MainRun {
	
	private String folderPath;
	private String filePath;
	private String binPath;
	private String meregedPath;
	
	private Thread thread;
	private Thread thread_files;
	private static final int NTHREDS = 10;
	
	public static String[] PredicateType = new String[5];
	public static String[] MinVal= new String[5];
	public static String[] MaxVal= new String[5];
	public static String LogicalOperator;
	
	public void saveFolderPath(String path){
		this.folderPath = path;
		System.out.println(folderPath);
		thread = new Thread(()->{
			FileChecker fileChecker = new FileChecker(path);
			for(;;){
				try{
					if (Thread.interrupted()) 
						throw new InterruptedException();
				} catch (InterruptedException e) {
					return;
				}
				System.out.println("thread works!");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				/**HERE
				 * make a list of FileInfo -> run on a folder -> find file -> 
				 * -> check if this file is in the list -> check for size and time modified-> if modified do{}
				 * and update list parameters 
				 * -> if not in list -> save to list
				 * 
				 * now we run on list in loop -> check if file in the list is not in the folder -> delete
				 */
				fileChecker.workerProsses();
			}
		});
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
		//if(!Thread.interrupted())
			executor.execute(thread);

	}
	
	public void saveFilePath(String path){
		this.filePath = path;
		System.out.println(filePath);
		thread_files = new Thread(()->{
			FileChecker fileChecker = new FileChecker(path,"Y");
			for(;;){
				try{
					if (Thread.interrupted()) 
						throw new InterruptedException();
				} catch (InterruptedException e) {
					return;
				}
				System.out.println("thread files works!");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				/**HERE
				 * make a list of FileInfo -> run on a folder -> find file -> 
				 * -> check if this file is in the list -> check for size and time modified-> if modified do{}
				 * and update list parameters 
				 * -> if not in list -> save to list
				 * 
				 * now we run on list in loop -> check if file in the list is not in the folder -> delete
				 */
				fileChecker.workerFileProsses();
			}
		});
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
		//if(!Thread.interrupted())
			executor.execute(thread_files);

		
	}
	
	public String getFilePath(){
		return this.filePath;
		
	}
	
	public void saveMeregedPath(String path){
		this.meregedPath = path;
		System.out.println(meregedPath);
	}
	
	public void saveBinPath(String path){
		this.binPath = path;
		System.out.println(binPath);
	}

	public String getFolderPath()
	{
		return this.folderPath;
	}
	
	public Thread getThread()
	{
		return this.thread;
	}
	
	public Thread getThreadFiles()
	{
		return this.thread_files;
	}
	
	
	public void saveToCSV()
	{
		String input_path = this.folderPath;
		String output_path = "C:/ex0/OUT/";
		toCSVtoKML toCSVtoKML = new toCSVtoKML();
		List<String> fileNameArray = new ArrayList<String>();
		Path folder = Paths.get(input_path);
		try {
			Files.newDirectoryStream(folder,"*.csv").forEach(s -> fileNameArray.add(s.toString()));	
		}
		catch(IOException e) {
			e.printStackTrace();	
		}

		for (int i = 0; i < fileNameArray.size(); i++) {
			toCSVtoKML.readCSV(fileNameArray.get(i));
		}
		toCSVtoKML.mergeData(output_path +"RESULT");
	}
	
	public void saveToKML()
	{
		String input_path = this.folderPath;
		String output_path = "C:/ex0/OUT/";
		toCSVtoKML toCSVtoKML = new toCSVtoKML();
		List<String> fileNameArray = new ArrayList<String>();
		Path folder = Paths.get(input_path);
		try {
			Files.newDirectoryStream(folder,"*.csv").forEach(s -> fileNameArray.add(s.toString()));	
		}
		catch(IOException e) {
			e.printStackTrace();	
		}

		for (int i = 0; i < fileNameArray.size(); i++) {
			toCSVtoKML.readCSV(fileNameArray.get(i));
		}

		toCSVtoKML.createPlacemarkListFromCsvFile();
		StringWriter XML_data = toCSVtoKML.ExportPlacemarkListToXML();
		toCSVtoKML.saveToKMLFile(XML_data,output_path + "RESULT.kml");
	}
	
	public void clearData() {
		String output_path = "C:/ex0/OUT/";
		toCSVtoKML toCSVtoKML = new toCSVtoKML();
		List<CSV_Merged_Row> mergedRowList = new ArrayList<CSV_Merged_Row>();
		toCSVtoKML.CreateCSV_Merged_File(output_path +"RESULT_Merged.csv", mergedRowList);

		StringWriter XML_data = toCSVtoKML.ExportPlacemarkListToXML();
		XML_data.getBuffer().setLength(0);
		toCSVtoKML.saveToKMLFile(XML_data,output_path + "RESULT.kml");
	}

//	public void addCSV()
//	{
//		
//	}
	
	public void submitFilter(){
		
	}
	
	
	public void uploadBinFile(){
		try {
			String input_path = this.binPath;
			@SuppressWarnings("resource")
			ObjectInputStream readBin = new ObjectInputStream(new FileInputStream(input_path));
			Filter filter = (Filter) readBin.readObject();
			readBin.close();
			System.out.println(filter.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void algorhthm1() {//b4:ee:b4:36:d2:b0 Full_Coordinate
		String output_path = this.meregedPath;
		String line=null;
		
		//toCSVtoKML toCSVtoKML = new toCSVtoKML();
		try {
			FileReader fr = new FileReader(output_path);
			BufferedReader bf = new BufferedReader(fr);
			List<CSV_Merged_Row> rowMergeList = new ArrayList<CSV_Merged_Row>();
			line = bf.readLine();
			String sufix ="";

			while(line != null) {
				//separate all values between ","
				String [] strs = line.split(",");
				if(strs.length < 45){
				String prefix = strs[0]+","+strs[1]+","+strs[2]+","+strs[3]+","+strs[4];
				//
				for(int i=5; i<strs.length; i++) {
					if(strs[i].equals("")) strs[i] = "NaN"; //Defect
					if(i<strs.length-1)
						sufix = sufix + strs[i]+",";
					else
						sufix = sufix + strs[i];
				}
				for(int i=strs.length; i<=40; i++) //Defect
					sufix = sufix + ",";
							
				CSV_Merged_Row row= new CSV_Merged_Row( prefix, sufix );
				rowMergeList.add(row);
				//
//					String prefix = strs[0]+","+strs[1]+","+strs[2]+","+strs[3]+",";
//					String sufix = strs[4]+","+strs[5]+","+strs[6]+","+strs[7]+","+strs[8]+","+strs[9]+","+strs[10]+","+strs[11]+","+strs[12]+","+strs[13]+","+strs[14]+","+strs[15]+","+strs[16]+","+strs[17]+","+strs[18]+","+strs[19]+","+strs[20]+","+strs[21]+","+strs[22]+","+strs[23]+","+strs[24]+","+strs[25]+","+strs[26]+","+strs[27]+","+strs[28]+","+strs[29]+","+strs[30]+","+strs[31]+","+strs[32]+","+strs[33]+","+strs[34]+","+strs[35]+","+strs[36]+","+strs[37]+","+strs[38]+","+strs[39]+","+strs[40]+","+strs[41]+","+strs[42]+","+strs[43]+","+strs[44];
//					CSV_Merged_Row row= new CSV_Merged_Row( prefix, sufix );
//					List<CSV_Merged_Row> rowMergeList = toCSVtoKML.getRowMergeList();
//					rowMergeList.add(row);
				}
				else{
					//Do nothing: row is not valid (there are not enough columns in file )
				}
				sufix="";
				line = bf.readLine();
			}
			fr.close();
			bf.close();
//			Full_Coordinate fc = null;
			System.out.println(rowMergeList.toString());
			Algo_1.runAlgo1(output_path, rowMergeList);
//			return fc;
		}
		catch (IOException e){
			//e.printStackTrace();
			System.out.println("File is opened!!!!");
//			return null;
		}
	//	toCSVtoKML.run();
//		Full_Coordinate fc = Algo_1.algorithm_1(mac, rowMergeList);
//		return fc;
	}
	
	public Full_Coordinate algorhthm2(String mac1, int signal1, String mac2, int signal2, String mac3, int signal3) {
		String output_path = this.meregedPath;
		String line=null;
		
		//toCSVtoKML toCSVtoKML = new toCSVtoKML();
		try {
			FileReader fr = new FileReader(output_path);
			BufferedReader bf = new BufferedReader(fr);
			List<CSV_Merged_Row> rowMergeList = new ArrayList<CSV_Merged_Row>();
			line = bf.readLine();
			String sufix ="";
			while(line != null) {
				//separate all values between ","
				String [] strs = line.split(",");
				if(strs.length < 45){
				String prefix = strs[0]+","+strs[1]+","+strs[2]+","+strs[3]+","+strs[4];
				//
				for(int i=5; i<strs.length; i++) {
					if(strs[i].equals("")) strs[i] = "NaN"; //Defect
					if(i<strs.length-1)
						sufix = sufix + strs[i]+",";
					else
						sufix = sufix + strs[i];
				}
				for(int i=strs.length; i<=40; i++) //Defect
					sufix = sufix + ",";
				CSV_Merged_Row row= new CSV_Merged_Row( prefix, sufix );
				rowMergeList.add(row);
				//
//					String prefix = strs[0]+","+strs[1]+","+strs[2]+","+strs[3]+",";
//					String sufix = strs[4]+","+strs[5]+","+strs[6]+","+strs[7]+","+strs[8]+","+strs[9]+","+strs[10]+","+strs[11]+","+strs[12]+","+strs[13]+","+strs[14]+","+strs[15]+","+strs[16]+","+strs[17]+","+strs[18]+","+strs[19]+","+strs[20]+","+strs[21]+","+strs[22]+","+strs[23]+","+strs[24]+","+strs[25]+","+strs[26]+","+strs[27]+","+strs[28]+","+strs[29]+","+strs[30]+","+strs[31]+","+strs[32]+","+strs[33]+","+strs[34]+","+strs[35]+","+strs[36]+","+strs[37]+","+strs[38]+","+strs[39]+","+strs[40]+","+strs[41]+","+strs[42]+","+strs[43]+","+strs[44];
//					CSV_Merged_Row row= new CSV_Merged_Row( prefix, sufix );
//					List<CSV_Merged_Row> rowMergeList = toCSVtoKML.getRowMergeList();
//					rowMergeList.add(row);
				}
				else{
					//Do nothing: row is not valid (there are not enough columns in file )
				}
				sufix="";
				line = bf.readLine();
			}
			fr.close();
			bf.close();
//			Full_Coordinate fc = null;
			return Algo_2.algorithm_2(mac1,signal1,mac2,signal2,mac3,signal3,rowMergeList);
			
					//Algo_1.runAlgo1(output_path, rowMergeList);
//			return fc;
		}
		catch (IOException e){
			e.printStackTrace();
			return null;
		}
		//toCSVtoKML.run();
		//Full_Coordinate fc = Algo_2.algorithm_2(mac1,signal1,mac2,signal2,mac3,signal3);
		
	}
	
	public boolean isMerged()
	{
		try {
			FileReader fr = new FileReader(this.filePath);
			BufferedReader bf = new BufferedReader(fr);

			String line = bf.readLine();
			
			fr.close();
			bf.close();
			
			if(line.length() <= 324 && line.length() >= 322)
				return true;
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return false;
	}
	
	
	public static void run(){
		Frame1 Frame1 = new Frame1();
		Frame1.GUIrun();
	}
	
	
	public static void main(String[] args) {
		run();
	}
}



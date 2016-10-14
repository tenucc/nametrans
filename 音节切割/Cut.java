package translation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class Cut {
	
    public final static int YUAN = 1; // Ԫ��
    public final static int FU = 2;   // ����
    public final static int BI = 3;   // ����
    public final static String[] doubleYUAN = {"eo", "ia", "io", "iu", "oi", "ua", "ui", "uo"};
    public final static String[] doubleFU ={"ch", "sh", "zh", "th", "ph", "wh", "gh", "ng"};
    
    private int getCharType(char temp){	
    	if(temp == 'a' || temp == 'e' || temp == 'i' || temp == 'o' || temp == 'u')
    		return YUAN;
    	else if(temp == 'm' || temp == 'n')
    		return BI;
    	else
    		return FU;
    }
    
    private int getStrType(String str){
    	
    	return getCharType(str.charAt(0));
    }
    
    // �ж�˫Ԫ������
    private boolean isInDoubleYUAN(String str){
    	boolean flag = false;
    	
    	for(int i = 0; i < doubleYUAN.length; i ++){
    		if(doubleYUAN[i].equals(str)){
    			flag = true;
    			break;
    		}
    	}
    	
    	return flag;
    }  
    
    // �ж�˫��������
    private boolean isInDoubleFU(String str){
    	boolean flag = false;
    	
    	for(int i = 0; i < doubleFU.length; i ++){
    		if(doubleFU[i].equals(str)){
    			flag = true;
    			break;
    		}
    	}
    	
    	return flag;
    }

 
    // Ԥ�����ַ��� ��������Ԫ�� �����ϲ�
    public ArrayList<String> pretreatment(String str){
    	str = str.toLowerCase();
    	ArrayList<String> list = new ArrayList<String>();
    	 	
    	for(int i = 0; i < str.length(); i ++){
    		
    		char first = str.charAt(i);
    		
    		if(i == str.length() - 1){ // �ַ���ĩβ ֱ�Ӽ����б�
    			list.add(first+"");
    			break;
    		}
    		
    		char second = str.charAt(i + 1);
    		
    		int firstFlag = getCharType(first);
    		int secondFlag = getCharType(second);
    		
    		// ��ĸ���Ͳ�ͬ ����Ԫ+Ԫ ��+�� ��+��
    		if(firstFlag != secondFlag){
    			list.add(first+"");
    		}else if(firstFlag == YUAN){
    			
    			// Ԫ��+Ԫ��  ��Ҫ�иeo ia io iu oi ua ui uo
    			//          ����ϲ� (������һ�����ǲ��ǿ��Ժϲ���Ԫ��)
    			String unit = first + "" + second;
    			
    			if(isInDoubleYUAN(unit)){ // ��Ҫ�и�
    				list.add(first+"");
    			}else{ // Ԫ����Ҫ�ϲ�
    				
    				// Ԫ+Ԫ+Ԫ 
    				if(i < (str.length()-2) && getCharType(str.charAt(i+2)) == YUAN){ 
    					// (Ԫ+Ԫ) Ԫ
    					if(isInDoubleYUAN(second + "" + str.charAt(i+2))){ 
    						list.add(unit);
    						i ++;
    					}else{ // (Ԫ+Ԫ+Ԫ)
    						list.add(unit+str.charAt(i+2));
    						i += 2;
    					}
    				}else{ // Ԫ+Ԫ+��/��/���ַ�
    					list.add(unit);
    					i ++;    					
    				}
    			}    			
    		}else if(firstFlag == FU){
    			
    			// �� + �� ��Ҫ�ϲ����ظ��ĸ��� / ch sh zh th ph wh gh ng
    			//       ������Ҫ�п�
    			String unit = first + "" + second;
    			
    			// �ظ������ϲ� (l+l)  
    			if(first == second || isInDoubleFU(unit)){ 
    				list.add(unit);
    				i ++;
    			}else{
    				list.add(first+"");
    			} 			
    			
    		}else if(firstFlag == BI){
    			
    			// ����+����   (�ϲ�)   (����+����)
    			String unit = first + "" + second;
    			list.add(unit);
    			i ++;
    		}	   		
    	}    	
    	return list;
    }
    
    // ��Ļ���
    public String getPrintList(ArrayList<String> list){
    	String str = "";
    	Iterator<String> iter = list.iterator();
    	while(iter.hasNext())
    		str += iter.next() + " ";
    	return str;
    }
     
    // �и������ַ���
    public ArrayList<String> cutName(ArrayList<String> list){
    	ArrayList<String> ans = new ArrayList<String>();
    	
    	for(int i = 0; i < list.size(); i ++){
    		
    		String first = list.get(i);
    		int firstFlag = getStrType(first);
    		
    		if(i == list.size() - 1){ // ���һ��Ԫ�� ����
    			ans.add(first);
    			break;
    		}
    		
    		String second = list.get(i+1);
    		int secondFlag = getStrType(second);
    		
    		switch (firstFlag){
    		case BI:{
    			// (�� + Ԫ) cut
    			// �� + ��  cut
    			if(secondFlag == YUAN){
    				ans.add(first + second);
    				i ++;
    			}else{
    				ans.add(first);
    			}
    			break;
    		}
    		case YUAN:{
    			// Ԫ + Ԫ cut
    			if(secondFlag == YUAN){
    				ans.add(first);
    			}
    			// Ԫ+ ��/r
    			else if(secondFlag == BI || second.equals("r")){
    				// Ԫ + (��/r + Ԫ)
    				// (Ԫ+��) + ��/���ַ�
    				if(i == list.size() - 2){
    					ans.add(first);
    					ans.add(second);
    					i ++;
    				}else if(getStrType(list.get(i+2)) == YUAN){
    					ans.add(first);
    					ans.add(second + list.get(i+2));
    					i ++;    					
    				}else{
    					ans.add(first + second);
    				}
    			}
    			// Ԫ + ��
    			else{
    				// (Ԫ + gh)
    				// (Ԫ + h)
    				// (Ԫ + ng)
    				if(second.equals("gh") || second.equals("h") || second.equals("ng")){
    					ans.add(first + second);
    					i ++;
    				}else{
    					ans.add(first);
    				}
    			}
    			break;
    		}
    		case FU:{
    			// (�� + Ԫ)
    			if(secondFlag == YUAN){
    				ans.add(first + second);
    				i ++;
    			}
    			// �� + y
    			else if(second.equals("y")){
    				// (�� + y) + ���ַ�
    				// (�� + y + Ԫ/y)
    				// (�� + y) + ��
    				if(i == list.size() - 2){
    					ans.add(first + second);
    					i ++;
    				}else if(getStrType(list.get(i+2)) == YUAN || list.get(i+2).equals("y")){
    					ans.add(first + second + list.get(i+2));
    					i += 2;
    				}else{
    					ans.add(first + second);
    					i ++;
    				}
    			}
    			// �� + ��/�� cut
    			else{
    				ans.add(first);
    			}
    			break;
    		}
    		}    			
    	}
    	
    	return ans;
    }
    
    public void readFromFile(String readFileName, String writeFileName){
    	try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(readFileName), "UTF-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(writeFileName)), "UTF-8"));
			
			String str = "";
			while((str = reader.readLine()) != null){
				ArrayList<String> middle = pretreatment(str);
				//System.out.println(getPrintList(middle));
		    	ArrayList<String> finalList = cutName(middle);
		    	String s = getPrintList(finalList);
		    	System.out.println(s);
		    	writer.write(s + "\n");
			}
			
			reader.close();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
    public static void main(String[] args){
    	
    	Cut cut = new Cut();
    	cut.readFromFile("F:\\learning\\sophomore\\��\\NLP\\Code\\eclipse\\translation\\src\\translation\\��Name List.txt", "F:\\learning\\sophomore\\��\\NLP\\Code\\eclipse\\translation\\src\\translation\\��cut.txt");
//    	ArrayList<String> middle = cut.pretreatment("Seriu");
//    	System.out.println(cut.getPrintList(middle));
//    	ArrayList<String> finalList = cut.cutName(middle);
//    	System.out.println(cut.getPrintList(finalList));
    }
    
    

}

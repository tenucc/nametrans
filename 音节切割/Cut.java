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
	
    public final static int YUAN = 1; // 元音
    public final static int FU = 2;   // 辅音
    public final static int BI = 3;   // 鼻音
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
    
    // 判断双元音类型
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
    
    // 判断双辅音类型
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

 
    // 预处理字符串 将连续的元音 辅音合并
    public ArrayList<String> pretreatment(String str){
    	str = str.toLowerCase();
    	ArrayList<String> list = new ArrayList<String>();
    	 	
    	for(int i = 0; i < str.length(); i ++){
    		
    		char first = str.charAt(i);
    		
    		if(i == str.length() - 1){ // 字符串末尾 直接加入列表
    			list.add(first+"");
    			break;
    		}
    		
    		char second = str.charAt(i + 1);
    		
    		int firstFlag = getCharType(first);
    		int secondFlag = getCharType(second);
    		
    		// 字母类型不同 不是元+元 辅+辅 鼻+鼻
    		if(firstFlag != secondFlag){
    			list.add(first+"");
    		}else if(firstFlag == YUAN){
    			
    			// 元音+元音  需要切割：eo ia io iu oi ua ui uo
    			//          其余合并 (看看下一个还是不是可以合并的元音)
    			String unit = first + "" + second;
    			
    			if(isInDoubleYUAN(unit)){ // 需要切割
    				list.add(first+"");
    			}else{ // 元音需要合并
    				
    				// 元+元+元 
    				if(i < (str.length()-2) && getCharType(str.charAt(i+2)) == YUAN){ 
    					// (元+元) 元
    					if(isInDoubleYUAN(second + "" + str.charAt(i+2))){ 
    						list.add(unit);
    						i ++;
    					}else{ // (元+元+元)
    						list.add(unit+str.charAt(i+2));
    						i += 2;
    					}
    				}else{ // 元+元+辅/鼻/无字符
    					list.add(unit);
    					i ++;    					
    				}
    			}    			
    		}else if(firstFlag == FU){
    			
    			// 辅 + 辅 需要合并：重复的辅音 / ch sh zh th ph wh gh ng
    			//       其他都要切开
    			String unit = first + "" + second;
    			
    			// 重复辅音合并 (l+l)  
    			if(first == second || isInDoubleFU(unit)){ 
    				list.add(unit);
    				i ++;
    			}else{
    				list.add(first+"");
    			} 			
    			
    		}else if(firstFlag == BI){
    			
    			// 鼻音+鼻音   (合并)   (鼻音+鼻音)
    			String unit = first + "" + second;
    			list.add(unit);
    			i ++;
    		}	   		
    	}    	
    	return list;
    }
    
    // 屏幕输出
    public String getPrintList(ArrayList<String> list){
    	String str = "";
    	Iterator<String> iter = list.iterator();
    	while(iter.hasNext())
    		str += iter.next() + " ";
    	return str;
    }
     
    // 切割姓名字符串
    public ArrayList<String> cutName(ArrayList<String> list){
    	ArrayList<String> ans = new ArrayList<String>();
    	
    	for(int i = 0; i < list.size(); i ++){
    		
    		String first = list.get(i);
    		int firstFlag = getStrType(first);
    		
    		if(i == list.size() - 1){ // 最后一个元素 独立
    			ans.add(first);
    			break;
    		}
    		
    		String second = list.get(i+1);
    		int secondFlag = getStrType(second);
    		
    		switch (firstFlag){
    		case BI:{
    			// (鼻 + 元) cut
    			// 鼻 + 辅  cut
    			if(secondFlag == YUAN){
    				ans.add(first + second);
    				i ++;
    			}else{
    				ans.add(first);
    			}
    			break;
    		}
    		case YUAN:{
    			// 元 + 元 cut
    			if(secondFlag == YUAN){
    				ans.add(first);
    			}
    			// 元+ 鼻/r
    			else if(secondFlag == BI || second.equals("r")){
    				// 元 + (鼻/r + 元)
    				// (元+鼻) + 辅/无字符
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
    			// 元 + 辅
    			else{
    				// (元 + gh)
    				// (元 + h)
    				// (元 + ng)
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
    			// (辅 + 元)
    			if(secondFlag == YUAN){
    				ans.add(first + second);
    				i ++;
    			}
    			// 辅 + y
    			else if(second.equals("y")){
    				// (辅 + y) + 无字符
    				// (辅 + y + 元/y)
    				// (辅 + y) + 辅
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
    			// 辅 + 辅/鼻 cut
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
    	cut.readFromFile("F:\\learning\\sophomore\\大创\\NLP\\Code\\eclipse\\translation\\src\\translation\\日Name List.txt", "F:\\learning\\sophomore\\大创\\NLP\\Code\\eclipse\\translation\\src\\translation\\日cut.txt");
//    	ArrayList<String> middle = cut.pretreatment("Seriu");
//    	System.out.println(cut.getPrintList(middle));
//    	ArrayList<String> finalList = cut.cutName(middle);
//    	System.out.println(cut.getPrintList(finalList));
    }
    
    

}

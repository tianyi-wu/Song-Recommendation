package makehash;


/*example.wordmap.containsKey("単語") 単語があるかどうかを調べる
	example.wordmap.get("単語")　単語のベクトルをゲットする（ベクトルを返す）
	example.calkey("単語","単語")単語の相関係数（－１～１）を返す。単語が存在しない場合は-2を返す、
	example.keylist() :歌の名前のリスト
	example.keylist().size
	たとえばある次元の数を知りたいならば、keylist()の0番目からexample.keylist().size-1番目の要素でhashmapで検索、その次元を出力すればよい

*/


import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;


public class Makehash {
	public int dim;
	public String[] worddim; 
	public HashMap<String,int[]> wordmap = new HashMap<String,int[]>();
	public int keydim;
	public  Makehash() {
	  }
	
	public void readFile(){
		try{
		      File file = new File("matrix.csv");
		      if (checkBeforeReadfile(file)){
		        BufferedReader br = new BufferedReader(new FileReader(file));
		        String str;
		        
		        str = br.readLine();
		        dim=findLength(str);
		        worddim = makeDim(str,dim);	        
		        
		        
		        
		        while((str = br.readLine()) != null){
		        	wordmap.put(makeKey(str),makeVector(str,dim));
		        	}

		        keydim = keyList().size();
		        br.close();
		      }else{
		        System.out.println("ファイルが見つからないか開けません");
		      }
		    }
		catch(FileNotFoundException e){
		      System.out.println(e);
		    }
		catch(IOException e){
		      System.out.println(e);
		    }
	}



	private static boolean checkBeforeReadfile(File file){
	    if (file.exists()){
	      if (file.isFile() && file.canRead()){
	        return true;
	      }
	    }

	    return false;
	  }
	
	private int findLength(String str){
		int count = 0;
		int i=2;
		while (i < str.length()) {
			if (str.charAt(i) == ','){
				count += 1;
			}
			i++;
		}
		return count;
	}
	
	
	private String[] makeDim(String str,int n){
		String word[] = new String[n+1];
		int count = 0;
		int i=2,j=0;
		
		while (i < str.length()) {
			if (str.charAt(i) == '"'){
				j=i+1;
				while(str.charAt(j) != '"'){
					j++;
				}
				word[count] = str.substring(i+1,j).toString();
				count++;
				i=j+1;
			}
			else{
				i++;
			}
		}
		return word;
	
	}
	
	
	
	
	private String makeKey(String str){
		
		int i=1;
		while(str.charAt(i) != '"'){
			i++;
		}
		return str.substring(1,i).toString();
	}

	private int[] makeVector(String str,int n){
		int songvector[] = new int[n];
		int i=1,j,count=0,strlen;
		strlen = str.length();
		while(count < n-1 && i < strlen-1){
			if (str.charAt(i) == ','){
				j=i+1;
				while(j < strlen-1 && str.charAt(j) != ','){
					j++;
				}
				if (j < strlen-1){
				songvector[count] = Integer.valueOf(str.substring(i+1,j));
				// System.out.println(songvector[count]+"-");
				count++;
				i=j;
				}
			}
			else{
				i++;
			}
		}
		
		songvector[n-1] = Integer.valueOf(str.substring(i+1,strlen));
		// System.out.println(songvector[n-1]+"-");
		return songvector;
	}

	
	public void showVector(int[] vector){
		int i=0;
		while(i < vector.length){
			System.out.println("num is"+vector[i]);
			i++;
		}
	}

	
	
	
	
	
	private double calVector(int[] vec1,int[] vec2,int dim){
		int res1=0,res2=0;
		double res=0;
		
		if (vec1.length != dim || vec1.length != dim){
			System.out.println("the vetor's dim is not equal to DIM");
			return (double) -2;
		}
		else{
			for (int i = 0; i < dim; i++) {
				res = res + vec1[i]*vec2[i];
				res1 = res1 + vec1[i]*vec1[i];
				res2 = res2 + vec2[i]*vec2[i];
				}
			res = res/(Math.sqrt(res1*res2));
			return res;
		}
	} 
	
	public double calKey(String key1,String key2){
		if (wordmap.containsKey(key1) && wordmap.containsKey(key2)){
			double res;
			res = calVector(wordmap.get(key1),wordmap.get(key2),dim);
			return res;
		}
		else{
			System.out.println("the vetor does not exsit");
			return (double) -2;
		}
	}
	
	
	public ArrayList<String>  keyList(){
		ArrayList<String> keylist = new ArrayList<String>();
		Iterator<String> it = wordmap.keySet().iterator();
        while (it.hasNext()) {
            String o = it.next();
            keylist.add(o);
        }
        return keylist;
	}
	
	
	public HashMap<String,int[]> mostWordList(String word, int size){
		String[] songList = new String[size];
		String[] keylist = new String[keydim];
		int[] keynum = new int[keydim];
		int number = 0,i=0,count1,count2,sizecount=0;
		
		while (number < dim){
			if (word.equals(worddim[number])){
				break;
			}
			number++;
		}

		for (String key : wordmap.keySet()) {
			keylist[i] = key.toString();
			keynum[i]=wordmap.get(key)[number];
			i++;
		}
		
	        
		while (size > 0){
			count1 = 0;
			count2=keynum[0];
			for (i=1;i<keydim;i++){
				if (keynum[i] > count2){
					count1 = i;
				}
			}
			songList[sizecount] = keylist[count1].toString();
			keynum[count1] = -1; 
			size--;
			sizecount++;
		}
		
		/*--------------------------------*/
		
		HashMap<String,int[]> songMap = new HashMap<String,int[]>();
		
		for(String song:songList){
			int[] wordList = this.wordmap.get(song);
			songMap.put(song, wordList);
		}
		
		return songMap;
		
		
	}
	
	public String[] getWordDim(){
		return this.worddim;
	}
	
	
	/*example.wordmap.containsKey("単語") 単語があるかどうかを調べる
		example.wordmap.get("単語")　単語のベクトルをゲットする（ベクトルを返す）
		example.calkey("単語","単語")単語の相関係数（－１～１）を返す。単語が存在しない場合は-2を返す、
		example.keylist() :歌の名前のリスト
		example.keylist().size()
		たとえばある次元の数を知りたいならば、keylist()の0番目からexample.keylist().size-1番目の要素でhashmapで検索、その次元を出力すればよい
		example.mostWordList(String word, int size)　単語wordの上位size個の歌の名前を返す、
	*/
	
	
	
	
	
	public static void main(String[] args){
		int i=0;
		HashSet<String> wordlist;
		Makehash example = new Makehash();
		example.readFile();
        System.out.println("dim is"+example.dim);
        System.out.println((example.wordmap.containsKey("インポート2")));
//        System.out.println(Arrays.asList(example.worddim));
        System.out.println(example.calKey("アップグレード","インポート"));
        
        while (i < example.dim) {
        	System.out.println(example.worddim[i]);
        	i++;
        }
        
//        wordlist = (HashSet<String>) example.mostWordList("修正プログラム",6).keySet();
//        for (i=0;i<6;i++){
//        	System.out.println(wordlist[i]);
//		}
        
        /*
        while (i < example.keyList().size()) {
        	System.out.println(example.keyList().get(i));
        	i++;
        }
		
		*/
		
	}
	

}

package makehash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

public class Kmeans {
	ArrayList<ArrayList<Element>> clusterList;
	ArrayList<Double[]> centerList;
	ArrayList<Element> elementList;
	HashMap<String,int[]> elementMap;
	String[] wordDim;
	int clusterNum;
	int degree;
	double range;
	double varLimit;
	
	Kmeans(HashMap<String,int[]> elementMap,String[] wordDim){
//		degree = 10;
		clusterNum = 5;
		range = 100;
		varLimit = 10;
		
		this.elementMap = elementMap;
		this.wordDim = wordDim;
		
		elementList = new ArrayList<Element>();
//		int elemntNum = 100;
		for(Map.Entry<String, int[]> e : elementMap.entrySet()) {
			Element song = new Element(e.getKey(),e.getValue());	
			elementList.add(song);
		}
		
		centerList = new ArrayList<Double[]>();
		for(int i=0;i<clusterNum;i++){
			if(centerList.isEmpty()){
				int index = (int) Math.floor(Math.random() * elementList.size());
				double[] centerRand = elementList.get(index).getCoordinate();
				centerList.add(ArrayUtils.toObject(centerRand));
			}else{
				double dSum = 0;
				
				for(Element song:elementList){
					double d = 0;
					double dMin = Double.MAX_VALUE;
					
					for(Double[] center:centerList){
						double[] centerP = ArrayUtils.toPrimitive(center);
						d = getDistance(song.getCoordinate(),centerP);
						if(d < dMin){
							dMin = d;
						}
					}
					
					song.setDistance(dMin);
					dSum += dMin;
				}
				
				//decide center of cluster
				double criteria = Math.random();
				double p = 0;
				
				for(Element element:elementList){
					p += element.getDistance() / dSum;
					if(p > criteria){
						centerList.add(ArrayUtils.toObject(element.getCoordinate()));
						break;
					}
				}
				
			}
			System.out.println("initial center[" + i + "] = " + Arrays.toString(centerList.get(i)));
		}
		
		clusterList = new ArrayList<ArrayList<Element>>();
		for(int i=0;i<clusterNum;i++){
			ArrayList<Element> cluster = new ArrayList<Element>();
			clusterList.add(cluster);
		}
		
		//set cluster
		for(Element element:elementList){
			double minD = Double.MAX_VALUE;
			int clusterIndex = 0;

			for(int i=0;i<centerList.size();i++){
				double[] center = ArrayUtils.toPrimitive(centerList.get(i));
				double d = getDistance(element.getCoordinate(),center);
				if(d < minD){
					minD = d;
					clusterIndex = i;
				}
			}
			clusterList.get(clusterIndex).add(element);			
		}
	}
	
	void clustering(){
		
		int count = 0;
		double var = Double.MAX_VALUE;
		
		ArrayList<Double[]> oldCenterList = new ArrayList<Double[]>();
		
		while(var > varLimit){
			var = 0;
			centerList = new ArrayList<Double[]>();
			
			for(int i=0;i<clusterNum;i++){
				ArrayList<Element> cluster = clusterList.get(i); 
				double[] center = getCenter(cluster);
				
				if(oldCenterList.isEmpty()){
					var = Double.MAX_VALUE;
				}else{
					for(int j=0;j<center.length;j++){
						var += Math.pow((center[j] - oldCenterList.get(i)[j]),2);
					}
				}
				
				centerList.add(ArrayUtils.toObject(center));
			}

			ArrayList<ArrayList<Element>> newClusterList = new ArrayList<ArrayList<Element>>();
			for(int i=0;i<clusterNum;i++){
				ArrayList<Element> cluster = new ArrayList<Element>();
				newClusterList.add(cluster);
			}

			for(Element song:elementList){
				double minD = Double.MAX_VALUE;
				int cluster = 0;

				for(int i=0;i<centerList.size();i++){
					double[] center = ArrayUtils.toPrimitive(centerList.get(i));
					double d = getDistance(song.getCoordinate(),center);
					if(d < minD){
						minD = d;
						cluster = i;
					}
				}
				newClusterList.get(cluster).add(song);
			}
			
			clusterList = newClusterList;
			oldCenterList = centerList;

			System.out.println("count = " + count);
			System.out.println("variation = " + var);
			for(int i=0;i<clusterList.size();i++){
				ArrayList<Element> cluster = clusterList.get(i);
				System.out.println("num of cluster[" + i + "] = " + cluster.size());
				//+ " center = " + Arrays.toString(centerList.get(i)));
				System.out.println(getClusterName(cluster));
				for(Element ele:cluster){
					System.out.print(ele.getName() + ",");
				}
				System.out.println();
			}
			
			count ++;
		}
	}
	
	double[] getCenter(ArrayList<Element> cluster){
		double[] center = null;
		
		if(cluster.isEmpty()){
			center = new double[degree];
			for(int j=0;j<center.length;j++){
				center[j] = (double) range/2;
			}
		}else{
			int clusterSize = cluster.size();
			
			for(Element song:cluster){
				double[] wordList = song.getCoordinate();
				
				if(center == null){
					center = wordList;
				}else{
					for(int i=0;i<center.length;i++){
						center[i] += wordList[i];
					}
				}
			}
			
			for(int i=0;i<center.length;i++){
				center[i] /= (double)clusterSize;
			}
		}
		
		return center;
	}
	
	double getDistance(double[] a1,double[] a2){
		double d = 0;
		for(int i=0;i<a1.length;i++){
			d += Math.pow(a1[i] - a2[i],2);
		}
		return d;
	}
	
	String getClusterName(ArrayList<Element> cluster){
		int[] word = null;
		for(Element ele:cluster){
			int[] wordArr = elementMap.get(ele.getName());
			if(word == null){
				word = wordArr;
			}else{
				for(int i=0;i<word.length;i++){
					word[i] += wordArr[i];
				}
			}	
		}
		
		double max = Double.MIN_VALUE;
		int index = 0;
		
		for(int i=0;i<word.length;i++){
			if(max < word[i]){
				index = i;
				max = word[i];
			}
		}
		
		return wordDim[index];
	}
	
	public static void main(String[] args){
		Makehash mh = new Makehash();
		mh.readFile();
		int size = 20;
		String word = "君";
		HashMap<String,int[]> songMap = mh.mostWordList(word, size);
		String[] wordDim = mh.getWordDim();
		
		Kmeans k = new Kmeans(songMap,wordDim);
		k.clustering();
		
//		if(mh.wordmap.containsKey(args)){
//			
//		}else{
//			System.out.println("such word does not exist.");
//			System.exit(1);
//		}
	}
	
}

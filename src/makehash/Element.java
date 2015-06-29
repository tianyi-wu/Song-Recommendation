package makehash;

import java.util.ArrayList;

public class Element {
	String name;
	double[] coordinate;
	double distance;
	
	Element(String name,int[] coordinate){
		this.name = name;
		this.coordinate = new double[coordinate.length];
		for(int i=0;i<coordinate.length;i++){
			this.coordinate[i] = (double) coordinate[i];
		}
		distance = 0;
	}
	
	double getDistance(){
		return this.distance;
	}
	
	void setDistance(double d){
		this.distance = d;
	}
	
	double[] getCoordinate(){
		return this.coordinate;
	}
	
	String getName(){
		return this.name;
	}
//	
//	void addCoordinate(int ele){
//		this.coordinate.add((double)ele);
//	}
}

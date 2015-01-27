package com.ok.mathlightalarm.math;

import java.util.Random;

public class Calculator {
	Random rand = new Random();
	int x = rand.nextInt(3);
	private int max,min,numberA,numberB;
	private float result;
	private int operator,current;
	public static int trueCount;
	String op;

	float[] numberArray = new float[]{11.11f,11.11f,11.11f};
	int [] ChosenArray=new int[]{111,111,111};
	public Calculator (int max, int min) 
	{
		this.max=max;
		this.min=min;

	}
   

    public void generate ()
    {
    	  numberA = (int) rand.nextInt(max - min + 1) + min;;
		  numberB = (int) rand.nextInt(max - min + 1) + min;
		
		     operator = rand.nextInt(4);
		    switch (operator) {
		    case 0:
		      result=numberA+numberB; 
		      op="+";
		      break;
		    case 1:
		    	result=numberA-numberB;
		    	op="-";
		      break;
		    case 2:
		    	result=(float)(numberA/numberB);
		    	op="/";
		      break;
		    case 3:
		    	result=numberA*numberB;
		    	op="*";
		      break;
		   
		    }
		    numberArray[0]=result;
		    for (int i = 1; i < numberArray.length; i++) 
		    { 
				int error = (int) rand.nextInt(9) -4; ;
				numberArray[i]=result+error;
				for (int a = 0; a < i; a++)
				{ 
					if(numberArray[a]==result+error)
					{
						i--;
						break;
					}
					else
					{
						numberArray[i]=result+error;
					}
					
				}
				
		    }
		    
		    current = rand.nextInt(3);
			 ChosenArray[0]= current;
		    for (int i = 1; i < ChosenArray.length; i++) 
		    {    
		    	current = rand.nextInt(3);
				 ChosenArray[i]= current;
				for (int a = 0; a < i; a++)
				{ 
					if(ChosenArray[a]== current)
					{
						i--;
						break;
					
					}
					else
					{
						ChosenArray[i]= current;
					}
				}
				
		    }
		  
    }
	public int getMax() {
		return max;
	}

	public void setMaxMin(int max,int min) {
		this.max = max;
		this.min = min;
	}
	public int getMin() {
		return min;
	}
	public int getOperator() {
		return operator;
	}

	public int getNumberA() {
		return numberA;
	}

	public void setNumberA(int numberA) {
		this.numberA = numberA;
	}

	public int getNumberB() {
		return numberB;
	}

	public void setNumberB(int numberB) {
		this.numberB = numberB;
	}

	public float getResult() {
		return result;
	}

	public void setResult(float result) {
		this.result = result;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public float[] getNumberArray() {
		return numberArray;
	}

	public void setNumberArray(float[] numberArray) {
		this.numberArray = numberArray;
	}

	public int[] getChosenArray() {
		return ChosenArray;
	}

	public void setChosenArray(int[] chosenArray) {
		ChosenArray = chosenArray;
	}

}

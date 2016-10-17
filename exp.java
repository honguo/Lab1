package expression;

import java.lang.management.OperatingSystemMXBean;
import java.sql.Time;
import java.util.*;
//import java.util.stream.Sink.ChainedReference;

import javax.print.attribute.standard.PrinterMessageFromOperator;

//每次只能单独的求导或者求值
//expression用于将输入的表达式存储为后最表达式形式，存储在栈中
//retVar用于将上述的栈中的相应的变量换成值，simplify后缀求值
//derivetive用于根据要求导的变量的前后变量来求导
//比如说x+y*x*z+1就会遍历表达式，找到第一个x，然后发现x的前后什么也没有，求导后为1
//然后找到y*x*z的x同理找到连续相乘的x的系数，即y*z，返回y*z,与求得的1相加
//同理最后得到1+y*z
public class exp {
	public static void main(String[] args) {
		System.out.print("print your expression:");
		Scanner in=new Scanner(System.in);
		String exp=in.nextLine();
		System.out.println(exp);
		expression temp1=new expression(exp);
		temp1.retExp();
		//System.out.println(temp1.value);
		System.out.println(temp1.value);
		System.out.println("input vars:");
		String inputValue=in.nextLine();
		TYPE type=temp1.retVar(inputValue);
		long currentTime=System.currentTimeMillis();
		if(type==TYPE.SIMPLIFY){
			String result=temp1.simplify();
			System.out.println(result);
		}
		else if(type==TYPE.DEREVETIVE){
			String de=temp1.derivetive();
			System.out.println(de);
		}
		long endTime=System.currentTimeMillis();
		System.out.println("time lasts: "+(endTime-currentTime)+"ms");
	}

}
class expression{//生成后缀表达式
	private String input;
	public Stack<String> value=new Stack<>();
	private Stack<String> op=new Stack<>();
	private Stack<String> origin=new Stack<>();
	private String[] vars;
	private String var;
	private int varLen;
	public expression(String input){
		this.input=input.substring(0);
	}
	public Stack<String> retExp(){///返回后缀表达式
		String top,now;
		int i,j=0;
		for(i=0;i<input.length();i++){
			if(isLegal(input.charAt(i))==3){
				System.out.println("input is illegal");
				System.exit(0);
			}
			if(isLegal(input.charAt(i))==2){
				origin.push(input.substring(j,i));
				origin.push(input.substring(i,i+1));
				value.push(input.substring(j,i));
				now=input.substring(i,i+1);
				if(!op.empty()){
					top=op.peek();
					while(retPriority(now)<=retPriority(top) && !op.empty()){
						value.push(op.pop());
						if(!op.empty()) top=op.peek();
					}
				}
				op.push(now);
				j=i+1;
			}
		}
		value.push(input.substring(j));
		origin.push(input.substring(j));
		while(!op.empty()){//reverse
			value.push(op.pop());
		}
		return value;
	}
	public TYPE retVar(String inputVars){///以字符串形式先后存储变量
		String[] varTemp=inputVars.split(" ");
		int i;
		String[] temp;
		if(varTemp[0].equals("!simplify")){
			if(varTemp.length>1){
				varLen=varTemp.length-1;
				String[] result=new String[2*varLen];
				for(i=0;i<varLen;i++){
					if(varTemp[i+1].indexOf("=")!=-1){
						temp=varTemp[i+1].split("=");
						result[2*i]=temp[0];
						result[2*i+1]=temp[1];
					}
					else{
						System.out.println("invalid input");
						System.out.println("invalid input");
						System.exit(0);
						break;
					}
				}
				vars=result;
				return TYPE.SIMPLIFY;
			}
			else{
				System.out.println(input);
				return TYPE.OTHER;
			}
		}
		else if(varTemp[0].equals("!d/d")){
			if(varTemp.length>1){
				var=varTemp[1].substring(0);
				return TYPE.DEREVETIVE;
			}
			else {
				System.out.println("null");
				return TYPE.OTHER;
			}
		}
		else{
			System.out.println("invalid input");
			System.exit(0);
			return TYPE.OTHER;
		}
	}
	public String derivetive(){//求导,针对顺序的表达式如[1,+,x,*,y]找到变量（而且是连乘的最后一个）将前后的相乘的拼接起来
		int i,j,k,index,count;
		int len,len2;
		String temp="";
		int size=origin.size();
		int count2=0;
		int flag=0;
		for(i=0;i<size;i++){
			if(origin.get(i).equals(var)){
				index=i;
				count=1;
				len=0;//向前拼接的长度
				len2=0;//向后
				/*********/
				for(j=i+1;j<size;j++){//找到最后的变量var
					if(origin.get(j).equals(var)){
						index=j;
						count++;
					}
					else if(origin.get(j).equals("+") || origin.get(j).equals("-")) break;
				}
				i=index;
				for(j=i-1;j>=0;j--){//找到前面的+,-
					if(origin.get(j).equals("+")){///如果是最前面的话不包含+
						if(count2==0) j=j+1;//只有是第一个求导的才不包含前面的+，如1+x*y
						break;
					}
					else if(origin.get(j).equals("-")) break;
				}
				for(k=(j<0?0:j);k<i-1;k++){//不包含最后的op,包含前面的+,-,拼接
					if(!origin.get(k).equals("+") && !origin.get(k).equals("-")) len++;
					temp+=origin.get(k);
				}
				if(j==i-1 && i-1>=0){//但是如果前面只有+，-的话，包括
					temp+=origin.get(i-1);
				}
				/***********/
				for(j=i+1;j<size;j++){//后面的子串
					if(origin.get(j).equals("+") || origin.get(j).equals("-")) break;
				}
				if(count>1) temp+=("*"+count);//x*x*x变为x*x*3
				for(k=i+2;k<j && k<size;k++){
					len2++;
				}
				if(len2<1 && len<1) {//前后都没有系数的
					temp+=1;
				}
				for(k=(len>0?i+1:i+2);k<j && k<size;k++){
					temp+=origin.get(k);
				}
			count2++;
			flag=1;
			}
		}//
		if(flag==0) return "0";
		return temp.substring(0);
	}
	public String simplify(){
		 int i,j;
		 String temp1,temp2;
		 Stack<String> temp=new Stack<>();
		 for(i=0;i<varLen;i++){
			 for(j=0;j<value.size();j++){
				 if(value.get(j).equals(vars[2*i])){
					 value.set(j, vars[2*i+1]);
				 }
			 }
		 }
		 //System.out.println(value);
		 for(i=0;i<value.size();i++){
			switch (value.get(i)) {
			case "+":
				temp1=temp.pop();
				temp2=temp.pop();
				if(IS.isDight(temp1) && IS.isDight(temp2)){
					temp1=(Integer.parseInt(temp2)+Integer.parseInt(temp1))+"";
					temp.push(temp1);
				}
				else{
					temp1=temp2+"+"+temp1;
					temp.push(temp1);
				}
				break;
			case "-":
				temp1=temp.pop();
				temp2=temp.pop();
				if(IS.isDight(temp1) && IS.isDight(temp2)){
					temp1=(Integer.parseInt(temp2)-Integer.parseInt(temp1))+"";
					temp.push(temp1);
				}
				else{
					temp1=temp2+"-"+temp1;
					temp.push(temp1);
				}
				break;
			case "*":
				temp1=temp.pop();
				temp2=temp.pop();
				if(IS.isDight(temp1) && IS.isDight(temp2)){
					temp1=(Integer.parseInt(temp2)*Integer.parseInt(temp1))+"";
					temp.push(temp1);
				}
				else{
					temp1=temp2+"*"+temp1;
					temp.push(temp1);
				}
				break;
			case "/":
				temp1=temp.pop();
				temp2=temp.pop();
				if(IS.isDight(temp1) && IS.isDight(temp2)){
					temp1=(Integer.parseInt(temp2)/Integer.parseInt(temp1))+"";
					temp.push(temp1);
				}
				else{
					temp1=temp2+"/"+temp1;
					temp.push(temp1);
				}
				break;
			default:
				temp.push(value.get(i));
			}
		 }
		 return temp.peek();
	}
	private int isLegal(char ch){//0,1,2,3分别代表数字，字母，op以及非法输入
		int temp=(int)ch;
		if(temp>=48 && temp<=57) return 0;
		else if((temp>=65 && temp<=90)||(temp>=97 && temp<=122)) return 1;
		else if(ch=='*' || ch=='+' || ch=='-' || ch=='/') return 2;
		else return 3;
	}
	private int retPriority(String ch){
		switch (ch) {
		case "+":
		case "-":
			return 0;
		case "*":
		case "/":
			return 1;
		default:
			return 2;
		}
	}
}
class IS{
	public static boolean isDight(String str){
		int i,index;
		for(i=0;i<str.length();i++){
			index=(int)str.charAt(i);
			if(i==0 && str.charAt(0)=='-') continue;
			if(index<48 || index>57) return false;
		}
		return true;
	}
}
enum TYPE{
	SIMPLIFY,
	DEREVETIVE,
	OTHER
}

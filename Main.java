import java.util.*;
import java.io.*;

public class Main{
	static int howManyLines = 0;
	static int howManyOnes = 0;
	static int howManyZeros = 0;
	static int howManyXs = 0;
	static int howManybits = 0;
		
	static int scanchains;
	static ArrayList<Map<Integer,Set<Integer>>> selectZhu = new ArrayList<Map<Integer,Set<Integer>>>();  //ArrayList��ӦС���Լ����У�Map��key��Ӧ���������������(��0����)��value��Ӧ��ЩС���Լ����Թ������������
	
	public static void readTestSet(String filename,ArrayList<String> testSet) throws Exception{
		BufferedReader bufr = null;
		String line = null;
		int i;
		bufr = new BufferedReader(new FileReader(filename));
		while((line = bufr.readLine())!=null){

			testSet.add(line);
			howManyLines++;
			
			for(i=0;i<line.length();i++){
				switch(line.charAt(i)){
					case '1' : howManyOnes++;break;
					case '0' : howManyZeros++;break;
					case 'X' :
					case 'x' : howManyXs++;break;
				}
			}
		}
		howManybits = howManyOnes + howManyZeros + howManyXs;
	}
	
	public static ArrayList<ArrayList<String>> getSmallSets(ArrayList<String> testSets,int n){
		
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> temp = null;
		int i;
		for(String t : testSets){
			temp = new ArrayList<String>();
			for(i=0;i<t.length();i+=n)
				if(i+n<t.length())
					temp.add(t.substring(i,i+n));
				else
					temp.add(t.substring(i));
			result.add(temp);
		}
		return result;
	}
	
	public static void adjust(ArrayList<String> testSet,int length){
		String temp = "";
		for(int i=0;i<testSet.size();i++){
			temp = "";
			temp = testSet.get(i);
			for(int j=temp.length();j<length;j++){
				temp+='Y';
			}
			testSet.set(i,temp);
		}
	}

	public static ArrayList<String> getHadmard(int level) throws Exception{ 
		
		if(level<0)
			throw new Exception("���������Ľ�������С��0��");

		ArrayList<String> result = new ArrayList<String>();
		if(level==0){
			result.add("1");
			return result;
		}
		
		ArrayList<String> part = new ArrayList<String>(getHadmard(level-1));

		for(String t : part)
			result.add(t+t);

		
		for(String t : part){
			String t2 = "";	
			for(int i=0;i<t.length();i++)
				if(t.charAt(i)=='1')
					t2 += '0';
				else
					t2 += '1';
			result.add(t+t2);
		}
		return result;
	}

	public static int[][] testToIntArray(ArrayList<String> test){
			int rows = test.size();
			int columns = test.get(0).length();
			
			int[][] results = new int[rows][columns];
			
			for(int i=0;i<rows;i++)
				for(int j=0;j<columns;j++)
					if(test.get(i).charAt(j)=='1')
						results[i][j] = 1;
					else if(test.get(i).charAt(j)=='0')
						results[i][j] = -1;
					else if(test.get(i).charAt(j)=='X' ||test.get(i).charAt(j)=='x')
						results[i][j] = 0;
					//X��ʱ��Ϊ-2
					else if(test.get(i).charAt(j)=='Y')
						results[i][j] = -2;
			return results;
	}
	public static int[][] adjustHadmard(ArrayList<String> hadmard){
			int rows = hadmard.size();
			int columns = hadmard.get(0).length();
			
			int[][] results = new int[rows][columns];
			
			for(int i=0;i<rows;i++)
				for(int j=0;j<columns;j++)
					if(hadmard.get(i).charAt(j)=='1')
						results[i][j] = 1;
					else if(hadmard.get(i).charAt(j)=='0')
						results[i][j] = -1;
			return results;
	}
		public static int[][] transform(int[][] test,int[][] hadmard,int smalltestsetNum) throws Exception{
			
			int testRows = test.length;
			int testColumns = test[0].length;
			
			int hadRows = hadmard.length;
			int hadColumns = hadmard[0].length;
			
			int[][] zhu = new int[testColumns][hadColumns];
			
			int[] temp = null;
			int addtmp = 0;    //ѡÿһ��������ʱ��temp����Ԫ�ص���ʱֵ��
			int index = -1;    //ѡÿһ��������ʱ��ѡ��������ֵ��
			
			for(int i=0;i<testColumns;i++){
				
				temp = new int[hadRows];
				//�Բ��Լ���ÿһ�н��б任��
				for(int j=0;j<hadRows;j++){
					addtmp = 0;
					for(int k=0;k<testRows;k++){
						if(test[k][i] == -2)
							continue;
						addtmp += hadmard[j][k]*test[k][i];
					}
					temp[j] = addtmp;	
				}
				//���ݲ��ԭ����в�֡�
				boolean indexHasValue = false;
				
				Map<Integer,Set<Integer>> mmap = selectZhu.get(i);
				for(int t : mmap.keySet())
					if(mmap.get(t).contains(smalltestsetNum)){
						index = t;
						indexHasValue = true;
						break;
					}
				if(!indexHasValue)
					index = pickAIndex2(temp);
				
				/*
				if(i == 6){
					System.out.println("index = "+index);
					System.out.println(mmap);
					Scanner fin = new Scanner(System.in);
					fin.nextInt();
				}
				*/
				for(int n=0;n<hadColumns;n++)
						zhu[i][n] = hadmard[index][n];     //�������������ƥ��
			}
			
			return zhu;
		}
		public static void startTransform(int[][] test,int[][] hadmard,String filename) throws Exception{
			
			int testRows = test.length;
			int testColumns = test[0].length;
			
			int hadRows = hadmard.length;
			int hadColumns = hadmard[0].length;
			
			int[][] zhu = new int[testColumns][hadColumns];
			
			int[] temp = null;
			int addtmp = 0;    //ѡÿһ��������ʱ��temp����Ԫ�ص���ʱֵ��
			int index = -1;    //ѡÿһ��������ʱ��ѡ��������ֵ��
			
			
			System.out.println("��ǰС���Լ�����Ϊ:"+testColumns);
			for(int i=0;i<testColumns;i++){
				Set<Integer> set = new HashSet<Integer>();   //��ſ�ѡ���������±꣬��0��ʼ
				
				temp = new int[hadRows];
				//�Բ��Լ���ÿһ�н��б任��
				for(int j=0;j<hadRows;j++){
					addtmp = 0;
					for(int k=0;k<testRows;k++){
						if(test[k][i] == -2)
							continue;
						addtmp += hadmard[j][k]*test[k][i];
					}
					temp[j] = addtmp;	
				}
				
				set = pickAIndex3(temp);
				BufferedWriter bufw = new BufferedWriter(new FileWriter("zhucost/"+filename,true));
				for(int t : set)
					bufw.write(t+"\t");
				bufw.newLine();
				bufw.close();
			}
		}
		
		/*
			�ѿ���ѡ���walsh�������±��¼����
		*/
		public static Set<Integer> pickAIndex3(int[] temp) throws Exception{
			int max = Integer.MIN_VALUE;
			Set<Integer> set = new HashSet<Integer>();
			for(int i=0;i<temp.length;i++)
				if(temp[i]>max)
					max = temp[i];
			for(int i=0;i<temp.length;i++)
				if(temp[i] == max)
					set.add(i);		
			return set;
		}
		
		public static int pickAIndex2(int[] temp) throws Exception{
			int max = Integer.MIN_VALUE;
			int maxIndex = -1;
			for(int i=0;i<temp.length;i++)
				if(temp[i]>max){
					max = temp[i];
					maxIndex = i;
				}
				/*
				else if(-temp[i]>max){
					max = -temp[i];
					maxIndex = -i;
				}
				*/
				
			return maxIndex;
		}

		public static int pickAIndex(int[] temp,int cmd) throws Exception{
			//����cmdѡ��������е�һ��ֵ��
			//cmd=1 ��ʾѡ���������ֵ��Ӧ������������ж�����ֵ��ô��������һ���������ɡ�
			
			int max = Integer.MIN_VALUE;
			Random rand = new Random();
			
			StringBuffer results = new StringBuffer(); //results��¼��������cmd���Ž������Ԫ�ص��±�
			
			if(cmd==1){
				for(int i=0;i<temp.length;i++)
					if(temp[i]>max)
						max = temp[i];
				for(int i=0;i<temp.length;i++)
					if(temp[i] == max)
						results.append(i);
				
				if(results.length()==1)
					return Integer.parseInt(results.charAt(0)+"");
				else
					return Integer.parseInt(results.charAt(rand.nextInt(results.length()))+"");
			}
			
			return -1;
		}
		
		//������任��ԭ����Ϊ�˵õ��з�����
		public static void revertMatrix(int[][] matrix){
			int rows = matrix.length;
			int columns = matrix[0].length;
			for(int i=0;i<rows;i++)
				for(int j=0;j<columns;j++)
						if(matrix[i][j]==-1)
							matrix[i][j] = 0;
						else if(matrix[i][j]==0)
							matrix[i][j] = -1; //-1����X
		}
		
		public static int[][] getCan(int[][] test,int[][] zhu) throws Exception{
			
			//���ָ�����Ҳ�ǿ��Եģ���Ϊ��Ӱ��������ж�
			revertMatrix(test);
			revertMatrix(zhu);
			
			int testRows = test.length;
			int testColumns = test[0].length;
			int judge = test[testRows-1].length;
			
			int[][] zhu2 = new int[zhu[0].length][zhu.length];
			for(int i=0;i<zhu.length;i++)	
				for(int j=0;j<zhu[i].length;j++)
					zhu2[j][i] = zhu[i][j];
			
			int zhuRows = zhu2.length;
			int zhuColumns = zhu2[0].length;
			
			int[][] results = new int[testRows][testColumns];
			for(int i=0;i<testRows;i++){
				for(int j=0;j<testColumns;j++){
					if(test[i][j] == -2){
						results[i][j] = -2;
						continue;
					}
					//�������С���Լ���-1����ô��ʾX��X���ܺ�˭����Ҷ�����0
					if(test[i][j] == -1){
						results[i][j] = 0;
						continue;
					}
					results[i][j] = test[i][j] ^ zhu2[i][j];
				}
			}
		
			return results;
		}
	
	public static void output(int[][] can,int fileCount,String kind) throws Exception{
		
		String dir = kind;    //"can/"
		File file = new File(dir);
		file.mkdirs();
		String filename = dir + "" + fileCount + ".txt";
		
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(filename));
			int rows = can.length;

			for(int i=0;i<rows;i++){
				for(int j=0;j<can[i].length;j++)
						bw.write(can[i][j]+"");
				bw.newLine();					
			}
		}catch(Exception e){
			throw new Exception("д�ļ�ʧ�ܣ�");		
		}finally{
			if(bw!=null)
				bw.close();
		}
	}

	public static String FDR(int length) throws Exception{
		
		if(length < 0)
			throw new Exception("�γ̳��Ȳ���Ϊ��ֵ��");
			
		if(length == 0)
			return "00";
		else if(length == 1)
			return "01";

		String result = "";
		int group=(int)(Math.ceil((Math.log(length+3.0)/Math.log(2.0)-1)));
		for(int i=1;i<group;i++)
			result += "1";
		result += "0";

		int tail=length-(1<<group)+2;
		
		String tailString = Integer.toString(tail,2);
		int tailStrLength = tailString.length();
		if(tailStrLength<group)
			for(int k=0;k<group-tailStrLength;k++)
				tailString = "0" + tailString;
		result += tailString;
		return result;
	}
	
	public static void main(String[] args) throws Exception{
		System.out.print("�������·���ƣ�");
		Scanner fin = new Scanner(System.in);
		String filename = fin.nextLine();
		fun(filename+".txt");
	}
	
	public static void fun(String fname) throws Exception{
		
		ArrayList<String> testset = new ArrayList<String>();
		readTestSet(fname,testset);
		System.out.println("��ȡԭʼ���Լ��ɹ���һ����" + howManybits +"λ��");
		System.out.println("ԭʼ���Լ�һ����"+howManyLines+"�У�");
		
		System.out.println("ԭ���Լ��Ĺ�ģΪ��"+testset.size()+"*"+testset.get(0).length());
		
		//С���Լ����ȵĻ���
		//1������ÿһ���г�ƽ����
		Scanner fin = new Scanner(System.in);
		System.out.print("����ɨ����������");
		scanchains = fin.nextInt();
		ArrayList<ArrayList<String>> smallsets = getSmallSets(testset,testset.get(0).length()%scanchains == 0 ? testset.get(0).length()/scanchains:(testset.get(0).length()/scanchains+1));
		
		System.out.println("�Ѿ��õ�С���Լ��ˣ�");
		System.out.println("С���Լ��Ĺ�ģΪ��"+smallsets.get(0).size()+"*"+smallsets.get(0).get(0).length());	
		
		//��С���Լ��ǣ����������浽�ļ��￴һ�£�
		String dir = "smalltest/";
		String filename = "";
		File file = new File(dir);
		file.mkdirs();

		BufferedWriter bufw = null;
		int count = 1;
		
		System.out.println("��ʼдС���Լ���");

		//дС���Լ�
		for(ArrayList<String> t : smallsets){
			filename = dir + count + ".txt";	
			count++;
			bufw = new BufferedWriter(new FileWriter(filename));
			for(int n=0;n<t.size();n++){
				bufw.write(t.get(n),0,t.get(n).length());
				bufw.newLine();
			}
			bufw.close();
		}
		
		File fff = new File("zhucost/");
		fff.mkdirs();
		
		//׼��������任���õ�����ѡȡ���������������浽zhucostĿ¼��
		int[][] hadmardPre = null;
		int[][] smallset = null;
		
		int ccount = 1;
		int hadmardLevel = 0;
		
		for(ArrayList<String> t : smallsets){
			
			adjust(t,t.get(0).length());
			//�ȵõ����ʵĹ��������
			int hadlevel = t.size();
			while((hadlevel & (hadlevel-1)) !=0)
				hadlevel++;
			
			ArrayList<String> hadmard = getHadmard((int)(Math.log(hadlevel)/Math.log(2)));
			System.out.println("�Ѿ��õ���������󣡽���Ϊ:"+Math.log((double)hadlevel)/Math.log((double)2)+"��");
			hadmardLevel = (int)(Math.log((double)hadlevel)/Math.log((double)2));
			hadmardPre = adjustHadmard(hadmard);
			smallset = testToIntArray(t);
			
			startTransform(smallset,hadmardPre,ccount+".txt");
			ccount++;
		}
		
		////////////////////////////////////////////////////////////////////////////////////
		System.out.print("��������������ѭ���Ĵ�����n����1����С�ڵ���7��:");
		
		//�����Ѿ�����zhucost�ļ��У������и���С���Լ������еĿ�ѡ��������
		int totalZhuCost = dealZhuCost(smallsets.get(0).get(0).length(),smallsets.size(),hadmardPre.length,hadmardLevel,fin.nextInt());
		
		////////////////////////////////////////////////////////////////////////////////////
		
		
		//��ʼ������任
		hadmardPre = null;
		smallset = null;
		int[][] zhu = null;
		int[][] can = null;
		count = 0;
		File f = new File("can/");
		f.mkdirs();
		
		String res = "";
		int totalResult = 0;   //�з�����Ҫ�洢��λ��
		
		File ff = new File("result/");
		ff.mkdirs();

		for(int m=0;m<smallsets.size();m++){
			count++;
			bufw = new BufferedWriter(new FileWriter("can/"+count+".txt"));
			
			adjust(smallsets.get(m),smallsets.get(m).get(0).length());
			//�ȵõ����ʵĹ��������
			int hadlevel = smallsets.get(m).size();
			while((hadlevel & (hadlevel-1)) !=0)
				hadlevel++;
			
			ArrayList<String> hadmard = getHadmard((int)(Math.log(hadlevel)/Math.log(2)));
			System.out.println("�Ѿ��õ���������󣡽���Ϊ:"+Math.log((double)hadlevel)/Math.log((double)2)+"��");
			
			hadmardPre = adjustHadmard(hadmard);
			smallset = testToIntArray(smallsets.get(m));
			
			zhu = transform(smallset,hadmardPre,m+1);
			can = getCan(smallset,zhu);
			
			//�Ѳз�����д���ļ�
			for(int n=0;n<can.length;n++){
				for(int l=0;l<can[n].length;l++)
					bufw.write(can[n][l]+"");
				bufw.newLine();
			}
			
			bufw.close();
			
			bufw = new BufferedWriter(new FileWriter("result/"+count+".txt"));
			bufw.write(getResult(can));
			bufw.close();
			totalResult += getResult(can).length();
		}
		
		System.out.println("��������Ҫ�洢��λ�� = " + totalZhuCost);	
		System.out.println("�з�����Ҫ�洢��λ�� = " + totalResult);
		
		System.out.println("ѹ����Ϊ��" + 1.0*(howManybits-totalResult-totalZhuCost)/howManybits);
		
	}
	
	//���زз�����ѹ������ַ�����
	public static String getResult(int[][] can) throws Exception{
		
		int rows = can.length;
		int columns = can[0].length;

		//int result = 0;
		
		int run_length = 0;
		
		boolean flag = false;
		
		String resultString = "";
		for(int i=0;i<rows;i++){
			for(int j=0;j<columns;j++){
				
				if(can[i][j] == -2){
					flag = true;
					break;
				}
				if(can[i][j] == 0)
					run_length++;
				else{
					//result += FDR(run_length).length();
					resultString += FDR(run_length);
					run_length = 0;

				}
			
			}
			
			if(flag)
				break;
		}
		return resultString;
	}
	/*
	public static int calCan(int smalltestsetCount) throws Exception{
		int totalCost = 0;  //�з�����Ҫ�洢����λ
		BufferedReader bufr = null;
		for(int i=1;i<=smalltestsetCount;i++){
			bufr = new BufferedReader(new FileReader("result/"+i+".txt"));
			while(bufr.hasNextLine())
				totalCost += bufr.readLine().length();
			bufr.close();
		}
		return totalCost;
	}
	*/
	public static int dealZhuCost(int smalltestsetColumns,int smalltestsetCount,int hadmardRows,int hadmardLevel,int nn) throws Exception{
		//ʮ����С���Լ�ÿһ�ж�Ӧ��set�����ڱ�Ǹ��е���ЩС���Լ��Ѿ��޳�
		Set<Integer> set = null;
		Set<Integer> set1 = null;  //��һ�ֹ�������������ЩС���Լ��ĵ�n�п��Բ��빲��
		Set<Integer> set2 = null;  //�ڶ��ֹ�������������ЩС���Լ��ĵ�n�п��Բ��빲��
		Set<Integer> set3 = null;  //�����ֹ�������������ЩС���Լ��ĵ�n�п��Բ��빲��
		Set<Integer> set4 = null;
		Set<Integer> set5 = null;
		Set<Integer> set6 = null;
		Set<Integer> set7 = null;
		Set<Integer> set8 = null;
		Set<Integer> set9 = null;
		Set<Integer> set10 = null;
		//�ܵ�����������
		int totalCost = 0;
		
		//n��ʾ��ǰ���ڱȶ�С���Լ��ǵĵ�n�С�
		for(int n=0;n<smalltestsetColumns;n++){
			
			//��ǰ�е���������Ҫ�洢��λ��
			int cost = 0;
			
			int testsetCount = smalltestsetCount; //С���Լ��ĸ���
			
			set = new HashSet<Integer>();
			set1 = new HashSet<Integer>();
			set2 = new HashSet<Integer>();
			set3 = new HashSet<Integer>();
			set4 = new HashSet<Integer>();
			set5 = new HashSet<Integer>();
			set6 = new HashSet<Integer>();
			set7 = new HashSet<Integer>();
			set8 = new HashSet<Integer>();
			set9 = new HashSet<Integer>();
			set10 = new HashSet<Integer>();
			
			Map<Integer,Set<Integer>> map = new HashMap<Integer,Set<Integer>>();
			int[] result = new int[hadmardRows];     //Ͱ����˼��,��ʾ16��������
			for(int i=1;i<=smalltestsetCount;i++){
				BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
				
				for(int k=0;k<n;k++)
					bufr.readLine();
				
				String[] line = bufr.readLine().split("\t");
				for(String t : line)
					result[Integer.parseInt(t)]++;
			}
			int[] res = findMax(result);
			BufferedWriter bufw = new BufferedWriter(new FileWriter("s5378ɨ��������Ϊ"+scanchains+".txt",true));
			bufw.write("��һ�֣�С���Լ��ĵ�"+(n+1)+"�У�"+"�����"+res[1]+"��С���Լ����Թ���������"+res[0]);
			cost += res[1];
			testsetCount -= res[1];
			bufw.newLine();
			bufw.close();
			
			//�ҵ�ʹ��res[0]�±���Ϊ�������ܹ��޳���ЩС���Լ���
			for(int i=1;i<=smalltestsetCount;i++){
				BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
				
				for(int k=0;k<n;k++)
					bufr.readLine();
				String[] line = bufr.readLine().split("\t");
				for(String t : line)
					if(Integer.parseInt(t) == res[0]){
						set.add(i);
						set1.add(i);
						continue;
					}
			}
			System.out.println("��һ�֣�set1="+set1+",set1.size()="+set1.size());
			
			map.put(res[0],set1);
			if(nn == 1)
				selectZhu.add(map);
			/////////////////////////////////////////////////////////////////////////////////////////////
			//��n�е�2�ֹ���������
			if(nn>=2){
				Map<Integer,Set<Integer>> map2 = new HashMap<Integer,Set<Integer>>();
				
				result = new int[hadmardRows];
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
					
					for(int k=0;k<n;k++)
						bufr.readLine();
					
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						result[Integer.parseInt(t)]++;
				}
				
				res = findMax(result);
				bufw = new BufferedWriter(new FileWriter("s5378ɨ��������Ϊ"+scanchains+".txt",true));
				bufw.write("�ڶ���,С���Լ��ĵ�"+(n+1)+"�У�"+"�����"+res[1]+"��С���Լ����Թ���������"+res[0]);
				cost += (res[1]*2);
				testsetCount -= res[1];
				bufw.newLine();
				bufw.close();
				
				
				///////////////////////////////////////////////////////////////////////////////////////////
				//�ҵ�ʹ��res[0]�±���Ϊ�������ܹ��޳���ЩС���Լ���
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
	
					for(int k=0;k<n;k++)
						bufr.readLine();
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						if(Integer.parseInt(t) == res[0]){
							set.add(i);
							set2.add(i);
							continue;
						}
				}
				System.out.println("�ڶ��֣�set2="+set2+",set2.size()="+set2.size());
				map.put(res[0],set2);
				
				if(nn == 2){
					selectZhu.add(map);
				}
			}
			/////////////////////////////////////////////////////////////////////////////////////////////
			//��n�е�3�ֹ���������
			if(nn>=3){
				Map<Integer,Set<Integer>> map3 = new HashMap<Integer,Set<Integer>>();
				
				result = new int[hadmardRows];
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
					
					for(int k=0;k<n;k++)
						bufr.readLine();
					
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						result[Integer.parseInt(t)]++;
				}
				
				res = findMax(result);
				bufw = new BufferedWriter(new FileWriter("s5378ɨ��������Ϊ"+scanchains+".txt",true));
				bufw.write("������,С���Լ��ĵ�"+(n+1)+"�У�"+"�����"+res[1]+"��С���Լ����Թ���������"+res[0]);
				cost += (res[1]*3);
				testsetCount -= res[1];
				bufw.newLine();
				bufw.close();
				
				
				///////////////////////////////////////////////////////////////////////////////////////////
				//�ҵ�ʹ��res[0]�±���Ϊ�������ܹ��޳���ЩС���Լ���
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
	
					for(int k=0;k<n;k++)
						bufr.readLine();
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						if(Integer.parseInt(t) == res[0]){
							set.add(i);
							set3.add(i);
							continue;
						}
				}
				System.out.println("�����֣�set3="+set3+",set3.size()="+set3.size());
				map.put(res[0],set3);
				if(nn == 3){
					selectZhu.add(map);
				}
			}
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			//��n�е�4�ֹ���������
			if(nn>=4){
				Map<Integer,Set<Integer>> map4 = new HashMap<Integer,Set<Integer>>();
				
				result = new int[hadmardRows];
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
					
					for(int k=0;k<n;k++)
						bufr.readLine();
					
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						result[Integer.parseInt(t)]++;
				}
				
				res = findMax(result);
				bufw = new BufferedWriter(new FileWriter("s5378ɨ��������Ϊ"+scanchains+".txt",true));
				bufw.write("������,С���Լ��ĵ�"+(n+1)+"�У�"+"�����"+res[1]+"��С���Լ����Թ���������"+res[0]);
				cost += (res[1]*4);
				testsetCount -= res[1];
				bufw.newLine();
				bufw.close();
				
				
				///////////////////////////////////////////////////////////////////////////////////////////
				//�ҵ�ʹ��res[0]�±���Ϊ�������ܹ��޳���ЩС���Լ���
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
	
					for(int k=0;k<n;k++)
						bufr.readLine();
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						if(Integer.parseInt(t) == res[0]){
							set.add(i);
							set4.add(i);
							continue;
						}
				}
				System.out.println("�����֣�set4="+set4+",set4.size()="+set4.size());
				map.put(res[0],set4);
				if(nn == 4){
					selectZhu.add(map);
				}
			}
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			//��n�е�5�ֹ���������
			if(nn>=5){
				Map<Integer,Set<Integer>> map5 = new HashMap<Integer,Set<Integer>>();
				
				result = new int[hadmardRows];
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
					
					for(int k=0;k<n;k++)
						bufr.readLine();
					
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						result[Integer.parseInt(t)]++;
				}
				
				res = findMax(result);
				bufw = new BufferedWriter(new FileWriter("s5378ɨ��������Ϊ"+scanchains+".txt",true));
				bufw.write("������,С���Լ��ĵ�"+(n+1)+"�У�"+"�����"+res[1]+"��С���Լ����Թ���������"+res[0]);
				cost += (res[1]*5);
				testsetCount -= res[1];
				bufw.newLine();
				bufw.close();
				
				
				///////////////////////////////////////////////////////////////////////////////////////////
				//�ҵ�ʹ��res[0]�±���Ϊ�������ܹ��޳���ЩС���Լ���
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
	
					for(int k=0;k<n;k++)
						bufr.readLine();
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						if(Integer.parseInt(t) == res[0]){
							set.add(i);
							set5.add(i);
							continue;
						}
				}
				System.out.println("�����֣�set5="+set5+",set5.size()="+set5.size());
				map.put(res[0],set5);
				if(nn == 5){
					selectZhu.add(map);
				}
			}
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			//��n�е�6�ֹ���������
			if(nn>=6){
				Map<Integer,Set<Integer>> map6 = new HashMap<Integer,Set<Integer>>();
				
				result = new int[hadmardRows];
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
					
					for(int k=0;k<n;k++)
						bufr.readLine();
					
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						result[Integer.parseInt(t)]++;
				}
				
				res = findMax(result);
				bufw = new BufferedWriter(new FileWriter("s5378ɨ��������Ϊ"+scanchains+".txt",true));
				bufw.write("������,С���Լ��ĵ�"+(n+1)+"�У�"+"�����"+res[1]+"��С���Լ����Թ���������"+res[0]);
				cost += (res[1]*6);
				testsetCount -= res[1];
				bufw.newLine();
				bufw.close();
				
				
				///////////////////////////////////////////////////////////////////////////////////////////
				//�ҵ�ʹ��res[0]�±���Ϊ�������ܹ��޳���ЩС���Լ���
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
	
					for(int k=0;k<n;k++)
						bufr.readLine();
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						if(Integer.parseInt(t) == res[0]){
							set.add(i);
							set6.add(i);
							continue;
						}
				}
				System.out.println("�����֣�set6="+set6+",set6.size()="+set6.size());
				map.put(res[0],set6);
				if(nn == 6){
					selectZhu.add(map);
				}
			}
			
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			//��n�е�7�ֹ���������
			if(nn>=7){
				Map<Integer,Set<Integer>> map7 = new HashMap<Integer,Set<Integer>>();
				
				result = new int[hadmardRows];
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
					
					for(int k=0;k<n;k++)
						bufr.readLine();
					
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						result[Integer.parseInt(t)]++;
				}
				
				res = findMax(result);
				bufw = new BufferedWriter(new FileWriter("s5378ɨ��������Ϊ"+scanchains+".txt",true));
				bufw.write("������,С���Լ��ĵ�"+(n+1)+"�У�"+"�����"+res[1]+"��С���Լ����Թ���������"+res[0]);
				cost += (res[1]*7);
				testsetCount -= res[1];
				bufw.newLine();
				bufw.close();
				
				
				///////////////////////////////////////////////////////////////////////////////////////////
				//�ҵ�ʹ��res[0]�±���Ϊ�������ܹ��޳���ЩС���Լ���
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
	
					for(int k=0;k<n;k++)
						bufr.readLine();
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						if(Integer.parseInt(t) == res[0]){
							set.add(i);
							set7.add(i);
							continue;
						}
				}
				System.out.println("�����֣�set4="+set4+",set4.size()="+set4.size());
				map.put(res[0],set7);
				if(nn == 7){
					selectZhu.add(map);
				}
			}
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			//��n�е�8�ֹ���������
			if(nn>=8){
				Map<Integer,Set<Integer>> map8 = new HashMap<Integer,Set<Integer>>();
				
				result = new int[hadmardRows];
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
					
					for(int k=0;k<n;k++)
						bufr.readLine();
					
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						result[Integer.parseInt(t)]++;
				}
				
				res = findMax(result);
				bufw = new BufferedWriter(new FileWriter("s5378ɨ��������Ϊ"+scanchains+".txt",true));
				bufw.write("�ڰ���,С���Լ��ĵ�"+(n+1)+"�У�"+"�����"+res[1]+"��С���Լ����Թ���������"+res[0]);
				cost += (res[1]*8);
				testsetCount -= res[1];
				bufw.newLine();
				bufw.close();
				
				
				///////////////////////////////////////////////////////////////////////////////////////////
				//�ҵ�ʹ��res[0]�±���Ϊ�������ܹ��޳���ЩС���Լ���
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
	
					for(int k=0;k<n;k++)
						bufr.readLine();
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						if(Integer.parseInt(t) == res[0]){
							set.add(i);
							set8.add(i);
							continue;
						}
				}
				System.out.println("�ڰ��֣�set8="+set8+",set8.size()="+set8.size());
				map.put(res[0],set8);
				if(nn == 8){
					selectZhu.add(map);
				}
			}
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			//��n�е�9�ֹ���������
			if(nn>=9){
				Map<Integer,Set<Integer>> map9 = new HashMap<Integer,Set<Integer>>();
				
				result = new int[hadmardRows];
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
					
					for(int k=0;k<n;k++)
						bufr.readLine();
					
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						result[Integer.parseInt(t)]++;
				}
				
				res = findMax(result);
				bufw = new BufferedWriter(new FileWriter("s5378ɨ��������Ϊ"+scanchains+".txt",true));
				bufw.write("�ھ���,С���Լ��ĵ�"+(n+1)+"�У�"+"�����"+res[1]+"��С���Լ����Թ���������"+res[0]);
				cost += (res[1]*9);
				testsetCount -= res[1];
				bufw.newLine();
				bufw.close();
				
				
				///////////////////////////////////////////////////////////////////////////////////////////
				//�ҵ�ʹ��res[0]�±���Ϊ�������ܹ��޳���ЩС���Լ���
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
	
					for(int k=0;k<n;k++)
						bufr.readLine();
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						if(Integer.parseInt(t) == res[0]){
							set.add(i);
							set8.add(i);
							continue;
						}
				}
				System.out.println("��9�֣�set9="+set9+",set9.size()="+set9.size());
				map.put(res[0],set9);
				if(nn == 9){
					selectZhu.add(map);
				}
			}
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			//��n�е�10�ֹ���������
			if(nn>=10){
				Map<Integer,Set<Integer>> map10 = new HashMap<Integer,Set<Integer>>();
				
				result = new int[hadmardRows];
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
					
					for(int k=0;k<n;k++)
						bufr.readLine();
					
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						result[Integer.parseInt(t)]++;
				}
				
				res = findMax(result);
				bufw = new BufferedWriter(new FileWriter("s5378ɨ��������Ϊ"+scanchains+".txt",true));
				bufw.write("��10��,С���Լ��ĵ�"+(n+1)+"�У�"+"�����"+res[1]+"��С���Լ����Թ���������"+res[0]);
				cost += (res[1]*10);
				testsetCount -= res[1];
				bufw.newLine();
				bufw.close();
				
				
				///////////////////////////////////////////////////////////////////////////////////////////
				//�ҵ�ʹ��res[0]�±���Ϊ�������ܹ��޳���ЩС���Լ���
				for(int i=1;i<=smalltestsetCount;i++){
					if(set.contains(i))
						continue;
					BufferedReader bufr = new BufferedReader(new FileReader("zhucost/"+i+".txt"));
	
					for(int k=0;k<n;k++)
						bufr.readLine();
					String[] line = bufr.readLine().split("\t");
					for(String t : line)
						if(Integer.parseInt(t) == res[0]){
							set.add(i);
							set10.add(i);
							continue;
						}
				}
				System.out.println("��10�֣�set10="+set10+",set10.size()="+set10.size());
				map.put(res[0],set10);
				if(nn == 10){
					selectZhu.add(map);
				}
			}
			//selectZhu.add(map);
			
			cost += (testsetCount*(nn+hadmardLevel));
			totalCost += cost;
		}
		System.out.println("s5378��sc="+scanchains+"ʱ���������ܹ�����Ϊ"+totalCost);
		return totalCost;
	}
	
	private static int[] findMax(int[] arr) throws Exception{
		int[] result = new int[2];  //result[0]��ʾarr���������ֵ���±꣬result[1]��ʾ�Ǹ����ֵ
		for(int i=0;i<arr.length;i++)
			if(arr[i] > result[1]){
				result[0] = i;
				result[1] = arr[i];
			}		
		return result;
	}
}
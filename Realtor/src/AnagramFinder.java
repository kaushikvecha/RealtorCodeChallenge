

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AnagramFinder {
	static Hashtable<Long, ArrayList<String>> dictData = new Hashtable<Long, ArrayList<String>>();
	static long primes[]= {167,79,103,113,211,127,7,101,193,233,229,157,23,241,97,223,241,199,139,31,83,173,53,89,239,167};
	public static void main(String[] args) throws InterruptedException, IOException {

		System.out.println("Welcome to the Anagram Finder");
		System.out.println("-----------------------------");

		long fileLoadStartTime = System.nanoTime();
		//File f1=new File("");
		File f = new File(args[0]);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e1) {
			System.out.println("Invalid File: Please provide valid file");
			e1.printStackTrace();
		}
		
		loadFile(br, dictData);
		//System.out.println(dictData);
		long fileLoadEndTime = System.nanoTime();

		System.out.print("Dictionary loaded in " + (fileLoadEndTime - fileLoadStartTime) / 1000000 + " ms");
		Scanner s = new Scanner(System.in);
		while (true) {
			System.out.print("\n\n\nAnagramFinder>");
			if (s.hasNext()) {
				
				String input = s.next();
				
				if (!input.equalsIgnoreCase("exit")) {
					long sTime = System.nanoTime();
					long hash=1;
					for(int i=0;i<input.length();i++)
					{
						long x=primes[input.charAt(i)-'a'];
						hash*=x;
					}
					
					long eTime = System.nanoTime();
						if(dictData.containsKey(hash))
						{
							System.out.println(dictData.get(hash).size() + " Anagrams found for " + input + " in "
									+ (eTime - sTime) / 1000000 + "ms");
							printingWordsByAppendingComma(dictData.get(hash));
						}
						else
						{
							System.out.print(
									"No Anagrams found for " + input + " in " + (eTime - sTime) / 1000000 + "ms");
						}
					}
				else {
					System.exit(1);
					break;
				}
			}
		}
		br.close();
		s.close();
	}

	private static void printingWordsByAppendingComma(List<String> l) {
		Iterator<String> it = l.iterator();
		while (it.hasNext()) {
			System.out.print(it.next());
			if (it.hasNext())
				System.out.print(",");
		}
	}

	private static void loadFile(BufferedReader br, Hashtable<Long, ArrayList<String>> dictData) throws IOException {
		String st;
		ExecutorService e = Executors.newFixedThreadPool(5);
		while ((st = br.readLine()) != null) {
			Runnable r=new AnagramAlgo(st);
			e.execute(r);
		}
		br.close();
	}
}

class AnagramAlgo implements Runnable {
	String dictionaryWord;
	AnagramAlgo(String t) {
		this.dictionaryWord = t;
	}

	@Override
	public void run() {
		isAnagram(dictionaryWord);
	}

	public void isAnagram(String st) {
		ArrayList<String> list;
		long mul=1;
		for(int i=0;i<st.length();i++)
		{
			long x=AnagramFinder.primes[st.charAt(i)-'a'];
			mul*=x;
		}
		if(AnagramFinder.dictData.containsKey(mul))
		{
			AnagramFinder.dictData.get(mul).add(st);
		}
		else {
			list=new ArrayList<String>();
			list.add(st);
			AnagramFinder.dictData.put(mul, list);
		}
	}

}

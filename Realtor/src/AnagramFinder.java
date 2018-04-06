import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AnagramFinder {
	public static void main(String[] args) throws InterruptedException, IOException {

		System.out.println("Welcome to the Anagram Finder");
		System.out.println("-----------------------------");

		long fileLoadStartTime = System.nanoTime();

		File f = new File(args[0]);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e1) {
			System.out.println("Invalid File: Please provide valid file");
			e1.printStackTrace();
		}
		HashMap<Integer, ArrayList<String>> dictData = new HashMap<Integer, ArrayList<String>>();
		loadFile(br, dictData);

		long fileLoadEndTime = System.nanoTime();

		System.out.print("Dictionary loaded in " + (fileLoadEndTime - fileLoadStartTime) / 1000000 + " ms");

		while (true) {
			System.out.print("\n\n\nAnagramFinder>");
			Scanner s = new Scanner(System.in);
			if (s.hasNext()) {
				String input = s.next();
				input = input.toLowerCase();
				if (!input.equalsIgnoreCase("exit")) {
					long sTime = System.nanoTime();
					ExecutorService e = threadPool(dictData, input);
					long eTime = System.nanoTime();
					if (e.isTerminated()) {
						if (AnagramAlgo.l.size() > 0) {
							System.out.println(AnagramAlgo.l.size() + " Anagrams found for " + input + " in "
									+ (eTime - sTime) / 1000000 + "ms");
							printingWordsByAppendingComma();
						} else {
							System.out.print(
									"No Anagrams found for " + input + " in " + (eTime - sTime) / 1000000 + "ms");
						}
						AnagramAlgo.l.clear();
					}
				} else {
					s.close();
					break;
				}
			}
		}
	}

	private static void printingWordsByAppendingComma() {
		Iterator<String> it = AnagramAlgo.l.iterator();
		while (it.hasNext()) {
			System.out.print(it.next());
			if (it.hasNext())
				System.out.print(",");
		}
	}

	private static ExecutorService threadPool(HashMap<Integer, ArrayList<String>> dictData, String input)
			throws InterruptedException {
		ArrayList<String> filteredData = dictData.get(input.length());
		ExecutorService e = Executors.newFixedThreadPool(3);
		for (String p : filteredData) {
			Runnable r = new AnagramAlgo(input, p);
			e.execute(r);
		}
		e.shutdown();
		e.awaitTermination(1L, TimeUnit.SECONDS);
		return e;
	}

	private static void loadFile(BufferedReader br, HashMap<Integer, ArrayList<String>> dictData) throws IOException {
		String st;
		ArrayList<String> list;
		while ((st = br.readLine()) != null) {
			if (dictData.get(st.length()) != null) {
				dictData.get(st.length()).add(st);
			} else {
				list = new ArrayList<String>();
				list.add(st);
				dictData.put(st.length(), list);
			}
		}
		br.close();
	}
}

class AnagramAlgo implements Runnable {
	String input;
	String dictionaryWord;
	static List<String> l = new ArrayList<String>();

	AnagramAlgo(String s, String t) {
		this.input = s;
		this.dictionaryWord = t;
	}

	@Override
	public void run() {
		if (isAnagram(input, dictionaryWord)) {
			l.add(dictionaryWord);
		}
	}

	public boolean isAnagram(String s, String t) {
		int[] alpha = new int[26];
		if (s.length() != t.length())
			return false;
		for (int i = 0; i < s.length(); i++)
			alpha[s.charAt(i) - 'a']++;
		for (int i = 0; i < t.length(); i++)
			alpha[t.charAt(i) - 'a']--;
		for (int i : alpha)
			if (i != 0)
				return false;
		return true;
	}

}
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		HashMap<Integer, ArrayList<String>> dictionaryData = new HashMap<Integer, ArrayList<String>>();
		loadFile(br, dictionaryData);

		long fileLoadEndTime = System.nanoTime();

		System.out.print("Dictionary loaded in " + (fileLoadEndTime - fileLoadStartTime) / 1000000 + " ms");

		while (true) {
			System.out.print("\n\n\nAnagramFinder>");
			Scanner s = new Scanner(System.in);
			if (s.hasNext()) {
				String input = s.next();
				Pattern p = Pattern.compile("[a-zA-Z]");
				Matcher m = p.matcher(input);
				input = input.toLowerCase();
				if (m.find()) {
					if (!input.equalsIgnoreCase("exit")) {
						long sTime = System.nanoTime();
						ExecutorService e = threadPool(dictionaryData, input);
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
				} else
					System.out.println("Try giving only alphabets");
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

	private static ExecutorService threadPool(HashMap<Integer, ArrayList<String>> dictionaryData, String input)
			throws InterruptedException {
		ArrayList<String> filteredData = dictionaryData.get(input.length());
		ExecutorService e = Executors.newFixedThreadPool(3);
		for (String p : filteredData) {
			Runnable r = new AnagramAlgo(input, p);
			e.execute(r);
		}
		e.shutdown();
		e.awaitTermination(1L, TimeUnit.SECONDS);
		return e;
	}

	private static void loadFile(BufferedReader br, HashMap<Integer, ArrayList<String>> dictionaryData)
			throws IOException {
		String st;
		ArrayList<String> list;
		while ((st = br.readLine()) != null) {
			if (dictionaryData.get(st.length()) != null) {
				dictionaryData.get(st.length()).add(st);
			} else {
				list = new ArrayList<String>();
				list.add(st);
				dictionaryData.put(st.length(), list);
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
		try {
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
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Please provide only alphabets in input");
			System.exit(1);
			return false;
		}
	}

}

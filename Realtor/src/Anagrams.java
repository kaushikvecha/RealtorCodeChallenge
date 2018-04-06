import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Anagrams {

	public static void main(String[] args) throws IOException {
		System.out.println("Welcome to the Anagram Finder");
		System.out.println("-----------------------------");
		File f = new File(args[0]);
		BufferedReader br = new BufferedReader(new FileReader(f));
		System.out.print("\nAnagramFinder>");
		Scanner s = new Scanner(System.in);
		String st;
		if (s.hasNext()) {
			String input = s.next();
			if (!input.equalsIgnoreCase("exit")) {

				long startTime = System.nanoTime();
				List<String> list = new ArrayList<String>();
				while ((st = br.readLine()) != null) {
					if (isAnagram(input, st))
						list.add(st);
				}
				long endTime = System.nanoTime();
				System.out.println(list.size() + " Anagrams found for " + input + " in "
						+ (endTime - startTime) / 1000000 + " ms");
				Iterator<String> it = list.iterator();
				while (it.hasNext()) {
					System.out.print(it.next());
					if (it.hasNext())
						System.out.print(",");
				}
				list.clear();

				br.close();

			} else {
				s.close();

			}
		}

	}

	public static boolean isAnagram(String s, String t) {
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

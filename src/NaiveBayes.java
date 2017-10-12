import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class NaiveBayes {
	
	//	This function reads in a file and returns a 
	//	set of all the tokens. It ignores the subject line
	//
	//	If the email had the following content:
	//
	//	Subject: Get rid of your student loans
	//	Hi there ,
	//	If you work for us, we will give you money
	//	to repay your student loans . You will be 
	//	debt free !
	//	FakePerson_22393
	//
	//	This function would return to you
	//	[hi, be, student, for, your, rid, we, get, of, free, if, you, us, give, !, repay, will, loans, work, fakeperson_22393, ,, ., money, there, to, debt]
	
	// lterate one file and put all the words and the time seen in the emails		
	
	public static HashSet<String> tokenSet(File filename) throws IOException {
		HashSet<String> tokens = new HashSet<String>();
		Scanner filescan = new Scanner(filename);
		filescan.next(); //Ignoring "Subject"
		while(filescan.hasNextLine() && filescan.hasNext()) {
			tokens.add(filescan.next());
		}
		filescan.close();
		return tokens;
	}	
	
	public static void wordSet(File filename, 
			Map<String, Integer> wordmap) throws IOException{
		HashSet<String> temp = tokenSet(filename);
		for(String word : temp){
			if(wordmap.containsKey(word)){
				wordmap.put(word, wordmap.get(word)+1);
			}else{
				wordmap.put(word, 1);
			}
		}
	}		
	
	// put all the words with its probability in a map
	public static Map<String, Double> wordFrequency(File filePath) throws IOException {
		
		// iterate all the files in a folder and add new words to the map
		Map<String, Integer> wordmap = new HashMap<>();
		for(File fileName : filePath.listFiles()){
			wordSet(fileName, wordmap);
		}
		
		
		//convert this map to a map with the word and its associated 
		//probability seen in all the emails in this folder 
		Map<String, Double> result = new HashMap<String, Double>();
		int emails = filePath.listFiles().length;

        for(String word : wordmap.keySet()){
        	double frequency = 1.0 * (wordmap.get(word) + 1) / (emails + 2);
			result.put(word, frequency);
        }
		return result;
	}
	
	//calculate the probability of being ham and spam of unlabeled emails
	public static void printResult(File hamPath, File spamPath,
			File testPath) throws IOException{
		
		Map<String, Double> hamFrequency = wordFrequency(hamPath);
		Map<String, Double> spamFrequency = wordFrequency(spamPath);
		
		int hamLength = hamPath.listFiles().length;
		int spamLength = spamPath.listFiles().length;
		for(File fileName:testPath.listFiles()){
			
			//calculate p(h) and p(s)
			double h=1.0*hamPath.listFiles().length/
					(hamPath.listFiles().length+spamPath.listFiles().length);
			double s=1-h;
			h = Math.log(h);	
			s= Math.log(s);
			Scanner filescan = new Scanner(fileName);
			filescan.next(); //Ignoring "Subject"
			while(filescan.hasNextLine() && filescan.hasNext()) {
				String word = filescan.next();
				
				//check if the new word isn't in ham and spam training data 
				// then calculate the probability of all the words in the new email
				if(hamFrequency.containsKey(word)||spamFrequency.containsKey(word)){
					if(hamFrequency.containsKey(word)){
						h+=hamFrequency.get(word);
					}else{
						h+=Math.log(1.0/(hamLength+2));
					}
					if(spamFrequency.containsKey(word)){
						s+=spamFrequency.get(word);
					}else{
						s+=Math.log(1.0/(spamLength+2));////spamLength
					}
				}				
			}
			filescan.close();
			
			// remove the path from the file name
			String name = fileName.toString().replace("src\\data\\test\\", "");

			// return ham if the probability of ham is greater, otherwise return spam
			if (h > s) {
				System.out.println(name + " ham");
			} else {
				System.out.println(name + " spam");
			}
		}		
	}

	
	public static void main(String[] args) throws IOException {
		//TODO: Implement the Naive Bayes Classifier
		
		File hamPath = new File("src/data/train/ham");
		File spamPath = new File("src/data/train/spam");
		File testPath = new File("src/data/test");
		
		printResult(hamPath, spamPath, testPath);		
	}	
	
}

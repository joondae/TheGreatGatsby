import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TextGenerator {
	/**
	 * br.read() returns next char in file as an int
	 * br.ready() returns boolean if at end of file
	 * must use br.close() once you're done reading the file
	 * equivalent for Scanner is sc.next(), sc.hasNext(),and sc.close()
	 */
	File f;
	FileReader fr;
	BufferedReader br;
	
	public TextGenerator(String inputFileName) {
		
		
		try {
			f = new File(inputFileName);
			fr = new FileReader(f);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			System.out.println("Can't open " + inputFileName);
			System.exit(1);
		}
		
		//POSSIBLE RELOCATION OF CODE???
	}

	/**
	 * Stout says this method should run in O(N) time i.e. only iterate through book text once
	 * have to do all the steps on the fly??????
	 * 
	 * IS IT POSSIBLE TO COMBINE STEPS 1, 2, & 3 INTO 2 STEPS? 1 STEP?
	 * (to optimize big-O time)
	 */
	public void generateText(int chainorder, String outputFileName, int numChars) {
		if(numChars <= 0 || numChars > f.length()) {
			throw new IllegalArgumentException("Illegal argument for numChars");
		}
		
		if(f.length() <= 0) {
			throw new IllegalArgumentException("Book text is blank");
		}
		
		if(chainorder < 1 || chainorder >= f.length()) {
			throw new IllegalArgumentException("Illegal argument for chainorder");
		}
				
		//HOW MUCH TIME ARE YOU REALLY SAVING USING THIS INSTEAD OF A STRING???
		StringBuilder chunk = new StringBuilder();
		boolean lastChunkUnique = true;
		//to track first index of chunk w/ respect to book text (used for lastChunk condition)
		int fileIndex = 0;
		HashMap<StringBuilder, GatsbyNode> hashMap = new HashMap<StringBuilder, GatsbyNode>();
		
		/**
		 * STEP 1: iterate through book's text and store data on frequencies
		 * do not do latter for last chunk (special case)
		 * HOW TO ENSURE BR DOESN'T TRY TO READ OUTSIDE OF TXT FILE (≈ IOOBE)???
		 * .ready() DOES NOT NECCESSARILY MEAN YOU'VE REACHED THE END OF BOOK TEXT (only if stream is ready)
		 */
		try {
			//build chunk of appropriate length to analyze frequencies (or not)
			//one time thing to "get the ball rolling"
			for(int i = 0; i < chainorder; i++) {
				chunk.append((char) br.read());
			}
			
			outerloop:
			while (br.ready()) {				
					//System.out.println("\nchunksList: " + chunksList + "\n");
				
				//letter to be analyzed for frequency BUT also the next char to tack on to chunk
				char letterToAnalyze = (char) br.read();
					//System.out.println("letterToAnalyze: " + letterToAnalyze);
		
				inputHashMapFrequencyData(hashMap, chunk, letterToAnalyze);
				
				
				//update count now that you're moving on to next chunk in book text
				fileIndex++;
				
				
				
				chunk.delete(0, 1);
				chunk.append(letterToAnalyze);
				
				/**
				 * if reached the last chunk (may have appeared before in book text or not)
				 * must account for it when making seed EVEN THOUGH don't account for it when analyzing frequencies
				 * (don't have to worry about br.ready() b/c chainorder can't be ≥ f.length)
				 * SECOND CONDITION??? (if lastChunk is unique or not)
				 */
				if(fileIndex == f.length() - chainorder) {
						//System.out.println("LASTCHUNK CONDITION HIT");
					//current value (NOT obj refc) of chunk is that of the last chunk
					//BUT must still mark whether last chunk is unique or not
					for(Map.Entry<StringBuilder, GatsbyNode> entry : hashMap.entrySet()) {
						if(entry.getKey().toString().equals(chunk.toString())) {
							//last chunk is not unique
							lastChunkUnique = false;
							break;
						}
					}
						//System.out.println("lastChunk2: " + chunk);
					break outerloop;
				}
				
					//System.out.println("chunk after replacing: " + chunk);
					//System.out.println("count: " + count);
					//System.out.println("hashMap: " + hashMap);
					//System.out.println("hashMap size: " + hashMap.size());
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
			//System.out.println(hashMap);
			//System.out.println("LAST CHUNK UNIQUE: " + lastChunkUnique);
		
		System.out.println("REACHED END OF " + f.getName());
		
		
		
		//**************
		//STEP 2: choose seed & random chars to add to output
		//Used prefix idea (random number generator in arbitrary probability distribution fashion)
		//Stout said don't need to consider case where there are multiple potential seeds
		//**************
		
		//STEP 2a
		StringBuilder myContent = new StringBuilder();
		
		//HOW MUCH TIME ARE YOU REALLY SAVING USING THIS INSTEAD OF A STRING???
		//CASE 1: seed points to actual obj refc of seed (which is NOT the last chunk)
		//CASE 2 (SPECIAL): seed points to value (NOT obj refc) of lastChunk
		StringBuilder seed = hashMapDataToPrefixesAndFindSeed(hashMap, chunk, lastChunkUnique);
		
		//Don't care about obj refc or only value (last chunk special case) for this
		myContent.append(seed);
		//System.out.println("myContent w/ only seed: " + myContent);
		
		
		//STEP 2b
		//NOTE: passing value for chunk (if considering last chunk special case), NOT obj refc
		createContent(hashMap, myContent, chunk, lastChunkUnique, chainorder, numChars);
		
			//System.out.println(myContent);
			//System.out.println(myContent.length());
		
		//**************
		//STEP 3: integrate myContent w/ BufferedWriter, try catch loop, & new txt file
		//**************
		
		//REPLACE FileWriter with PrintWriter
		//Use pw.print(), pw.println(), pw.close()
		//DIFFERENCE BETWEEN THE TWO ^^^???
		
		BufferedWriter bw = null;
	    
		try {
			File file = new File(outputFileName);
			
			//This logic will make sure that the file gets created if it is not present at the specified location
			if (!file.exists()) {
				file.createNewFile();
			}

			bw = new BufferedWriter(new PrintWriter(file));
			bw.write(myContent.toString());
			
		    System.out.println("File written Successfully");
	    } catch (IOException ioe) {
	    	ioe.printStackTrace();
		}
		finally
		{ 
		   try {
		      if(bw!=null) {
		    	  bw.close();
		      }
		   } catch(Exception ex) {
		       System.out.println("Error in closing the BufferedWriter" + ex);
		   }
		}
	}

	private void inputHashMapFrequencyData(HashMap<StringBuilder, GatsbyNode> hMap, StringBuilder ch, char c) {
		/**
		 * CASE 1: if "chunk"-GatsbyNode pair has already been created
		 * (i.e. already has data for frequency of certain letter(s))
		 * iterate through hashMap & compare ch to each entry
		 * Note: must cast BOTH to String
		 * (immutable String vs. mutable StringBuilders)
		 * (mutable means compare object references instead of "value")
		 * HOW DOES EFFICIENCY COMPARE IF YOU MADE HASHMAP STORE KEYS THAT WERE STRINGS???
		 */
		for(Map.Entry<StringBuilder, GatsbyNode> entry : hMap.entrySet()) {
			if(entry.getKey().toString().equals(ch.toString())) {
					//System.out.print("Found preexisting GatsbyNode: " + entry);
				hMap.get(entry.getKey()).incrementFrequency(c);
				return;
			}
		}
	
		/**
		 * CASE 2: first time a chunk is encountered while iterating through book text
		 * then create a new chunk reference AND new node to store data (and any potential subsequent data)
		 * create the former to avoid same StringBuilder reference for every key in StrinBuilder-GatsbyNode pairs of outer "hashMap"
		 */
		//to avoid same StringBuilder reference
			//System.out.println("Making new GatsbyNode");
		StringBuilder newCh = new StringBuilder();
		newCh.append(ch);
		
		hMap.put(newCh, new GatsbyNode());
		
			//System.out.println(newCh + "'s GatsbyNode (should be blank/zeroed): " + hMap.get(newCh));
		
		//should work since you haven't changed reference for newCh
		hMap.get(newCh).incrementFrequency(c);
				
			//System.out.println("GatsbyNode's frequencies (should only have 1 k-v pair): " + hMap.get(newCh).getFrequencies() + "\n");
	}
	
	private StringBuilder hashMapDataToPrefixesAndFindSeed(HashMap<StringBuilder, GatsbyNode> hMap, StringBuilder ch, boolean lastChUnique) {
		StringBuilder seed = new StringBuilder();
		int seedAppearances = 0;
		
		for(Map.Entry<StringBuilder, GatsbyNode> entry : hMap.entrySet()) {
			//prefix process (only once per unique chunk)
			hMap.get(entry.getKey()).replaceValuesWithPrefix();
			
				//System.out.println("current potential seed: " + seed);
			
			//regular case seed condition
			if(entry.getValue().sumFrequencies > seedAppearances) {
					//System.out.println("hit normal case seed condition");
				seedAppearances = hMap.get(entry.getKey()).sumFrequencies;
				seed = entry.getKey();
					//System.out.println("seed is now: " + seed);
			}
			
			//special case seed condition: lastChunk appears multiple times in book text
			//var named "chunk", which is the last chunk, is the seed
			if(entry.getKey().toString().equals(ch.toString()) && lastChUnique == false && seedAppearances < entry.getValue().sumFrequencies + 1) {
					//System.out.println("hit special case seed condition");
				//seed = ch;???
				//NOTE this is still value, NOT obj refc
				return ch;
			}
		}
		
		//NOTE this is obj refc
		return seed;
	}
	
	private void createContent(HashMap<StringBuilder, GatsbyNode> hMap, StringBuilder content, StringBuilder lastCh, boolean lastChUnique, int cOrder, int numC) {
		int myContentIndex = 0;		
		
		//HOW TO REDUCE BIG-O FOR THIS???
		outerloop:
		while(content.length() < numC) {
				//System.out.println("(B) myContentIndex: " + myContentIndex);
			
			StringBuilder chunkToAnalyze = new StringBuilder();
			
			for(int i = 0; i < cOrder; i++) {
				//the value of myContentIndex shouldn't actually be changed
				//repetitive for first iteration
				chunkToAnalyze.append(content.charAt(myContentIndex + i));
			}
			
				//System.out.println("chunkToAnalyze: " + chunkToAnalyze);
			
			for(Map.Entry<StringBuilder, GatsbyNode> entry : hMap.entrySet()) {
				if(entry.getKey().toString().equals(chunkToAnalyze.toString())) {
						//System.out.println("hit match condition: " + entry.getKey() + " should equal " + chunkToAnalyze);
					content.append(hMap.get(entry.getKey()).chooseChar());
					
					myContentIndex++;
					
						//System.out.println("myContent: " + content);
						//System.out.println("(A1) myContentIndex: " + myContentIndex);
					
					if(content.length() == numC) {
							//System.out.println("hit break outerloop condition");
						break outerloop;
					}
					
					//once you've found the frequency that you want to "random number generator" for,
					//you're done with the current chunkToAnalyze
					break;
				}
			}
			
			if(lastChUnique == true && lastCh.toString().contentEquals(chunkToAnalyze)) {
				//assumed b/c what would you analyze after the unique last chunk
				//that doesn't have any frequencies to "random number generate" on?
					//System.out.println("unique chunk " + lastCh + " has been chosen");
				return;
			}
		}
		
	}
}
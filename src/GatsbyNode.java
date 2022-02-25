import java.util.HashMap;
import java.util.Map;

public class GatsbyNode {
	HashMap<Character, Integer> frequencies;
	int sumFrequencies;
	
	public GatsbyNode() {
		this.frequencies = new HashMap<Character, Integer>();
		this.sumFrequencies = 0;
	}
	
	public void replaceValuesWithPrefix() {
		int count = 0;
		
		for(Map.Entry<Character, Integer> entry : frequencies.entrySet())
		{
			//this process is repetitive for the first entry since it's value will just be replaced to what it was before
			//MORE EFFICIENT TO SOMEWHOW SKIP FIRST ENTRY AND LEAVE ITS INTEGER VALUE UNTOUCHED???
			count += entry.getValue();
			
			frequencies.replace(entry.getKey(), count);
		}
		
		sumFrequencies = count;
		
			//System.out.println("(A) sumFrequencies: " + sumFrequencies);
	}
	
	public char chooseChar() {
		//generate random number w/ value from 0 to sumFrequencies
		double rand = Math.random() * sumFrequencies;
		
		//choose next char based on probability
		//\u0000 is null char literal
		char nextChar = '\u0000';
		
		for (Map.Entry<Character, Integer> entry : frequencies.entrySet())
		{
			if (rand <= entry.getValue())
		    {
		        nextChar = entry.getKey();
		        return nextChar;
		    }
		}
		
		//should return null if method doesn't work
		return nextChar;
	}
	
	public void incrementFrequency(char key) {
		for(Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
			//System.out.println("\nRESULT OF COMPARISON: " + entry.getKey().equals(key) + "\n");
			
			if(entry.getKey() == key) {
				this.frequencies.replace(entry.getKey(), entry.getValue() + 1);
					//System.out.println("\t\tchanged frequency for " + key + ": " + this.frequencies.get(entry.getKey()) + "\n");
				return;
			}
		}
		
			//System.out.println("\t\tnew frequency for: " + key);
		this.frequencies.put(key, 1);
	}
	
	public HashMap<Character, Integer> getFrequencies() {
		//return this.frequencies.entrySet();
		return this.frequencies;
	}
	
	/*
	public String toString() {
		//return "" + this.frequencies.entrySet();
		return "sumFrequencies: " + this.sumFrequencies + "\t" +this.frequencies.entrySet();
	}
	*/
	
}

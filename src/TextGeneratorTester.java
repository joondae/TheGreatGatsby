public class TextGeneratorTester {
	public static void main(String [] args) {
		final long startTime = System.currentTimeMillis();
		
		/*
		//For my simple tests
		TextGenerator tg = new TextGenerator("TestText.txt");
		tg.generateText(10, "greatgatsby2.txt", 303);
		*/
		
		
		//For my more complex tests
		TextGenerator tg = new TextGenerator("thegreatgatsby.txt");
		tg.generateText(3, "greatgatsby2.txt", 1000);
		
		
		//Stout's test
		/*
		TextGenerator tg = new TextGenerator("thegreatgatsby.txt");
		tg.generateText(2, "greatgatsby2.txt", 1000);
		*/
		
		final long endTime = System.currentTimeMillis();
		
		System.out.println("Execution time: " + (endTime - startTime));
	}
}

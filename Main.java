import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main{
    public static void main(String[] args) throws FileNotFoundException {
        long start = System.currentTimeMillis();

        // Reading the find_words.txt file
        ArrayList<String> findWords = new ArrayList<>();
        FileReader fr =new FileReader("find_words.txt");
        BufferedReader wordsTxt = new BufferedReader(fr);
        try (wordsTxt) {
            String line;
            while ((line = wordsTxt.readLine()) != null) {
                findWords.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Reading the find_words.txt file
        StringBuilder textString = new StringBuilder();
        try (BufferedReader shakespeareText = new BufferedReader(new FileReader("t8.shakespeare.txt"))) {
            String line;
            while ((line = shakespeareText.readLine()) != null) {
                textString.append(line.toLowerCase()).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Reading the dictionary.csv file as a dictionary
        Map<String, String> dict = new HashMap<>();
        try (BufferedReader data = new BufferedReader(new FileReader("french_dictionary.csv"))) {
            String line;
            while ((line = data.readLine()) != null) {
                String[] rows = line.split(",");
                if (rows.length >= 2) {
                    dict.put(rows[0], rows[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Creating t8.shakespeare.translated.txt.txt (output)
        String testStr = textString.toString();
        StringBuilder res = new StringBuilder();
        String[] temp = testStr.split(" ");
        for (String word : temp) {
            res.append(dict.getOrDefault(word, word)).append(" ");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("t8.shakespeare.translated.txt.txt"))) {
            writer.write(res.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }



        //unique lists of words
        String unique = "find_words.txt";
        List<String> uniqueWordList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(unique))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    uniqueWordList.add(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> uniqueWords = new HashSet<>(uniqueWordList);

        for (String word : uniqueWords) {
            try(BufferedWriter uniqlist = new BufferedWriter(new FileWriter("Unique.txt",true))){
                uniqlist.write(word);
                uniqlist.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        // Creating performance.txt & storing the time and memory taken
        long timeTaken = System.currentTimeMillis() - start;
        double memoryTaken = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("performance.txt"))) {
            writer.write("Time to process: " + timeTaken + " milliseconds");
            writer.newLine();
            writer.write("Memory used: " + memoryTaken + " bytes");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //replaced words stores in frequency.txt
        String translatedFile = "t8.shakespeare.translated.txt.txt";
        String englishFrenchFile = "french_dictionary.csv";

        Map<String, String> frenchEnglishMap = new HashMap<>();
        Map<String, Integer> frequencyMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(englishFrenchFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(",");
                if (words.length == 2) {
                    String englishWord = words[0].trim().toLowerCase();
                    String frenchWord = words[1].trim().toLowerCase();

                    frenchEnglishMap.put(frenchWord, englishWord);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(translatedFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
                    String englishWord = frenchEnglishMap.getOrDefault(word, "");
                    if (!englishWord.isEmpty()) {
                        frequencyMap.put(englishWord, frequencyMap.getOrDefault(englishWord, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print English word, French word, and Frequency together
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            String englishWord = entry.getKey();
            int frequency = entry.getValue();
            String frenchWord = "";
            for (Map.Entry<String, String> frenchEnglishEntry : frenchEnglishMap.entrySet()) {
                if (englishWord.equalsIgnoreCase(frenchEnglishEntry.getValue())) {
                    frenchWord = frenchEnglishEntry.getKey();
                    break;
                }
            }
            String freq = "frequency.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(freq, true))) {
                writer.write(englishWord + ","+frenchWord+"," + frequency);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}


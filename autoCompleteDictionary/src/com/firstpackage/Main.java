package com.firstpackage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

// Trie Node, which stores a character and the children in a HashMap
class TrieNode {
    private final char value;
    private final HashMap<Character, TrieNode> children;
    private boolean lastNode;

    public TrieNode(char ch) {
        value = ch;
        children = new HashMap<>();
        lastNode = false;
    }

    public HashMap<Character, TrieNode> getChildren() {
        return children;
    }

    public void setIsEnd(boolean val) {
        lastNode = val;
    }

    public boolean isEnd() {
        return lastNode;
    }
}

// Implements the actual Trie
class Trie {
    // an arraylist for storing words in search history for 7 days.
    public List<String> history = new ArrayList<String>();
    Map<String, List<String>> hm = new HashMap<String, List<String>>();
    private final TrieNode root;

    // Constructor
    public Trie() {
        root = new TrieNode((char) 0);
    }

    void setDict(Scanner scanner) {
        //read dictionary from file and add it to HashMap
        scanner.nextLine(); // skip header
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            // since there are some commas in the word definition, we use regex to tokenize the fields.
            String[] splitted = line.split(",(?=(?:[^\"]*\"[^\"]*\")*(?![^\"]*\"))");
            String word = splitted[0];
            String translation = splitted[1];
            String type = splitted[2];
            String meaning = splitted[3];

            List<String> values = new ArrayList<String>();
            values.add(translation);
            values.add(type);
            values.add(meaning);
            hm.put(word, values);
        }
    }

    void printValues(String key) {
        List<String> value = hm.get(key);

        System.out.println("\nKey: " + key);
        System.out.println("French Translation: " + value.get(0));
        System.out.println("Type: " + value.get(1));
        System.out.println("Meaning: " + value.get(2));
        System.out.println("--------------------------------------------------------");
    }

    void insertWords(Scanner scanner) {
        //add songs from ASCII file to Array
        scanner.nextLine();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] splitted = line.split(",", 4);
            String word = splitted[0];
            insert(word);
        }
    }

    // Method to insert a new word to Trie
    private void insert(String word) {

        // Find length of the given word
        int length = word.length();
        TrieNode currentNode = root;

        // Traverse through all characters of given word
        for (int level = 0; level < length; level++) {
            HashMap<Character, TrieNode> child = currentNode.getChildren();
            char ch = word.charAt(level);

            // If there is already a child for current character of given word
            if (child.containsKey(ch))
                currentNode = child.get(ch);
            else   // Else create a child
            {
                TrieNode temp = new TrieNode(ch);
                child.put(ch, temp);
                currentNode = temp;
            }
        }
        // Set IsEnd true for last character
        currentNode.setIsEnd(true);
    }

    // Show search history from latest to oldest search.History search will only show the existing in the dictionary.
    void showHistory() {
        if (history.size() > 0) {
            Collections.reverse(history);
            for (String item : history) {
                System.out.println(item);
            }
            Collections.reverse(history);
        } else
            System.out.println("You have not enter any word that existing in the dictionary/There is no search history yet...\n");
    }


    // The main method that finds out the longest string 'input'
    private String getMatchingPrefix(String input) {
        String result = ""; // Initialize resultant string
        int length = input.length();  // Find length of the input string

        // Initialize reference to traverse through Trie
        TrieNode currentNode = root;

        // Iterate through all characters of input string 'str' and traverse down the Trie
        int level, prevMatch = 0;
        for (level = 0; level < length; level++) {
            // Find current character of str
            char ch = input.charAt(level);

            // HashMap of current Trie node to traverse down
            HashMap<Character, TrieNode> child = currentNode.getChildren();
            if (child.containsKey(ch)) {
                result += ch;          //Update result
                currentNode = child.get(ch); //Update crawl to move down in Trie
                // If this is end of a word, then update prevMatch
                if (currentNode.isEnd())
                    prevMatch = level + 1;

            } else break;
        }

        // If the last processed character did not match end of a word,
        // return the previously matching prefix
        if (currentNode.isEnd()) {
            return result.substring(0, prevMatch);
        } else {
            return result;
        }
    }

    public List<String> autoComplete(String input) {

        List<String> autoCompWords = new ArrayList<String>();
        // Get prefix
        String prefix = getMatchingPrefix(input);
        //No matching words if the length of prefix is 0
        if (prefix.length() == 0) {
            System.out.println(input + " is not found.");
            return autoCompWords;
        }
        TrieNode currentNode = root;
        // get prefix by traversing all characters
        for (int i = 0; i < prefix.length(); i++) {
            HashMap<Character, TrieNode> child = currentNode.getChildren();
            currentNode = child.get(prefix.charAt(i));
            if (currentNode == null)
                return autoCompWords;
        }
        getPrefix(currentNode, autoCompWords, prefix);
        String simWord = autoCompWords.get(0);
        //if search word is found, add it to dictionary and print the translation.
        // If not found, shows similar words and translation.
        if (autoCompWords.size() == 1 || simWord.equals(input)) {
            printValues(simWord);
            history.add(input);
        } else {
            //Print number of similar words and ask if user wants to see the translation of the similar words
            System.out.println("\n" + input + " has " + autoCompWords.size() + " similar words.");
            System.out.println("Similar words are: " + autoCompWords + "\n");
            System.out.println("Would you like to see the translation of the similar words? \nEnter 1=yes or 2=no");
            Scanner in = new Scanner(System.in);
            String option2 = in.nextLine();
            try{
                if (Integer.parseInt(option2) == 1) {
                    for (String key : autoCompWords) {
                        printValues(key);
                    }
                }
            }
            catch(Exception e) {
                System.out.println("You must enter an integer value");
            }
        }
        return autoCompWords;
    }

    // search for prefix of the input words in Trie
    private void getPrefix(TrieNode currentNode, List<String> autoCompWords, String word) {
        if (currentNode == null) return;
        if (currentNode.isEnd()) {
            autoCompWords.add(word);
        }
        // Traverse through all characters of given word
        HashMap<Character, TrieNode> map = currentNode.getChildren();
        for (Character c : map.keySet()) {
            getPrefix(map.get(c), autoCompWords, word + c);
        }
    }
}

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        String requests = args[0];
        // read dictionary from files
        Scanner scanner = new Scanner(new FileInputStream(requests));
        Scanner scanner2 = new Scanner(new FileInputStream(requests));
        Trie dict = new Trie();

        // read files and insert each word into a Trie
        dict.setDict(scanner);
        dict.insertWords(scanner2);
        Scanner in = new Scanner(System.in);
        boolean cont = true;

        while (cont) {
            System.out.println("Please enter 1=shows search history, 2=search word and translation or 3=terminate.");
            String option = in.nextLine();
            try {
                //If user enter 1, show search history
                if (Integer.parseInt(option) == 1) {
                    dict.showHistory();
                    // If user enter 2, search the word and shows its translation
                } else if (Integer.parseInt(option) == 2) {
                    System.out.println("Enter english word... ");
                    String word = in.nextLine();
                    dict.autoComplete(word);

                    // If user enter 3 or others, break the loop and terminate
                } else cont = false;
            }
            catch(Exception e) { //Catch when user does not enter a choice of integer.
                System.out.println("You must enter an integer value");
            }
        }
    }
}

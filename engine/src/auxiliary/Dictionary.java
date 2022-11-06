package auxiliary;

import javafx.collections.ObservableList;
import jaxb.generated.CTEDictionary;

import java.util.*;

public class Dictionary {
    HashSet<String> words;
    Alphabet alphabet;
    Trie trie;

     final int ABC_SIZE;

    public Dictionary(CTEDictionary cteDictionary, Alphabet abc){
        words = new HashSet<>();
        this.alphabet = abc;
        String exclude = cteDictionary.getExcludeChars();
        String text = cteDictionary.getWords();
        String regex = "[" +exclude +"]";

        text = text.replaceAll(regex,"");


        for(String word :  text.split("\\s+")){
            word = word.replaceAll("\\s+", "");
            words.add(word);
        }
        ABC_SIZE = alphabet.size();

        // Creating Trie for Word Completions
        trie = new Trie(abc);
        for (String word : words) {
            trie.addWord(word);
        }


    }
    // FOR TESTING
    public Dictionary(String text, String exclude, Alphabet abc){
        words = new HashSet<>();
        for (char c : exclude.toCharArray()) {
            text = text.replace(c,' ');
        }
        this.alphabet = abc;

        words.addAll(Arrays.asList(text.toUpperCase().split("\\s+")));
        ABC_SIZE = abc.size();


        // Creating Trie for Word Completions
        trie = new Trie(abc);
        for (String word : words) {
            trie.addWord(word);
        }
    }


    public boolean contains(String word){
       return words.contains(word);
    }

    public boolean checkCorrectness(String processed) {

        for (String word : processed.split(" ")) {
            String upperCaseWord = word.toUpperCase();
            String lowerCaseWord = word.toLowerCase();
            if (!words.contains(lowerCaseWord) && !words.contains(upperCaseWord)) {
                return false;
            }
        }
        return true;
    }

    public List<String> getWordsList() {
        List<String> result = new ArrayList<>(this.words);
        Collections.sort(result);
        return result;
    }


    class Trie{
         // Nice Try!
        private Node root;
        private Alphabet abc;


        class Node{
            Node [] children;
            boolean isTerminal;

            String value;

            public Node(){
                this(false);
                value = "";
            }
            public Node(boolean terminal){
                children = new Node[ABC_SIZE];
                for (Node n : children) {
                    n = null;
                }
                this.isTerminal = terminal;
            }

        }

        public Trie(Alphabet abc){
            this.root = new Node(); this.abc = abc;
        }


        public void addWord(String word) {
            int level;
            int length = word.length();
            int index;

           Node pCrawl = root;
           for (level = 0; level < length; level++)
            {
                try{
                index = abc.getOrder(word.charAt(level));
                if (pCrawl.children[index] == null) {
                    pCrawl.children[index] = new Node();
                }

                pCrawl = pCrawl.children[index];}catch(ArrayIndexOutOfBoundsException e){
                    System.out.println("error char is " + word.charAt(level));
                    throw e;
                }

            }

            // mark last node as leaf
            pCrawl.isTerminal = true;
            pCrawl.value = word;
        }


        public List<String> getWordsFromPrefix(List<Integer> path){
            List<String> words = new ArrayList<>();
            int maxDepth = 4;
            Node prefix = this.getNode(path);
            if(prefix == null){return null;}
            return BFS(prefix);

        }

        private Node getNode(List<Integer> path) {
            Node pCrawl = this.root;

            for (int nextNode : path) {
                pCrawl = pCrawl.children[nextNode];
                if(pCrawl == null){
                    return null;
                }
            }

            return pCrawl;

        }

        private List<String> BFS(Node root){
            List<Node> notVisited = new ArrayList<>();
            notVisited.add(root);
            List<String> wordsWtPrefix = new ArrayList<>();

            int nodeLevelCount = 1;
            while(!notVisited.isEmpty()){
                Node node = notVisited.remove(0);
                nodeLevelCount--;
                for (Node n : node.children) {
                    if(n != null){
                        notVisited.add(n);
                        if(n.isTerminal) {
                            wordsWtPrefix.add(n.value);
                        }
                    }
                }
                if(nodeLevelCount == 0){
                    nodeLevelCount = notVisited.size();
                }
            }
            return wordsWtPrefix;
        }

        private List<String> getSuggestionsHelper(Node root, List<String> words, int depth){


            if(root.isTerminal){
                words.add(root.value);
            }

            if(words.size() >= 5 || depth == 0){
                return words;
            }

            for(Node node : root.children){

                if(words.size() >= 5){
                    return words;
                }
                if(node == null){
                    continue;
                }
                words.addAll(getSuggestionsHelper(node,words,depth-1));
            }
            return words;
        }



    }





    public List<String> getWordSuggestions(String prefix){
        List<Integer> path = new ArrayList<>();
        for(char c : prefix.toCharArray()){
            path.add(alphabet.getOrder(c));
        }
        return trie.getWordsFromPrefix(path);
    }



}

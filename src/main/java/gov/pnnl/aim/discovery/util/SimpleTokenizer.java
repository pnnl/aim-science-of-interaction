package gov.pnnl.aim.discovery.util;

import gov.pnnl.aim.discovery.pojo.Substring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** 
 * Utility class for tokenizing a content String, and finding (tokenized) 
 * phrases in the (tokenized) result.
 * 
 * @author Grant Nakamura, December 2014 
 */
public class SimpleTokenizer {
  
  private SimpleTokenizer() {  
  }
  
  /** Gets the content from a text file. */
  public static String readContent(File f) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(f));
    String line;
    
    StringBuilder builder = new StringBuilder();
    
    while ((line = in.readLine()) != null) {
      builder.append(line);
      builder.append('\n');
    }
    in.close();
    
    String content = builder.toString();
    return content;
  }


  /**
   * Tokenizes a string.
   * 
   * <p> Performs a simple tokenization:
   * <ol> 
   * <li>Whitespace blocks are the word boundaries</li>
   * <li>At the start and end of words, punctuation blocks are tokens</li>
   * <li>Within words, remove punctuation</li>
   * <li>Remaining words are tokens</li>
   * </p>
   * 
   * @return List of tokens (containing text + position)
   */
  public static List<Substring> tokenize(String content) {
    List<Substring> finalTokenList = new ArrayList<Substring>();
    
    // Separate on whitespace
    List<Substring> intermediateTokenList = splitOnWhitespace(content);
    
    // Get pattern for separating token into (punctuation)(word)(punctuation)
    String PUNCT = "[\\p{Punct}]";
    String regex = String.format("(%s*)(\\S*?)(%s*)", PUNCT, PUNCT);
    Pattern pattern = Pattern.compile(regex);
    
    for (Substring token : intermediateTokenList) {
      String text = token.getText();
      Matcher matcher = pattern.matcher(text);
      if (matcher.matches()) {
        String frontPunc = matcher.group(1); 
        String wordWithPunc = matcher.group(2); 
        String backPunc = matcher.group(3); 
        
        int start = token.getIndex();
        
        // Remove interior punctuation and normalize case
        String word = wordWithPunc.replaceAll(PUNCT, "").toLowerCase();
        
        if (!frontPunc.isEmpty()) {
          // Save token for front punctuation
          Substring frontToken = new Substring(frontPunc, start);
          finalTokenList.add(frontToken);
          start += frontPunc.length();
        }
        
        if (!word.isEmpty()) {
          // Save token for word
          Substring wordToken = new Substring(word, start);
          finalTokenList.add(wordToken);
          start += wordWithPunc.length();
        }
        
        if (!backPunc.isEmpty()) {
          // Save token for back punctuation
          Substring backToken = new Substring(backPunc, start);
          finalTokenList.add(backToken);
        }
      }
    }
    
    return finalTokenList;
  }


  /**
   * Tokenizes a list of Strings. 
   * 
   * @return A List of tokens (Substrings) for each String
   */
  public static List<List<Substring>> tokenizeStrings(List<String> strList) {
    // Allocate
    int count = strList.size();
    List<List<Substring>> tokenListPerString = new ArrayList<List<Substring>>(count);
    
    // Tokenize
    for (String str : strList) {
      List<Substring> tokens = tokenize(str);
      tokenListPerString.add(tokens);
    }
    
    return tokenListPerString;
  }

  /** 
   * Splits a String on whitespace into substrings, where each substring
   * records the text and the original position.
   */
  private static List<Substring> splitOnWhitespace(String content) {
    List<Substring> splitList = new ArrayList<Substring>();
    
    int length = content.length();
    int i = 0;
    while (i < length) {
      // Find start of non-whitespace
      while (i < length  &&  Character.isWhitespace(content.charAt(i))) {
        i++;
      };
      int start = i;
      
      // Find end of non-whitespace
      while (i < length  &&  !Character.isWhitespace(content.charAt(i))) {
        i++;
      };
      String text = content.substring(start, i); 
      
      // Record a new substring
      Substring block = new Substring(text, start);
      splitList.add(block);
    }
    
    return splitList;
  }

  /** 
   * Searches content for phrase strings. The matching is token-based. That
   * is, both the content and highlights are tokenized using the same rules,
   * prior to matching.  
   * 
   * @param content  The original text to search
   * @param phrases  Phrases to look for
   * 
   * @return List of matches
   */
   public static List<Substring> findPhrases(String content, List<String> phrases) {
    
    // Tokenize the original text
    List<Substring> contentTokens = tokenize(content);
    int contentCount = contentTokens.size();
        
    // Tokenize the phrases
    List<List<Substring>> tokenizedPhrases = tokenizeStrings(phrases);
    int phraseCount = phrases.size();
    
    
    // We'll just use a naive brute force search for now. This should suffice, 
    // since we won't have a lot of partial matches.
    
    List<Substring> matches = new ArrayList<Substring>();

    // For each starting position
    for (int c = 0; c < contentCount; c++) {
      // Get the sublist there
      List<Substring> tokenSublist = contentTokens.subList(c, contentCount);
      
      // For each phrase
      for (int p = 0; p < phraseCount; p++) {
        List<Substring> highlightTokens = tokenizedPhrases.get(p);
        
        // Check its tokens against the current sublist
        Substring match = getMatchIfAny(content, tokenSublist, highlightTokens);
        if (match != null) {
          matches.add(match);
        }
      }
    }
    
    return matches;
  }
  
   
  /**
   * Filters the match list to remove overlapping hits.
   * 
   * <p> It currently favors earlier matches over later matches (regardless of
   * length). For multiple matches at a single position, it favors the longest
   * match.
   * 
   * @param matches  Matches, assumed to be in non-decreasing index order
   */
   public static List<Substring> filterOverlaps(List<Substring> matches) {

     // Allocate the result list
     int matchCount = matches.size();
     List<Substring> retain = new ArrayList<Substring>(matchCount);
     
     int i = 0;
     while (i < matchCount) {
       // Get the first match at a position
       Substring best = matches.get(i);
       
       // For each other match at that position
       i++;
       while (i < matchCount) {
         Substring match = matches.get(i);
         
         if (match.getIndex() > best.getIndex()) {
           // Not at same position 
           break;
         }
         
         if (match.getEndIndex() > best.getEndIndex()) {
           // Found a longer match
           best = match;
         }
         
         i++;
       }
       
       // Keep the best match at the position
       retain.add(best);
       
       // Skip any matches overlapping it
       while (i < matchCount) {
         Substring match = matches.get(i);
         
         if (match.getIndex() >= best.getEndIndex()) {
           // Not an overlap
           break;
         }
         
         i++;
       }
     }
     
     return retain;
   }

  /** 
   * Gets the match, if any, for the given prefix.
   *
   * @param content  The entire original content string
   * @param tokens   The tokens to check for the prefix
   * @param prefix   The prefix we're seeking
   * 
   * @return Matching substring from the content (or null if not a match)
   */
  private static Substring getMatchIfAny(String content, List<Substring> tokens, List<Substring> prefix) {
    Substring match = null;
    
    if (tokensStartWith(tokens, prefix)) {
      
      // It's a match; get the original position
      int start = tokens.get(0).getIndex();
      int end = tokens.get(prefix.size() - 1).getEndIndex();

      // Make a new substring from the original text
      String matchingText = content.substring(start, end);
      match = new Substring(matchingText, start);
    }
    
    return match;
  }
  
  /** Checks a token list to see if it starts with the prefix tokens. */
  private static boolean tokensStartWith(List<Substring> tokens, List<Substring> prefix) {
    int tokenCount = tokens.size();
    int prefixSize = prefix.size();
    
    if (tokenCount < prefixSize) {
      // There aren't enough tokens to contain the prefix
      return false;
    }
    
    // Match tokens one at a time
    for (int i = 0; i < prefixSize; i++) {
      String token = tokens.get(i).getText();
      String prefixToken = prefix.get(i).getText();

      if (!token.equals(prefixToken)) {
        // Found a mismatch
        return false;
      }
    }
    
    // Everything matched
    return true;
  }
  
  
  /**
   * Counts frequency of each phrase size (number of words in a token).
   * 
   * @return Map of phrase size (in number of words) to frequency 
   */
  public static SortedMap<Integer, Integer> countPhraseSizeFrequency(List<Substring> tokens) {
    SortedMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
    
    // For each token
    for (Substring token : tokens) {
      String text = token.getText();
      
      // Get the phrase size
      String[] split = text.split("\\s+");
      int phraseSize = split.length;
      
      // Increment the frequency of that size
      Integer fObj = map.get(phraseSize);
      int f = (fObj == null)? 0 : fObj;
      map.put(phraseSize, f + 1);
    }
    
    return map;
  }

  
  // Test driver
  public static void main(String[] args) throws IOException {
    
    List<String> phrases = Arrays.asList(new String[] {
      "coast", "death", "demonstrators", "died", "elian", "elian karel", 
      "juliana", "karel", "money", "obtain", "protests", "water", "centrum", 
      "company", "curve", "fluss", "gained", "gas", "hank", "million", 
      "sanjorge", "sold", "sr", "tiskele", "department", "fire", "aid", "car", 
      "driver", "elodis", "force", "hospital", "koppolis", "person", "police", 
      "police force", "road", "servants", "team", "truck", "assembly", "city", 
      "family", "fields", "leader", "marek", "pok", "program", "vote", 
      "exploration", "industry", "possession", "araullo", "foreign", 
      "incentives", "tax", "health", "kapelou", "letters", "minister", "threat", 
      "threats", "vincent", "vincent kapelou", "bend", "tiskele bend", 
      "declaration", "kidnapping", "avila", "body", "interrogated", 
      "investigation", "speculation", "capitol", "increase", "community", 
      "hyper", "incidence", "nespola", "ratio", "substrate", "technique", 
      "construction", "acidic", "contamination", "developed", 
      "hyper acidic substrate removal", "removal", "bending", "bending tiskele", 
      "port", "research", "taxes", "van", "arrest", "protesters", "development", 
      "oil", "agriculture", "bodrogi", "difficult", "jeroen", "leaders", "mob", 
      "earthquake", "funds", "global", "passengers", "aforesaid", "arrested", 
      "facility", "district", "siopa", "chemical", "controlled", "costs", 
      "cotton", "designer", "drug", "drugs", "medical", "substance", "accused", 
      "attack", "cities", "energy", "ii", "income", "jets", "principal", 
      "started", "violence", "protestors", "tiskele bend fields", "traffic", 
      "vehicle", "window", "crowd", "officers", "rally", "riot", "presidential", 
      "gas development", "margin", "measure", "elected", "registered", 
      "registered offices", "individuals", "missing", "concerning", "gathering", 
      "protestateurs", "equipment", "fourteen employees", "collection", 
      "hoofdkwartier", "east", "sea", "task", "minor", "cheap", "decision", 
      "families", "generations", "hank fluss", "osvaldo", "reese", "question", 
      "kraft", "loads", "charges", "death of karel", "cell", "related", "apa", 
      "feared", "ransom", "hard", "disclosed", "fall", "julian", "explosion", 
      "assistance", "accident", "edris", "school", "disposal", "fear", "tell", 
      "controversial", "bad", "plane", "mdmc", "poisoning", "wfa", "door", 
      "election", "millions", "drivers"
    });
    
    // Some of the more interesting IDs in terms of phrase size
    int[] ids = {111, 153, 158, 175, 179, 188, 190, 249, 254, 257, 273, 274, 
        297, 314, 334, 423, 523, 566, 650, 658, 709, 781, 799};
    
    for (int id : ids) {
      String filename = id + ".txt";
      File f = new File(filename);
      String content = SimpleTokenizer.readContent(f);
      List<Substring> contentTokens = SimpleTokenizer.tokenize(content);
      List<Substring> highlightHits = SimpleTokenizer.findPhrases(content, phrases);
      List<Substring> filteredHits = SimpleTokenizer.filterOverlaps(highlightHits);

      String highlighted = Highlighter.highlight(content, phrases);
      
      SortedMap<Integer, Integer> phraseSizeMap = SimpleTokenizer.countPhraseSizeFrequency(filteredHits);

      System.err.println(filename);
      System.err.println();
      System.err.println(content);
      System.err.println("----------------------------------------");
      System.err.println(highlighted);
      System.err.println("----------------------------------------");
      System.err.println();
      System.err.println(contentTokens);
      System.err.println(highlightHits);
      System.err.println(filteredHits);
      System.err.println(phraseSizeMap);
      System.err.println("========================================");
    }
  }

}

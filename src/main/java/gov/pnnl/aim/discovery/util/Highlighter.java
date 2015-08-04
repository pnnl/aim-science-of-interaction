package gov.pnnl.aim.discovery.util;

import gov.pnnl.aim.discovery.pojo.DocumentHighlight;
import gov.pnnl.aim.discovery.pojo.Substring;

import java.util.List;


/**
 * Utility class for finding the highlights in content and wrapping them
 * in <span> tags.
 *
 * @author Grant Nakamura, December 2014
 */
public class Highlighter {

  private Highlighter() {
  }

  /** Finds phrases in content and wraps them in <span> tags. */
  public static String highlight(final String content, final List<String> phrases) {
    // Find the phrases and get rid of any overlap
    List<Substring> phraseHits = SimpleTokenizer.findPhrases(content, phrases);
    List<Substring> filtered = SimpleTokenizer.filterOverlaps(phraseHits);

    // Prepare to build a revised version of the content
    StringBuilder highlighted = new StringBuilder();
    int previousEnd = 0;
    String highlightClassName = "feature";
    String startDelimiter = String.format("<span class=\"%s\">", highlightClassName);
    String endDelimiter = "</span>";

    // For each hit
    for (Substring hit : filtered) {
      String text = hit.getText();
      int start = hit.getIndex();
      int end = hit.getEndIndex();

      // Copy any content between the previous hit and this one
      String beforePhrase = content.substring(previousEnd, start);
      highlighted.append(beforePhrase);

      // Wrap the hit
      highlighted.append(startDelimiter);
      highlighted.append(text);
      highlighted.append(endDelimiter);

      previousEnd = end;
    }

    // Copy any content after the last hit
    String remainder = content.substring(previousEnd);
    highlighted.append(remainder);

    return highlighted.toString();
  }

  /** Finds phrases in content and wraps them in <span> tags. */
  public static String highlightHighlights(final String content, final List<DocumentHighlight> highlights) {
    // Prepare to build a revised version of the content
    StringBuilder highlighted = new StringBuilder();
    int previousEnd = 0;
    String highlightClassName = "annotated";
    String startDelimiter = String.format("<span class=\"%s\">", highlightClassName);
    String endDelimiter = "</span>";

    // For each hit
    for (DocumentHighlight a : highlights) {
      String text = a.getText();
      int start = a.getStart();
      int end = start + text.length();

      // Copy any content between the previous hit and this one
      String beforePhrase = content.substring(previousEnd, start);
      highlighted.append(beforePhrase);

      // Wrap the hit
      highlighted.append(startDelimiter);
      highlighted.append(text);
      highlighted.append(endDelimiter);

      previousEnd = end;
    }

    // Copy any content after the last hit
    String remainder = content.substring(previousEnd);
    highlighted.append(remainder);

    return highlighted.toString();
  }
}

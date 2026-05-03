package com.zeter.meme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Library {
   public enum FileType {
      IMAGE, VIDEO, ANIMATED, OTHER
   }

   public enum SortType {
      ID, ALPHABETICAL
   }

   /**
    * Takes a List of Strings and turns it into a string of the items, separated by
    * spaces. If the List is null, "" will be returned.
    * 
    * @param list
    * @return List items, separated by spaces
    */
   public static String listToString(List<String> list) {
      if (list == null) {
         return "";
      }
      return Arrays.toString(list.toArray()).replaceAll("[\\[,\\]]", "");
   }

   /**
    * Takes a string and turns it into a List. Each item should be separated by
    * spaces. If the string is empty, an empty List will be returned. If there 
    * is more than one space in a row, it is treated as one.
    * 
    * @param string - input string
    * @return - List of Strings
    */
   public static List<String> stringToList(String string) {
      if (string.length() == 0) {
         return new ArrayList<String>();
      }

      ArrayList<String> output = new ArrayList<>();
      String[] items = string.trim().split(" +");
      for (int i = 0; i < items.length; i++) {
         output.add(items[i]);
      }
      return output;
   }
}

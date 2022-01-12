import java.util.Collections;
import java.util.Set;

import software.leonov.common.collect.LRUCache;

public class TestLRU {

    public static void main(String[] args) {
//        LRUCache<String, Boolean> m = LRUCache.create(5);
//        Set<String> s = Collections.newSetFromMap(m);
//        
////        m.put("one", true);
////        m.put("two", true);
////        m.put("three", true);
////        m.put("four", true);
////        m.put("five", true);
////        
////        System.out.println(m.keySet());
////        
////        m.containsKey("one");
////        //m.get("one");
////        m.keySet().contains("one");
////        m.put("six", true);
////        
////       System.out.println(m.keySet());
//        
//        s.add("one");
//        s.add("two");
//        s.add("three");
//        s.add("four");
//        s.add("five");
//        
//        s.contains("one");
//        
//        s.add("six");
//        
//        System.out.println(s);
        Set<String> s = Collections.newSetFromMap(LRUCache.create(5));
      s.add("one");
      s.add("two");
      s.add("three");
      s.add("four");
      s.add("five");
      
      System.out.println(s);
      
      s.add("one");
      s.add("six");
      
      System.out.println(s);
        
    }
    
    

}

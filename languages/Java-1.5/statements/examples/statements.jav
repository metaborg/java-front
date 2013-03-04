public class Statements {
  public void test() {
    int a;
    int a = 1;
    final int a = 1;
    
    if(true)
      return true;
    else
      return false;
      
    if(true)
      return true;
      
    while(true) {
      return true;
    }
    
    do 
      return true;
    while(true);
    
    for(int i = 1; i < 10; ++i) {
    	return i;
    }
    
    for(1; i < 10; ++i) {
      return i;
    }
    
    for(int i : list) {
    	return i;
    }
    
    label : {
    	return "stuff";
    }
    
    continue label;
    break label;
    
    synchronized(true) {
    	int a;
    	return 1;
    }
    
    try {
    	throw 1;
    } finally {
    	return 1;
    }
    
    try {
      throw 1;
    } catch(int a) {
    	return a; 
    } catch(int b) {
      return b;
    }
    
    try {
      throw 1;
    } catch(int a) {
      
    } catch(int b) {
      
    } finally {
    	
    }
    
    switch(1) {
    	case 1:
    		return 3;
      case 2: {
      	return 2;
    	}
      default:
      	return 1;
    }
  }
}
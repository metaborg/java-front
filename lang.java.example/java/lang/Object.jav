package java.lang;


public class Object {
//public final native Class<?> getClass( ) ;
  public native int hashCode( ) ;
  public boolean equals(Object obj) {
    throw new java . lang . RuntimeException ("Implementation stripped");
  }
  public String toString( ) {
    throw new java . lang . RuntimeException ("Implementation stripped");
  }
  public final native void notify( ) ;
  public final native void notifyAll( ) ;
  public final native void wait(long timeout) throws InterruptedException ;
  public final void wait(long timeout, int nanos) throws InterruptedException {
    throw new java . lang . RuntimeException ("Implementation stripped");
  }
  public final void wait( ) throws InterruptedException {
    throw new java . lang . RuntimeException ("Implementation stripped");
  }
}

package DesignPatterns;

public interface OrderSubject 
{
    void attach(OrderObserver observer);
    void detach(OrderObserver observer);
    void notifyObservers();
}

package synchronizacjawatkow;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SynchronizacjaWatkow 
{
    public static void main(String[] args) 
    {
        Skrzynka skrzynka = new Skrzynka();
        
        Lock lock = new ReentrantLock();
        Condition oczekiwanie = lock.newCondition();
        
        MaszynaProdukujacaButelki maszyna1 = new MaszynaProdukujacaButelki(skrzynka, lock, oczekiwanie);
        MaszynaZmieniającaSkrzynki maszyna2 = new MaszynaZmieniającaSkrzynki(skrzynka, lock, oczekiwanie);
        
        Thread produkcja = new Thread(maszyna1, "Producent");
        Thread zmieniacz = new Thread(maszyna2, "Zmieniacz");
        
        produkcja.start();
        zmieniacz.start();
    }   
}

class MaszynaProdukujacaButelki implements Runnable
{
    public MaszynaProdukujacaButelki(Skrzynka skrzynka, Lock lock, Condition oczekiwanie)
    {
        this.skrzynka = skrzynka;
        this.lock = lock;
        this.oczekiwanie = oczekiwanie;
    }
    @Override
    public void run() 
    {
        lock.lock();
        try
        {
            System.out.println(Thread.currentThread().getName()+": Zaczynam produkować butelki.");
            while(true)
            {
                while(skrzynka.jestPelna())
                {
                    try 
                    {
                        System.out.println(Thread.currentThread().getName()+": Informuję, że trzeba wymienić skrzynkę.");
                         //skrzynka.wait(); - poprzednia metoda
                        oczekiwanie.await();
                        System.out.println(Thread.currentThread().getName()+": Powróciłem do produkcji.");
                    } 
                    catch (InterruptedException ex) 
                    {
                        ex.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getName()+": Wyprodukowałem "+(++i)+" Butelkę");
                skrzynka.dodaj(new Butelka());
                
                //skrzynka.notifyAll();
                oczekiwanie.signalAll();
            }
        }
        finally
        {
            lock.unlock();
        }
    }
    private Skrzynka skrzynka;
    private Lock lock;
    private Condition oczekiwanie;
    private int i = 0;
}

class MaszynaZmieniającaSkrzynki implements Runnable
{
    public MaszynaZmieniającaSkrzynki(Skrzynka skrzynka, Lock lock, Condition oczekiwanie)
    {
        this.skrzynka = skrzynka;
        this.lock = lock;
        this.oczekiwanie = oczekiwanie;
    }
    @Override
    public void run() 
    {
        //synchronised(skrzynka) - poprzednia metoda bez bloku finally
        lock.lock();
        try
        {
            System.out.println(Thread.currentThread().getName()+": Zaczynam przygotowywać się do zmiany skrzynki");
            while(true)
            {
                
                while(!skrzynka.jestPelna())
                {
                    try 
                    {
                        System.out.println(Thread.currentThread().getName()+": Informuję że zakończono zamianę.");
                        //skrzynka.wait(); - poprzednia metoda
                        oczekiwanie.await();
                        System.out.println(Thread.currentThread().getName()+": Powróciłem do zamiany.");
                    } 
                    catch (InterruptedException ex) 
                    {
                        ex.printStackTrace();
                    }
                }
                skrzynka.pobierzIloscButelek();
                skrzynka.zamiana();
                skrzynka.pobierzIloscButelek();
                
                //skrzynka.notifyAll();
                oczekiwanie.signalAll();
            }
        }
        finally
        {
            lock.unlock();
        }
    }
    private Skrzynka skrzynka;
    private Lock lock;
    private Condition oczekiwanie;
}

class Skrzynka
{
    public synchronized boolean jestPelna()
    {
        if(listaButelek.size() == pojemnosc)
            return true;
        
        return false;
    }
    public synchronized int pobierzIloscButelek()
    {
        System.out.println(Thread.currentThread().getName()+ " Aktualnie w skrzynce jest: "+this.listaButelek.size());
        return this.listaButelek.size();
    }
    public synchronized void dodaj(Butelka butelka)
    {
        listaButelek.add(butelka);
    }
    public synchronized void zamiana()
    {
        System.out.println(Thread.currentThread().getName()+": Zamieniam skrzynki");
        listaButelek.clear();
    }
    
    private final int pojemnosc = 10;
    private ArrayList listaButelek = new ArrayList();
}

class Butelka
{
    
}
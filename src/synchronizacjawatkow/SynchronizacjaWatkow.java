package synchronizacjawatkow;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SynchronizacjaWatkow 
{
    public static void main(String[] args) 
    {
        Skrzynka skrzynka = new Skrzynka();
        
        MaszynaProdukujacaButelki maszyna1 = new MaszynaProdukujacaButelki(skrzynka);
        MaszynaZmieniającaSkrzynki maszyna2 = new MaszynaZmieniającaSkrzynki(skrzynka);
        
        Thread produkcja = new Thread(maszyna1, "Producent");
        Thread zmieniacz = new Thread(maszyna2, "Zmieniacz");
        
        produkcja.start();
        zmieniacz.start();
    }   
}

class MaszynaProdukujacaButelki implements Runnable
{
    public MaszynaProdukujacaButelki(Skrzynka skrzynka)
    {
        this.skrzynka = skrzynka;
    }
    @Override
    public void run() 
    {
        synchronized(skrzynka)
        {
            System.out.println(Thread.currentThread().getName()+": Zaczynam produkować butelki.");
            while(true)
            {
                while(skrzynka.jestPelna())
                {
                    try 
                    {
                        System.out.println(Thread.currentThread().getName()+": Informuję, że trzeba wymienić skrzynkę.");
                        skrzynka.wait();
                        System.out.println(Thread.currentThread().getName()+": Powróciłem do produkcji.");
                    } 
                    catch (InterruptedException ex) 
                    {
                        ex.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getName()+": Wyprodukowałem "+(++i)+" Butelkę");
                skrzynka.dodaj(new Butelka());
                
                skrzynka.notifyAll();
            }
        }
    }
    private Skrzynka skrzynka;
    private int i = 0;
}

class MaszynaZmieniającaSkrzynki implements Runnable
{
    public MaszynaZmieniającaSkrzynki(Skrzynka skrzynka)
    {
        this.skrzynka = skrzynka;
    }
    @Override
    public void run() 
    {
        synchronized(skrzynka)
        {
            System.out.println(Thread.currentThread().getName()+": Zaczynam przygotowywać się do zmiany skrzynki");
            while(true)
            {
                
                while(!skrzynka.jestPelna())
                {
                    try 
                    {
                        System.out.println(Thread.currentThread().getName()+": Informuję że zakończono zamianę.");
                        skrzynka.wait();
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
                
                skrzynka.notifyAll();
            }
        }
    }
    private Skrzynka skrzynka;
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
package edu.eci.arsw.blacklistvalidator;

import java.util.LinkedList;

public class Porciones {
    public Porciones(int F, int N){

        int partes = F/N;
        int sobrante = F%N;

        LinkedList<Integer> porciones = new LinkedList<>();

        int inicio = partes + sobrante;
        porciones.add(inicio);
        for(int i=0;i<N-1;i++){
            int fin = inicio + partes;
            int finHilo = fin -1;
            System.out.println("Hilo"+i+": (inicio,fin):"+inicio+","+finHilo);
            inicio = fin;
            porciones.add(inicio);
        }

        System.out.println(porciones);
    }

}

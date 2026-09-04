/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */


    //2. agrego parametro n para dividir la busqueda en hilos
    public List<Integer> checkHost(String ipaddress, int N){



        // esta parte corresponde a la separacion de la lista de acuerdo a la cantidad de hilos
        // si es impar o par, el restante se le suma a la primera porcion y las demas son de partes iguales
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();

        int partes = skds.getRegisteredServersCount()/N;
        int sobrante = skds.getRegisteredServersCount()%N;

        LinkedList<ThreadSearch> hilos = new LinkedList<>();

        int inicio = partes + sobrante;
        ThreadSearch hilo = new ThreadSearch(0,inicio,ipaddress);
        hilos.add(hilo);
        for(int i=0;i<N-1;i++){
            int fin = inicio + partes;
            int finHilo = fin -1;
            ThreadSearch hilo1 = new ThreadSearch(inicio,finHilo,ipaddress);
            inicio = fin;
            hilos.add(hilo1);

        }


        // por la cantidad de hilos los inicia, respetando el ciclo de vida.
        for(ThreadSearch i : hilos){
            i.start();
        }

        // para evitar condiciones carrera, espero a que todos los hilos terminen con el join()
        // sin hacer el join, el buscador no espera a que los hilos acaben y dara una lista vacia diciendo que cualquiera es confiable
        for(ThreadSearch i : hilos){
            try {
                i.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        // Se recopilan los datos en las ips buscadas y las apariciones
        // Se juntan las respuestas de todos los hilos para dar formato a la salida
        int ocurrencesCount = 0;
        int checkedListsCount = 0;
        LinkedList<Integer> blackListOcurrences = new LinkedList<>();

        for(ThreadSearch i : hilos){

            LinkedList<Integer> listaPorHilo = i.getBlackListOcurrences();

            blackListOcurrences.addAll(listaPorHilo);
            ocurrencesCount = ocurrencesCount + listaPorHilo.size();
            checkedListsCount = checkedListsCount + i.getCheckedListsCount();

        }


        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}

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
    public List<Integer> checkHost(String ipaddress, int N){

        
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

        }

        for(int i=0;i<N-1;i++){
            int fin = inicio + partes;
            int finHilo = fin -1;
            System.out.println("Hilo"+i+": (inicio,fin):"+inicio+","+finHilo);
            inicio = fin;
            porciones.add(inicio);
        }




        ThreadSearch hilo = new ThreadSearch(0,skds.getRegisteredServersCount(),ipaddress);
        hilo.start();
        try {
            hilo.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        LinkedList<Integer> blackListOcurrences = hilo.getBlackListOcurrences();

        int ocurrencesCount = blackListOcurrences.size();

        int checkedListsCount = hilo.getCheckedListsCount();


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

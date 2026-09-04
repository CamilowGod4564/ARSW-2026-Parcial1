package edu.eci.arsw.blacklistvalidator;

import java.util.LinkedList;
import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

public class ThreadSearch extends Thread{

    private final int inicio;
    private final int fin;
    private final String ipaddress;

    private final LinkedList blackListOcurrences;

    private static final int BLACK_LIST_ALARM_COUNT=5;
    private int checkedListsCount;

    public ThreadSearch(int inicio, int fin, String ipAddres){
        this.inicio = inicio;
        this.fin = fin;
        this.ipaddress = ipAddres;
        this.blackListOcurrences = new LinkedList<>();
        this.checkedListsCount = 0;

    }

    @Override
    public void run(){

        int ocurrencesCount=0;

        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();

        for (int i=inicio;i<=fin && ocurrencesCount<BLACK_LIST_ALARM_COUNT;i++){
            checkedListsCount++;

            if (skds.isInBlackListServer(i, ipaddress)){

                blackListOcurrences.add(i);

                ocurrencesCount++;
            }
        }

    }

    public LinkedList getBlackListOcurrences() {
        return blackListOcurrences;
    }

    public int getCheckedListsCount() {
        return checkedListsCount;
    }
}

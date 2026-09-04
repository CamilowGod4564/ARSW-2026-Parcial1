/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.List;

/**
 *
 * @author hcadavid
 */
public class Main {
    
    public static void main(String a[]){


        System.out.println(11/4);
        System.out.println(11%4);

        for(int i=0;i<=10;i++){
            System.out.println(i);
        }
        HostBlackListsValidator hblv=new HostBlackListsValidator();
        List<Integer> blackListOcurrences=hblv.checkHost("202.24.34.55",1);
        System.out.println("The host was found in the following blacklists:"+blackListOcurrences);

        
    }
    
}
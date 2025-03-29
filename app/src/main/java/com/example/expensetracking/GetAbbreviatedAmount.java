package com.example.expensetracking;

import java.text.DecimalFormat;
import java.util.HashMap;

public class GetAbbreviatedAmount {
    static String AbbAmount(float amt){
        float storeamt = amt;
        int gens = 0;

        while(amt > 999){
            amt /= 100;
            gens++;
        }

        if (storeamt >= 1000000000){
            amt = 1000;
            gens = 4;
        }else if (storeamt < 1000){
            amt = storeamt * 10;
            gens = 0;
        }

        // roundup

        float tempamt = amt;
        tempamt *= 10;
        amt = Math.round(tempamt);
        amt /= 10;

        HashMap<Integer,String> Letters = new HashMap<>();

        Letters.put(1,"K");
        Letters.put(2,"L");
        Letters.put(3,"Cr");
        Letters.put(4,"Cr+");

        DecimalFormat DF = new DecimalFormat("#.##");
        amt /= 10;

        return DF.format(amt)+""+Letters.getOrDefault(gens,"");
    }

    static int NextLargeNumber(int amt){
        int inte = amt;
        int i = 1;
        int previnte = 1;

        while (inte > 0){
            previnte = inte;
            inte -= (int) (inte%Math.pow(10,i));
            i++;
        }

        previnte += (int) Math.pow(10,i-2);

        return previnte;
    }
}
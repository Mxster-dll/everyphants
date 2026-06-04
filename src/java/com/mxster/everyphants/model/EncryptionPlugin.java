package com.mxster.everyphants.model;

public class EncryptionPlugin {



    public static String Caesar(String s){
        int l = s.length();
        String s1 ="";

        for(int i = 0; i < l; i++){
            s1 += s.charAt(i) - 3;
        }

        return s1;
    }

    public static String Fence(String s){
        int l = s.length();
        String s1 = "";
        String s2 = "";

        for(int i = 0; i < l / 2; i++){
            s1 += s.charAt(i);
        }
        for(int i = l / 2; i < l; i++){
            s2 += s.charAt(i);
        }

        String s3 = "";
        for(int i = 0; i < l / 2; i++){
            if(i % 2 == 0)
                s3 += s1.charAt(i);
            else s3 += s2.charAt(i);
        }
        return s3;
    }

}

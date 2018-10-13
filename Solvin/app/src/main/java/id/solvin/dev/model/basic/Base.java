package id.solvin.dev.model.basic;

import java.io.Serializable;

/**
 * Created by edinofri on 10/02/2017.
 */

public class Base implements Serializable
{
    protected String getSafeString(String string){
        if(string == null || string.isEmpty()){
            return "";
        }
        return string;
    }
    protected String getSafeStringDefault(String string,String defaultString) {
        if (string == null) {
            return defaultString;
        }
        return string;
    }

    protected String getSafeString(String string,String emptyValue) {
        if (string == null) {
            return "";
        }
        return string;
    }

    protected String getSafeStringDefault(String string,String emptyValue,String defaultValue){
        if(string.equals(emptyValue)){
            return defaultValue;
        }
        return string;
    }
}

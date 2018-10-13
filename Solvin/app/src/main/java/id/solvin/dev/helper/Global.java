package id.solvin.dev.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by edinofri on 01/12/2016.
 */


public class Global {
    public final static int TRIGGGET_UPDATE = 11001;

    public static Global get() {
        return new Global();
    }

    public String getInitialName(String name) {
        String initial = "";
        String[] nameParts = name.split(" ");
        if (nameParts.length == 1) {
            initial += Character.toUpperCase(nameParts[0].charAt(0));
        } else {
            for (String namePart : nameParts) {
                initial += Character.toUpperCase(namePart.charAt(0));
                if (initial.length() == 2) {
                    break;
                }
            }
        }
        return initial;
//        String[] explode_word = name.split(" ");
//        if(explode_word.length>1){
//            return (explode_word[0].substring(0,1)+explode_word[1].substring(0,1)).toUpperCase();
//        }else{
//            return (explode_word[0].substring(0,2)).toUpperCase();
//        }
    }

    public String getAssetURL(String nameFile, String kategori) {
        return ConfigApp.get().API_ASSETS_URL + "/" + kategori + "/" + nameFile;
    }

    public String convertDateToFormat(String date, String pattern) {
        SimpleDateFormat dfParse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dfFormat = new SimpleDateFormat(pattern);
        try {
            if (dfFormat.format(dfParse.parse(date)).split("-").length > 1) {
                return String.format("%s pada %s", dfFormat.format(dfParse.parse(date)).split("-")[0], dfFormat.format(dfParse.parse(date)).split("-")[1]);
            }
            return dfFormat.format(dfParse.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getExpiredPayment(String date) {
        SimpleDateFormat dfParse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = new GregorianCalendar();
        try {
            c.setTime(dfParse.parse(date));
            c.add(Calendar.DATE, 1);
            String date_convert = dfParse.format(c.getTime());
            return convertDateToFormat(date_convert, "EEEE, dd MMMM yyyy-HH.mm ");
//            String end_date = df.format(c.getTime());
//            showtoast("end Time : "+end_date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "-";
    }

    public int getDifferentMonth(String date) {
        SimpleDateFormat dfParse = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar startCalendar = new GregorianCalendar();
        try {
            startCalendar.setTime(dfParse.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(new Date());

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
        return diffMonth;
    }

    public long getDifferentYear(String date) {
        SimpleDateFormat dfParse = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date_birth = dfParse.parse(date);
            Date now = new Date();
            long diff = now.getTime() - date_birth.getTime();
            return diff / 1000 / 60 / 60 / 24 / 356;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public RequestBody convertToRequestBody(String s) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), s);
        return body;
    }

    public GlobalKey key() {
        return new GlobalKey();
    }

    public class GlobalKey {
        public final String QUESTION_MODE = "QUESTION_MODE";
        public final String QUESTION_DATA = "QUESTION_DATA";
        public final String QUESTION_DATA_POSITION = "QUESTION_DATA_POSITION";
        // api
        public final String REQUEST_FIREBASE = "firebase";
        public final String REQUEST_PHONE = "phone";

        public final String REQUEST_CODE_VERIFY = "code";
        public final String REQUEST_NAME = "name";
        public final String REQUEST_EMAIL = "email";
        public final String REQUEST_PASSWORD = "password";
        public final String REQUEST_MEMBER_CODE = "member_code";

        public final String REQUEST_PACKAGE_ID = "package_id";
        public final String REQUEST_BANK_ID = "bank_id";
        public final String REQUEST_MOBILE_NETWORK_ID = "mobile_network_id";
        public final String REQUEST_UNIQUE_CODE = "unique_code";
    }

    public GlobalConstant value() {
        return new GlobalConstant();
    }

    public class GlobalConstant {
        public final int QUESTION_MODE_CREATE = 0;
        public final int QUESTION_MODE_EDIT = 1;
    }
}
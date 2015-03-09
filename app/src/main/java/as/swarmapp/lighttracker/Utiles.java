package as.swarmapp.lighttracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by asmodeeus on 09/03/15.
 */
public final class Utiles {



    public static String streamToString(InputStream in){
        int n = 0;
        StringBuffer sb = new StringBuffer();
        try{
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            while ((n = isr.read()) != -1) {
                sb.append((char)n);
            }
            in.close();

        }catch (IOException e){
            e.printStackTrace();
            return "";

        }
        return sb.toString();
    }
}

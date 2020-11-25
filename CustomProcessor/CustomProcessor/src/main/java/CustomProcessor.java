import org.json.simple.JSONObject;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by taray on 06/02/2019.
 */
@Information(name="Publicar tweets", description="Publica un tweet con la informacion de una estacion meteorologica")
public class CustomProcessor {


    CustomProcessor(){


    }


    String process(String data, String CONSUMER_KEY, String CONSUMER_SECRET, String ACCESS_TOKEN, String ACCESS_TOKEN_SECRET, String nombreEstacion){

        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedDate=dateFormat.format(date);
        String msg= "El dioxido de Nitrogeno en la estacion " + nombreEstacion + " es " + data + "  a las " + formattedDate;

        StatusUpdate status;

        Twitter twitter;

        //String CONSUMER_KEY = "Usw6RrcjrszXzzhukeVtQbpJt";
        //String CONSUMER_SECRET = "GxzfBb0OkLPXtRGi5n9qIDPB8lct1zUk9mmLIfMu8ae7u2eWPF";
        //String ACCESS_TOKEN = "1135539195919949824-ajOIi3lnspiN9dW3IsLkLzCxpoiVTs";
        //String ACCESS_TOKEN_SECRET = "UEHfuLHZlkXaeN7ZgFMHG9p8tyXGmlYFr6KAw3HRP8JcS";

        String key = CONSUMER_KEY;
        String secret = CONSUMER_SECRET;
        String token = ACCESS_TOKEN;
        String token_secret = ACCESS_TOKEN_SECRET;

        ConfigurationBuilder cb = new ConfigurationBuilder();

        cb.setDebugEnabled(true).setOAuthConsumerKey(key).setOAuthConsumerSecret(secret)
                .setOAuthAccessToken(token).setOAuthAccessTokenSecret(token_secret);

        Configuration conf = cb.build();

        try {

            TwitterFactory tf = new TwitterFactory(conf);
            twitter = tf.getInstance();
            twitter.verifyCredentials();

            int id=0;

            if (id != -1) {


                if (msg.length() > 280) {
                    msg = msg.substring(0, 275) + "...";
                }
                status = new StatusUpdate(msg);
                status.setInReplyToStatusId(id);
            } else {
                if (msg.length() > 280) {
                    msg = msg.substring(0, 275) + "...";
                }
                status = new StatusUpdate(msg);
            }


            twitter.updateStatus(status);


        } catch (TwitterException ex) {
            System.out.println("Error: " + ex.getMessage());

        }
        return data;
    }

}

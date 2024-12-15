package tinosnegocios.new_user_created.consume.Services;

import com.google.gson.Gson;
import tinosnegocios.new_user_created.consume.Models.UserCreated;

public class ProcessReadMessage {
    public boolean Process(String message){
        Gson gson = new Gson();

        try{
            SNSService snsService = new SNSService();

            UserCreated user = gson.fromJson(message, UserCreated.class);

            snsService.SubscribeTopis(user);


            System.out.println("E-mail enviado para " + user.getEmailAddress());
            return true;
        }catch (Exception ex){
            return false;
        }
    }
}

package tinosnegocios.new_user_created.consume.Services;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;
import tinosnegocios.new_user_created.consume.Models.UserCreated;

import java.util.ArrayList;
import java.util.List;

public class SNSService {
    SnsClient snsClient;
    Topic theTopic;

    public SNSService() {
        String accessKey = System.getenv().getOrDefault("AWSAccessKey", "****");
        String secretKey = System.getenv().getOrDefault("AWSSecretKey", "****");

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();

        GetTopic(snsClient);
    }

    public boolean SubscribeTopis(UserCreated user) {
        SubscribeRequest request = SubscribeRequest.builder()
                .protocol("email")
                .endpoint(user.getEmailAddress())
                .returnSubscriptionArn(true)
                .topicArn(theTopic.topicArn())
                .build();

        SubscribeResponse result = snsClient.subscribe(request);

        return true;
    }

    private void GetTopic(SnsClient snsClient) {
        try {
            ListTopicsRequest request = ListTopicsRequest.builder()
                    .build();

            ListTopicsResponse result = snsClient.listTopics(request);
            List<Topic> topics = result.topics();

            for (Topic topic : topics) {
                if (topic.topicArn().contains("user-created-email")) {
                    theTopic = topic;
                }
            }
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }


    public void searchAndSendEmailToConfirmedEmails(){

        List<String> usersConfirmed = UsersConfirmed();
        if(!usersConfirmed.isEmpty()){
            sendWelcomeEmail(usersConfirmed);
        }
    }
    private List<String> UsersConfirmed() {
        List<String> confirmedSubscription = new ArrayList<>();

        ListSubscriptionsByTopicRequest request = ListSubscriptionsByTopicRequest.builder()
                .topicArn(theTopic.topicArn())
                .build();

        ListSubscriptionsByTopicResponse response = snsClient.listSubscriptionsByTopic(request);

        for (Subscription subscription : response.subscriptions()) {
            if (subscription.subscriptionArn() != null && !subscription.subscriptionArn().equals("PendingConfirmation")) {
                System.out.println("Email: " + subscription.endpoint());
                System.out.println("Subscription ARN: " + subscription.subscriptionArn());
                System.out.println("Protocolo: " + subscription.protocol());
                System.out.println("----------------------------");

                confirmedSubscription.add(subscription.subscriptionArn());
            }
        }

        return confirmedSubscription;
    }
    private void sendWelcomeEmail(List<String> usersConfirmed) {
        String message = "Olá, seja bem-vindo à nossa plataforma! Estamos felizes em ter você conosco.";

        PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(theTopic.topicArn())
                .message(message)
                .subject("Bem-vindo ao ChurchManager!")
                .build();

        PublishResponse response = snsClient.publish(publishRequest);
        System.out.println("Mensagem de boas-vindas enviada para o tópico SNS. Message ID: " + response.messageId());

        for (String sub : usersConfirmed){
            snsClient.unsubscribe(unsubscribe -> unsubscribe.subscriptionArn(sub));
        }
    }
}

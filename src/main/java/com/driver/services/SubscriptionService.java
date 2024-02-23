package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user=userRepository.findById(subscriptionEntryDto.getUserId()).get();
        LocalDate currentDate = LocalDate.now();
        Date date = java.sql.Date.valueOf(currentDate);
        SubscriptionType subscriptionType=subscriptionEntryDto.getSubscriptionType();
        int totalAmount=0;
        int noOfScreen=subscriptionEntryDto.getNoOfScreensRequired();
        if(subscriptionType==SubscriptionType.BASIC){
            totalAmount=500+(200*noOfScreen);
        }
        else if(subscriptionType==SubscriptionType.PRO){
            totalAmount=800+(250*noOfScreen);
        }
        else{
            totalAmount=1000+(300*noOfScreen);
        }

        Subscription subscription=new Subscription(subscriptionType,noOfScreen,date,totalAmount);

            subscription.setUser(user);
            user.setSubscription(subscription);//bidirectional mapping

        subscriptionRepository.save(subscription);





        return totalAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        User user=userRepository.findById(userId).get();
        SubscriptionType subscriptionType=user.getSubscription().getSubscriptionType();
        if(subscriptionType==SubscriptionType.ELITE){
            throw new Exception("Already the best Subscription");
        }
        else if(subscriptionType==SubscriptionType.BASIC){
            // we will update it to pro
            int noOfScreen=user.getSubscription().getNoOfScreensSubscribed();
            int priceForPro=800 + (250*noOfScreen);
            int diff=priceForPro-user.getSubscription().getTotalAmountPaid();

            Subscription subscription=user.getSubscription();
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(priceForPro);
            user.setSubscription(subscription);
            subscriptionRepository.save(subscription);
            return diff;

        }
        else{
            int noOfScreen=user.getSubscription().getNoOfScreensSubscribed();
            int priceForElite=1000 + (300*noOfScreen);
            int diff=priceForElite-user.getSubscription().getTotalAmountPaid();

            Subscription subscription=user.getSubscription();
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(priceForElite);
            user.setSubscription(subscription);
            subscriptionRepository.save(subscription);
            return diff;

        }
        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository


    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList=subscriptionRepository.findAll();
        int ans=0;
        for(Subscription i:subscriptionList){

            ans+=i.getTotalAmountPaid();


        }
        return ans;
    }

}

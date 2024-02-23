package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

            ProductionHouse productionHouse=productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
            String seriesName= webSeriesEntryDto.getSeriesName();
            WebSeries findwebSeries=webSeriesRepository.findBySeriesName(seriesName);

        if( findwebSeries!=null){
            throw new Exception("Series is already present");
        }
           WebSeries webSeries=new WebSeries(webSeriesEntryDto.getSeriesName(), webSeriesEntryDto.getAgeLimit(),webSeriesEntryDto.getRating(),webSeriesEntryDto.getSubscriptionType());

            double productionRating=(productionHouse.getRatings())*(productionHouse.getWebSeriesList().size());
            double newRating=(productionRating+webSeriesEntryDto.getRating())/(productionHouse.getWebSeriesList().size()+1);

            productionHouse.setRatings(newRating);
            productionHouse.getWebSeriesList().add(webSeries);
            webSeries.setProductionHouse(productionHouse);

           webSeries= webSeriesRepository.save(webSeries);
           productionHouseRepository.save(productionHouse);

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        return webSeries.getId();
    }

}

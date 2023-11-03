package com.example.pidev.Interfaces;

import com.example.pidev.Entities.MarketPlace;

import java.util.List;

public interface IMarketPlaceService {

    List<MarketPlace> retrieveAvailableSales();
    List<MarketPlace> retrieveSoldItems();
}

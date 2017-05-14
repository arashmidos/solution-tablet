package com.parsroyal.solutiontablet.data.model;

import com.parsroyal.solutiontablet.data.entity.City;

import java.util.List;

/**
 * Created by Mahyar on 6/19/2015.
 */
public class CityList extends BaseModel
{

    private List<City> cityList;

    public List<City> getCityList()
    {
        return cityList;
    }

    public void setCityList(List<City> cityList)
    {
        this.cityList = cityList;
    }
}

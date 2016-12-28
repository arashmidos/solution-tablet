package com.conta.comer.data.model;

import com.conta.comer.data.entity.Province;

import java.util.List;

/**
 * Created by Mahyar on 6/15/2015.
 */
public class ProvinceList extends BaseModel
{

    private List<Province> provinceList;

    public List<Province> getProvinceList()
    {
        return provinceList;
    }

    public void setProvinceList(List<Province> provinceList)
    {
        this.provinceList = provinceList;
    }
}

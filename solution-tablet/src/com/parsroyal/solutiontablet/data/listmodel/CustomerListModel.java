package com.parsroyal.solutiontablet.data.listmodel;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.parsroyal.solutiontablet.data.model.BaseListModel;

/**
 * Created by Mahyar on 7/6/2015.
 */
public class CustomerListModel extends BaseListModel implements ClusterItem
{
    private String address;
    private String phoneNumber;
    private String cellPhone;
    private boolean hasLocation;
    private boolean isVisited;
    private double xlocation;
    private double ylocation;
    private boolean hasOrder;
    private boolean hasRejection;
    private Long codeNumber;
    private Float distance;
    private Long backendId;
    private String lastVisit;

    public Long getBackendId()
    {
        return backendId;
    }

    public void setBackendId(Long backendId)
    {
        this.backendId = backendId;
    }

    public String getLastVisit()
    {
        return lastVisit;
    }

    public void setLastVisit(String lastVisit)
    {
        this.lastVisit = lastVisit;
    }

    public boolean hasOrder()
    {
        return hasOrder;
    }

    public void setHasOrder(boolean hasOrder)
    {
        this.hasOrder = hasOrder;
    }

    public boolean hasRejection()
    {
        return hasRejection;
    }

    public void setHasRejection(boolean hasRejection)
    {
        this.hasRejection = hasRejection;
    }

    public double getXlocation()
    {
        return xlocation;
    }

    public void setXlocation(double xlocation)
    {
        this.xlocation = xlocation;
    }

    public double getYlocation()
    {
        return ylocation;
    }

    public void setYlocation(double ylocation)
    {
        this.ylocation = ylocation;
    }

    public boolean hasLocation()
    {
        return hasLocation;
    }

    public void setHasLocation(boolean hasLocation)
    {
        this.hasLocation = hasLocation;
    }

    public boolean isVisited()
    {
        return isVisited;
    }

    public void setVisited(boolean visited)
    {
        isVisited = visited;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getCellPhone()
    {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone)
    {
        this.cellPhone = cellPhone;
    }

    public Float getDistance()
    {
        return distance;
    }

    public void setDistance(Float distance)
    {
        this.distance = distance;
    }

    public Long getCodeNumber()
    {
        return codeNumber;
    }

    public void setCodeNumber(Long codeNumber)
    {
        this.codeNumber = codeNumber;
    }

    @Override
    public LatLng getPosition()
    {
        return new LatLng(xlocation, ylocation);
    }

    @Override
    public String getSnippet()
    {
        return address;
    }
}

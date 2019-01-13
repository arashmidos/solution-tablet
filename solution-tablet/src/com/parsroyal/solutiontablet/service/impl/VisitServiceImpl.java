package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import android.location.Location;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.constants.SaleType;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.CustomerPicDao;
import com.parsroyal.solutiontablet.data.dao.VisitInformationDao;
import com.parsroyal.solutiontablet.data.dao.VisitInformationDetailDao;
import com.parsroyal.solutiontablet.data.dao.VisitLineDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerPicDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitInformationDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitInformationDetailDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitLineDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.entity.VisitLine;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.data.model.VisitInformationDetailDto;
import com.parsroyal.solutiontablet.data.model.VisitInformationDto;
import com.parsroyal.solutiontablet.data.searchobject.VisitInformationDetailSO;
import com.parsroyal.solutiontablet.service.PositionService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Arash on 2017-03-08
 */
public class VisitServiceImpl implements VisitService {

  private final SettingServiceImpl settingService;
  private Context context;
  private CustomerDao customerDao;
  private CustomerPicDao customerPicDao;
  private VisitLineDao visitLineDao;
  private PositionService positionService;
  private VisitInformationDao visitInformationDao;
  private VisitInformationDetailDao visitInformationDetailDao;

  public VisitServiceImpl(Context context) {
    this.context = context;
    this.customerDao = new CustomerDaoImpl(context);
    this.customerPicDao = new CustomerPicDaoImpl(context);
    this.visitLineDao = new VisitLineDaoImpl(context);
    this.visitInformationDao = new VisitInformationDaoImpl(context);
    this.visitInformationDetailDao = new VisitInformationDetailDaoImpl(context);
    this.positionService = new PositionServiceImpl(context);
    this.settingService = new SettingServiceImpl();
  }

  @Override
  public List<VisitLine> getAllVisitLines() {
    return visitLineDao.retrieveAll();
  }

  @Override
  public List<VisitLineListModel> getAllVisitLinesListModel() {
    return visitLineDao.getAllVisitLineListModel();
  }

  @Override
  public List<VisitLineListModel> getAllVisitLinesListModel(Date from, Date to) {
    return visitLineDao.getAllVisitLineListModel(from, to);
  }

  @Override
  public List<LabelValue> getAllVisitLinesLabelValue() {
    return visitLineDao.getAllVisitLineLabelValue();
  }

  @Override
  public VisitLineListModel getVisitLineListModelByBackendId(long visitlineBackendId) {
    return visitLineDao.getVisitLineListModelByBackendId(visitlineBackendId);
  }

  @Override
  public Long startVisiting(Long customerBackendId, int distance) {
    VisitInformation visitInformation = new VisitInformation();
    visitInformation.setCustomerBackendId(customerBackendId);
    visitInformation
        .setVisitDate(DateUtil.convertDate(new Date(), DateUtil.GLOBAL_FORMATTER, "FA"));
    visitInformation.setStartTime(DateUtil.convertDate(new Date(), DateUtil.TIME_24, "EN"));
//    visitInformation.setUpdateDateTime(
//        DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
    Date networkDate = SolutionTabletApplication.getInstance().getTrueTime();
    visitInformation.setNetworkDate(networkDate == null ? null : networkDate.getTime());
    visitInformation.setDistance((long) distance);
    return saveVisit(visitInformation);
  }

  @Override
  public Long startPhoneVisiting(Long customerBackendId) {
    VisitInformation visitInformation = new VisitInformation();
    visitInformation.setCustomerBackendId(customerBackendId);
    visitInformation
        .setVisitDate(DateUtil.convertDate(new Date(), DateUtil.GLOBAL_FORMATTER, "FA"));
    visitInformation.setStartTime(DateUtil.convertDate(new Date(), DateUtil.TIME_24, "EN"));

    Date networkDate = SolutionTabletApplication.getInstance().getTrueTime();
    visitInformation.setNetworkDate(networkDate == null ? null : networkDate.getTime());
    visitInformation.setDistance(0L);
    visitInformation.setPhoneVisit(true);
    return saveVisit(visitInformation);
  }

  @Override
  public Long startVisitingNewCustomer(Long customerId) {
    VisitInformation visitInformation = new VisitInformation();
    visitInformation.setCustomerId(customerId);
    visitInformation
        .setVisitDate(DateUtil.convertDate(new Date(), DateUtil.GLOBAL_FORMATTER, "FA"));
    visitInformation.setStartTime(DateUtil.convertDate(new Date(), DateUtil.TIME_24, "EN"));
    visitInformation.setUpdateDateTime(
        DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
    Date networkDate = SolutionTabletApplication.getInstance().getTrueTime();
    visitInformation.setNetworkDate(networkDate == null ? null : networkDate.getTime());
    // -1 Means new customer without backendId
    visitInformation.setResult(-1L);
    Position position = SolutionTabletApplication.getInstance().getLastSavedPosition();

    if (Empty.isNotEmpty(position)) {
      visitInformation.setxLocation(position.getLatitude());
      visitInformation.setyLocation(position.getLongitude());
      return saveVisit(visitInformation);
    } else {
      visitInformation.setxLocation(0.0);
      visitInformation.setyLocation(0.0);
      final Long visitId = saveVisit(visitInformation);
      /*locationService.findCurrentLocation(new FindLocationListener() {
        @Override
        public void foundLocation(Location location) {
          visitInformation.setxLocation(0.0);
          visitInformation.setyLocation(0.0);
          updateVisitLocation(visitId, location);
        }

        @Override
        public void timeOut() {
        }
      });*/
      return visitId;
    }
  }

  @Override
  public void finishVisiting(Long visitId) {
    VisitInformation visitInformation = visitInformationDao.retrieve(visitId);
    visitInformation.setEndTime(DateUtil.convertDate(new Date(), DateUtil.TIME_24, "EN"));

    Date networkDate = SolutionTabletApplication.getInstance().getTrueTime();
    visitInformation.setEndNetworkDate(networkDate == null ? null : networkDate.getTime());
    saveVisit(visitInformation);
  }

  @Override
  public List<VisitInformation> getAllVisitInformationForSend() {
    return visitInformationDao.getAllVisitInformationForSend();
  }

  @Override
  public VisitInformation getVisitInformationById(Long visitId) {
    return visitInformationDao.retrieve(visitId);
  }

  @Override
  public VisitInformation getVisitInformationForNewCustomer(Long customerId) {
    return visitInformationDao.retrieveForNewCustomer(customerId);
  }

  @Override
  public Long saveVisit(VisitInformation visitInformation) {
    if (Empty.isEmpty(visitInformation.getId())) {
      visitInformation.setCreateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      visitInformation.setUpdateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      return visitInformationDao.create(visitInformation);
    } else {
      visitInformation.setUpdateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      visitInformationDao.update(visitInformation);
      return visitInformation.getId();
    }
  }

  @Override
  public void updateVisitLocation(Long visitInformationId, Location location) {
    visitInformationDao
        .updateLocation(visitInformationId, location.getLatitude(), location.getLongitude());
  }

  @Override
  public void updateVisitLocation(Long visitInformationId, Position position) {
    visitInformationDao
        .updateLocation(visitInformationId, position.getLatitude(), position.getLongitude());
  }


  @Override
  public void saveVisitDetail(VisitInformationDetail visitDetail) {
    if (Empty.isEmpty(visitDetail.getId())) {
      visitDetail.setCreateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      visitDetail.setUpdateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      visitInformationDetailDao.create(visitDetail);
    } else {
      visitDetail.setCreateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      visitDetail.setUpdateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      visitInformationDetailDao.update(visitDetail);
    }
  }

  @Override
  public List<VisitInformationDetail> getAllVisitDetailById(Long visitId) {
    return visitInformationDetailDao.getAllVisitDetail(visitId);
  }

  @Override
  public void updateVisitDetailId(VisitInformationDetailType type, long id, long backendId) {
    visitInformationDetailDao.updateVisitDetailId(type, id, backendId);
  }

  @Override
  public List<VisitInformationDetail> searchVisitDetail(Long visitId,
      VisitInformationDetailType type, Long typeId) {
    return visitInformationDetailDao.search(visitId, type, typeId);
  }

  @Override
  public List<VisitInformationDetail> searchVisitDetail(Long visitId,
      VisitInformationDetailType type) {
    return visitInformationDetailDao.search(visitId, type);
  }

  @Override
  public List<VisitInformationDetail> searchVisitDetail(
      VisitInformationDetailSO visitInformationDetailSO) {
    return visitInformationDetailDao.search(visitInformationDetailSO);
  }

  @Override
  public List<VisitInformationDto> getAllVisitDetailForSend(Long visitId) {
    List<VisitInformationDto> visitList = visitInformationDao
        .getAllVisitInformationDtoForSend(visitId);
    SaleType saleType = SaleType.getByValue(
        Long.parseLong(settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE)));
    for (VisitInformationDto visit : visitList) {
      Map<VisitInformationDetailType, VisitInformationDetailDto> map = new HashMap<>();
      List<VisitInformationDetailDto> tempDetailList = visitInformationDetailDao
          .getAllVisitDetailDto(visit.getId());

      for (int i = 0; i < tempDetailList.size(); i++) {
        VisitInformationDetailDto detailToAdd = tempDetailList.get(i);
        if (map.containsKey(detailToAdd.getType())) {
          VisitInformationDetailDto detailToUpdate = map.get(detailToAdd.getType());
          if (detailToUpdate.getData().equals("")) {
            detailToUpdate.setData(detailToUpdate.getTypeId() + "&" + detailToAdd.getTypeId());
            detailToUpdate.setTypeId(0);
          } else {
            detailToUpdate.setData(detailToUpdate.getData() + "&" + detailToAdd.getTypeId());
          }
          map.put(detailToUpdate.getType(), detailToUpdate);
        } else {
          if (detailToAdd.getType() == VisitInformationDetailType.TAKE_PICTURE) {
            detailToAdd.setData(String.valueOf(detailToAdd.getTypeId()));
          }
          map.put(detailToAdd.getType(), detailToAdd);
        }
      }
      visit.setDetails(new ArrayList<>(map.values()));
      visit.setSaleType(saleType);
    }
    return visitList;
  }

  @Override
  public Long startAnonymousVisit() {
    VisitInformation visitInformation = new VisitInformation();
    visitInformation
        .setVisitDate(DateUtil.convertDate(new Date(), DateUtil.GLOBAL_FORMATTER, "FA"));
    visitInformation.setStartTime(DateUtil.convertDate(new Date(), DateUtil.TIME_24, "EN"));
    visitInformation.setUpdateDateTime(
        DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
    // -2 Means visit without customer
    visitInformation.setResult(-2L);
    Date networkDate = SolutionTabletApplication.getInstance().getTrueTime();
    visitInformation.setNetworkDate(networkDate == null ? null : networkDate.getTime());

    Position position = SolutionTabletApplication.getInstance().getLastSavedPosition();

    if (Empty.isNotEmpty(position)) {
      visitInformation.setxLocation(position.getLatitude());
      visitInformation.setyLocation(position.getLongitude());
    } else {
      visitInformation.setxLocation(0.0);
      visitInformation.setyLocation(0.0);
    }

    return saveVisit(visitInformation);
  }

  @Override
  public void deleteVisitById(Long visitId) {
    visitInformationDao.delete(visitId);
  }

  @Override
  public void deleteAll() {
    visitLineDao.deleteAll();
    visitInformationDao.deleteAll();
    visitInformationDetailDao.deleteAll();
  }
}

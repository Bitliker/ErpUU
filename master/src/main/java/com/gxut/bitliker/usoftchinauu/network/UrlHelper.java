package com.gxut.bitliker.usoftchinauu.network;


import com.gxut.bitliker.usoftchinauu.config.AppConfig;
import com.gxut.bitliker.usoftchinauu.model.User;

/**
 * Created by Bitliker on 2017/6/20.
 */

public class UrlHelper {
    private static UrlHelper api;
    public String baseUrl;

    public static UrlHelper api() {
        if (api == null) {
            synchronized (UrlHelper.class) {
                if (api == null) api = new UrlHelper();
            }
        }
        return api;
    }

    public UrlHelper() {
        User user = AppConfig.api().getLoginUser();
        if (user != null)
            this.baseUrl = user.getBaseUrl();
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    //获取账套接口
    public String accountUrl() {
        return "http://manage.ubtob.com/public/account";
    }

    public String loginUrl() {
        return this.baseUrl + "mobile/login.action";
    }

    public String mastersUrl() {
        return this.baseUrl + "mobile/getAllMasters.action";
    }

    public String workUrl() {
        return this.baseUrl + "mobile/getWorkDate.action";
    }

    public String workLogUrl() {
        return this.baseUrl + "mobile/oa/workdata.action";
    }

    public String workSetUrl() {
        return this.baseUrl + "mobile/getconfigs.action";
    }

    public String saveCardlogUrl() {
        return this.baseUrl + "mobile/saveCardLog.action";
    }

    public String commonCodeUrl() {
        return this.baseUrl + "common/getCodeString.action";
    }

    public String addWorkReportUrl() {
        return this.baseUrl + "mobile/addWorkReport.action";
    }

    public String catchWorkContentUrl() {
        return this.baseUrl + "oa/persontask/catchWorkContent.action";
    }

    public String getWorkDailyUrl() {
        return this.baseUrl + "mobile/getWorkDaily.action";
    }

    //获取下一节点审批人
    public String getMultiNodeUrl() {
        return this.baseUrl + "common/getMultiNodeAssigns.action";
    }

    //提交下一节点审批人
    public String takeOverTaskUrl() {
        return this.baseUrl + "common/takeOverTask.action";
    }

    //反提交
    public String commonresUrl(String caller) {
        if ("ExtraWork$".equals(caller)) {
            return this.baseUrl + "hr/attendance/resSubmitExtraWork.action";
        } else {
            return this.baseUrl + "mobile/commonres.action";
        }
    }

    //删除
    public String commondeleteUrl(String caller) {
        if ("ExtraWork$".equals(caller)) {
            return this.baseUrl + "hr/attendance/deleteExtraWork.action";
        } else {
            return this.baseUrl + "mobile/commondelete.action";
        }
    }

    public String updateWorkDailyUrl() {
        return this.baseUrl + "oa/persontask/updateWorkDaily.action";
    }

    public String commonUpdateUrl() {
        return this.baseUrl + "mobile/commonUpdate.action";
    }

    public String submitWorkDailyUrl() {
        return this.baseUrl + "oa/persontask/submitWorkDaily.action";
    }

    public String getformandgriddetailUrl(int id) {
        if (id>0){
            return this.baseUrl + "mobile/getformandgriddetail.action";
        }else{
            return this.baseUrl + "mobile/common/getformandgriddetail.action";
        }
    }

    public String getformandgriddataUrl() {
        return this.baseUrl + "mobile/common/getformandgriddata.action";
    }

    public String commonSaveAndSubmitUrl(String caller) {
        if ("ExtraWork$".equals(caller)) {
            return this.baseUrl + "mobile/oa/ExtraWorkSaveAndSubmit.action";
        } else {
            return this.baseUrl + "mobile/oa/commonSaveAndSubmit.action";
        }
    }

    public String getoaconifgUrl() {
        return this.baseUrl + "mobile/oa/getoaconifg.action";
    }

    public String commonListUrl() {
        return this.baseUrl + "mobile/common/list.action";
    }


}

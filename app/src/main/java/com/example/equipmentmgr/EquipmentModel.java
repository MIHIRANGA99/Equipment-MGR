package com.example.equipmentmgr;

public class EquipmentModel {

    String eqName, modNo, prtNo, serNo, freq, addedDate, ID;

    public EquipmentModel() {

    }

    public EquipmentModel(String eqName, String modNo, String prtNo, String serNo, String freq, String addedDate, String ID) {
        this.eqName = eqName;
        this.modNo = modNo;
        this.prtNo = prtNo;
        this.serNo = serNo;
        this.freq = freq;
        this.addedDate = addedDate;
        this.ID = ID;
    }

    public String getEqName() {
        return eqName;
    }

    public void setEqName(String eqName) {
        this.eqName = eqName;
    }

    public String getModNo() {
        return modNo;
    }

    public void setModNo(String modNo) {
        this.modNo = modNo;
    }

    public String getPrtNo() {
        return prtNo;
    }

    public void setPrtNo(String prtNo) {
        this.prtNo = prtNo;
    }

    public String getSerNo() {
        return serNo;
    }

    public void setSerNo(String serNo) {
        this.serNo = serNo;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}

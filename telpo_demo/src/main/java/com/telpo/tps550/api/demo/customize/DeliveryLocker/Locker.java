package com.telpo.tps550.api.demo.customize.DeliveryLocker;

public class Locker {
    private String lockerId;
    private boolean isOccupied;
    private boolean isLocked;
    private String packageId;


    public void setLocked(boolean locked) {
        isLocked = locked;
    }
    public void setLockerId(String lockerId) {
        this.lockerId = lockerId;
    }
    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }
    // Getters and Setters
    public String getPackageId() {
        return packageId;
    }

    public boolean isLocked() {
        return isLocked;
    }
    public boolean isOccupied() {
        return isOccupied;
    }

    public String getLockerId() {
        return lockerId;
    }

}

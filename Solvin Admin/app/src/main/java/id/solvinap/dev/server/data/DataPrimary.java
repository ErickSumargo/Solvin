package id.solvinap.dev.server.data;

import java.util.List;

/**
 * Created by Erick Sumargo on 3/2/2017.
 */

public class DataPrimary {
    private List<DataSubject> materialList;
    private List<DataPackage> packageList;

    public List<DataPackage> getPackageList() {
        return packageList;
    }

    private void setPackageList(List<DataPackage> packageList) {
        this.packageList = packageList;
    }

    public List<DataSubject> getMaterialList() {
        return materialList;
    }

    private void setMaterialList(List<DataSubject> materialList) {
        this.materialList = materialList;
    }
}
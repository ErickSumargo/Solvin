package id.solvinap.dev.view.helper;

/**
 * Created by Erick Sumargo on 2/8/2017.
 */

public class CategoryBus {
    private int subjectId, materialId;
    private String materialFillText;

    public CategoryBus(int subjectId, int materialId, String materialFillText) {
        this.subjectId = subjectId;
        this.materialId = materialId;
        this.materialFillText = materialFillText;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public String getMaterialFillText() {
        return materialFillText;
    }
}
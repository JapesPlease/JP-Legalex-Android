
package lax.lega.rv.com.legalax.pojos.getSetting;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("id")
    private Long mId;
    @SerializedName("setting_name")
    private String mSettingName;
    @SerializedName("setting_value")
    private String mSettingValue;
    @SerializedName("updated_at")
    private String mUpdatedAt;

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getSettingName() {
        return mSettingName;
    }

    public void setSettingName(String settingName) {
        mSettingName = settingName;
    }

    public String getSettingValue() {
        return mSettingValue;
    }

    public void setSettingValue(String settingValue) {
        mSettingValue = settingValue;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

}

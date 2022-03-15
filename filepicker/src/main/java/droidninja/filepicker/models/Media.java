package droidninja.filepicker.models;

public class Media extends BaseFile {

  public int mediaType;

  public Media(int id, String name, String path, int mediaType) {
    super(id, name, path);
    this.mediaType = mediaType;
  }

  public Media() {
    super(0,null,null);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Media)) return false;

    Media media = (Media) o;

    return getId() == media.getId();
  }

  @Override
  public int hashCode() {
    return getId();
  }

  public String getPath() {
    return (getPath() !=null)? getPath() :"";
  }

  public void setPath(String path) {
    this.setPath(path);
  }

  public int getId() {
    return getId();
  }

  public void setId(int id) {
    this.setId(id);
  }

  public String getName() {
    return getName();
  }

  public void setName(String name) {
    this.setName(name);
  }

  public int getMediaType() {
    return mediaType;
  }

  public void setMediaType(int mediaType) {
    this.mediaType = mediaType;
  }

}

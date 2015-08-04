/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.pojo;

/**
 * @author hamp645
 *
 */
public class TwitterData {
  private String created_at;

  private String followers_count;

  private String image;

  private String id;

  private String user_id;

  private String text;

  private String category;

  private String user_description;

  /**
   * @return the created_at
   */
  public String getCreated_at() {
    return created_at;
  }

  /**
   * @param created_at
   *          the created_at to set
   */
  public void setCreated_at(final String created_at) {
    this.created_at = created_at;
  }

  /**
   * @return the followers_count
   */
  public String getFollowers_count() {
    return followers_count;
  }

  /**
   * @param followers_count
   *          the followers_count to set
   */
  public void setFollowers_count(final String followers_count) {
    this.followers_count = followers_count;
  }

  /**
   * @return the image
   */
  public String getImage() {
    return image;
  }

  /**
   * @param image
   *          the image to set
   */
  public void setImage(final String image) {
    this.image = image;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(final String id) {
    this.id = id;
  }

  /**
   * @return the user_id
   */
  public String getUser_id() {
    return user_id;
  }

  /**
   * @param user_id
   *          the user_id to set
   */
  public void setUser_id(final String user_id) {
    this.user_id = user_id;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * @param text
   *          the text to set
   */
  public void setText(final String text) {
    this.text = text;
  }

  /**
   * @return the category
   */
  public String getCategory() {
    return category;
  }

  /**
   * @param category
   *          the category to set
   */
  public void setCategory(final String category) {
    this.category = category;
  }

  /**
   * @return the user_description
   */
  public String getUser_description() {
    return user_description;
  }

  /**
   * @param user_description
   *          the user_description to set
   */
  public void setUser_description(final String user_description) {
    this.user_description = user_description;
  }
}

package gov.pnnl.aim.discovery.pojo;

public class JsonClusterMemberHome {

	private int clusterId = -1;

	private JsonClusterMember member = null;
	
	private String notificationLevel = "low";
	
	private String snippet = "";
	
	
	public JsonClusterMemberHome(final JsonClusterMember member, final int clusterId, final String notificationLevel) {
		this.member = member;
		this.clusterId = clusterId;
		this.notificationLevel = notificationLevel;
		if (this.notificationLevel.equals("high")) {
			this.snippet = "Document Title Goes Here\n... snippet of text with an important keyword ...\n... another snippet of text with an important keyword ...";
		}
	}

	public JsonClusterMember getMember() {
		return member;
	}

	public int getClusterId() {
		return clusterId;
	}

	public String getNotificationLevel() {
	  return notificationLevel;
  }

	public void setNotificationLevel(String notificationLevel) {
	  this.notificationLevel = notificationLevel;
  }

	public String getSnippet() {
	  return snippet;
  }

	public void setSnippet(String snippet) {
	  this.snippet = snippet;
  }
}

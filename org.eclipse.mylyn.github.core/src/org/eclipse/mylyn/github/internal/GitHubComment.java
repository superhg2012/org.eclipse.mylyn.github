package org.eclipse.mylyn.github.internal;

import com.google.gson.annotations.SerializedName;

public class GitHubComment {

	@SerializedName("gravatar_id")
	private String gravatarId;

	private String user;

	private String body;

	private String id;

	@SerializedName("created_at")
	private String createdAt;
	@SerializedName("updated_at")
	private String updatedAt;

	public GitHubComment(String gravatarId, String user, String body,
			String id, String createdAt, String updatedAt) {
		this.gravatarId = gravatarId;
		this.user = user;
		this.body = body;
		this.id = id;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public GitHubComment() {
		this.gravatarId = "";
		this.user = "";
		this.body = "";
		this.id = "";
		this.createdAt = "";
		this.updatedAt = "";
	}

	public void setGravatarId(String gravatarId) {
		this.gravatarId = gravatarId;
	}

	public String getGravatarId() {
		return gravatarId;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getBody() {
		return body;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

}

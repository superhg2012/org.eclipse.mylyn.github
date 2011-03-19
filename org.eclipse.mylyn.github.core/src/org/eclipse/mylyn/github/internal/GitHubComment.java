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

	public final void setGravatarId(String gravatarId) {
		this.gravatarId = gravatarId;
	}

	public final String getGravatarId() {
		return gravatarId;
	}

	public final void setBody(String body) {
		this.body = body;
	}

	public final String getBody() {
		return body;
	}

	public final void setUser(String user) {
		this.user = user;
	}

	public final String getUser() {
		return user;
	}

	public final void setId(String id) {
		this.id = id;
	}

	public final String getId() {
		return id;
	}

	public final void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public final String getCreatedAt() {
		return createdAt;
	}

	public final void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public final String getUpdatedAt() {
		return updatedAt;
	}

}

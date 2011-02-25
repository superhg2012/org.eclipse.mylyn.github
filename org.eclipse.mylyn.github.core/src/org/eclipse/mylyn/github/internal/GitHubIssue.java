/*
 * Copyright 2009 Christian Trutz 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 */
package org.eclipse.mylyn.github.internal;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * GitHub Issue object to hold all the properties of an individual issue.
 */
public class GitHubIssue {

	private String number;

	private String user;

	private String title;

	private String body;
	
	/**
	 * open, closed
	 */
	private String state;

	@SerializedName("created_at")
	private String createdAt;
	@SerializedName("updated_at")
	private String updatedAt;
	@SerializedName("closed_at")
	private String closedAt;
	
	private Integer comments;
	
	private List<String> labels;

	/**
	 * Create a new GitHub Issue Object
	 * 
	 * @param number
	 *            - GitHub Issue number
	 * @param user
	 *            - User who the posted issue belongs too.
	 * @param title
	 *            - Issue title
	 * @param body
	 *            - The text body of the issue;
	 */
	public GitHubIssue(final String number, final String user,
			final String title, final String body, final Integer comments, final List<String> labels) {
		this.number = number;
		this.user = user;
		this.title = title;
		this.body = body;
		this.setComments(comments);
		this.setLabels(labels);
	}

	/**
	 * Create a GitHub Issue with all parameters set to empty.
	 */
	public GitHubIssue() {
		this.number = "";
		this.user = "";
		this.title = "";
		this.body = "";
		this.setComments(0);
		this.setLabels(new ArrayList<String>());
	}

	/**
	 * Getter for the issue number
	 * 
	 * @return The string representation of the issue number.
	 */
	public final String getNumber() {
		return number;
	}

	/**
	 * Set the issues's number
	 * 
	 * @param number
	 *            - String representation of the number to set to.
	 */
	public final void setNumber(final String number) {
		this.number = number;
	}

	/**
	 * Getter for the user name of the issue creator
	 * 
	 * @return The user name of the person who created the issue
	 */
	public final String getUser() {
		return user;
	}

	/**
	 * Set the issue user name to
	 * 
	 * @param user
	 *            - The user name to set the issue creator to.
	 */
	public final void setUser(final String user) {
		this.user = user;
	}

	/**
	 * Getter for the issue Title
	 * 
	 * @return The title text of this issue
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public final void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * Getter of the body of an issue
	 * 
	 * @return The text body of the issue
	 */
	public final String getBody() {
		return body;
	}

	/**
	 * Setter for the body of an issue
	 * 
	 * @param body
	 *            - The text body to set for this issue
	 */
	public final void setBody(final String body) {
		this.body = body;
	}

	public final String getState() {
		return state;
	}

	public final void setState(String state) {
		this.state = state;
	}

	public final String getCreatedAt() {
		return createdAt;
	}

	public final void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public final String getUpdatedAt() {
		return updatedAt;
	}

	public final void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public final String getClosedAt() {
		return closedAt;
	}

	public final void setClosedAt(String closedAt) {
		this.closedAt = closedAt;
	}

	public final void setComments(Integer comments) {
		this.comments = comments;
	}

	public final Integer getComments() {
		return comments;
	}

	public final void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public final List<String> getLabels() {
		return labels;
	}
}

/**
 * 
 */
package org.eclipse.mylyn.github.internal;

import com.google.gson.annotations.SerializedName;

/**
 * GitHub user details
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public class GitHubUser {

	private String login;

	private String name;

	private String company;

	private String location;

	private String blog;

	private String email;

	@SerializedName("gravatar_id")
	private String gravatarId;

	public GitHubUser() {
		this.login = "";
		this.name = "";
		this.company = "";
		this.location = "";
		this.blog = "";
		this.email = "";
		this.gravatarId = "";
	}

	public GitHubUser(String login, String name, String company,
			String location, String blog, String email, String gravatarId) {
		this.login = login;
		this.name = name;
		this.company = company;
		this.location = location;
		this.blog = blog;
		this.email = email;
		this.gravatarId = gravatarId;

	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCompany() {
		return company;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setBlog(String blog) {
		this.blog = blog;
	}

	public String getBlog() {
		return blog;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setGravatarId(String gravatarId) {
		this.gravatarId = gravatarId;
	}

	public String getGravatarId() {
		return gravatarId;
	}

}

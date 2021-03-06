package com.jpanotesproject.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "User")
public class User extends BaseEntity {

	@Column(name = "username", unique = true, length = 255)
	private String username;

	@Column(name = "password", length = 255)
	private String password;

	@Column(name = "email", unique = true, length = 255)
	private String email;

	@Column(name = "resgistration_date")
	private java.sql.Timestamp registrationDate;

	@ElementCollection
	@CollectionTable(name = "User_Has_Shared_Notes", joinColumns = @JoinColumn(name = "shared_to", referencedColumnName = "username"))
	@MapKeyJoinColumn(name = "shared_note", referencedColumnName = "id")
	@Column(name = "permission_level")
	private Map<Note, Integer> sharedNotes;

	@OneToMany
	@JoinTable(name = "User_Has_Notes", joinColumns = { @JoinColumn(name = "author_name", referencedColumnName = "username") }, inverseJoinColumns = {
			@JoinColumn(name = "note_id", referencedColumnName = "id") })
	private Set<Note> ownNotes;

	public User(String name, String password, String email) {
		super();
		this.username = name;
		this.password = password;
		this.email = email;

		long now = new java.util.Date().getTime();
		this.registrationDate = new java.sql.Timestamp(now);

		sharedNotes = new HashMap<Note, Integer>();
		ownNotes = new HashSet<Note>();
	}

	public User() {
		super();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void addAuthorNote(Note note) {
		this.ownNotes.add(note);
	}

	public void shareNote(Note n, int permissionLevel) {
		if (permissionLevel > 0)
			this.sharedNotes.put(n, permissionLevel);
		else
			this.sharedNotes.remove(n);
	}

	public void unshareNote(Note n) {
		this.sharedNotes.remove(n);
	}

	public boolean canRead(Note note) {
		return ownNotes.contains(note) || (sharedNotes.containsKey(note) && sharedNotes.get(note) >= 1);
	}

	public boolean canReadAndWrite(Note note) {
		return ownNotes.contains(note) || (sharedNotes.containsKey(note) && sharedNotes.get(note) == 2);
	}

	public void setPermission(Note note, Integer permissionLevel) {
		if (permissionLevel > 0)
			sharedNotes.put(note, permissionLevel);
		else
			sharedNotes.remove(note);
	}

	public java.sql.Timestamp getRegistrationDate() {
		return registrationDate;
	}

	public Set<Note> getOwnNotes() {
		return ownNotes;
	}

	public Map<Note, Integer> getSharedNotes() {
		return sharedNotes;
	}
}

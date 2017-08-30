package com.jpanotesproject.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "USER")
public class User extends BaseEntity {

	@Column(name = "USERNAME", unique = true, length = 255)
	private String username;

	@Column(name = "PASSWORD", length = 255)
	private String password;

	@Column(name = "EMAIL", unique = true, length = 255)
	private String email;

	@Column(name = "RESGISTRATION_DATE")
	private java.sql.Timestamp registrationDate;

	@ElementCollection
	@CollectionTable(name = "USER_SHARED_NOTES")
	@MapKeyColumn(name = "PERMISSION_LEVEL")
	@MapKey
	private Map<Note, Integer> sharedNotes;

	@OneToMany
	@JoinTable(name = "AUTHOR_NOTES", joinColumns = {
			@JoinColumn(name = "NOTE_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
					@JoinColumn(name = "AUTHOR_ID", referencedColumnName = "ID") })
	private List<Note> ownNotes;

	public User(String name, String password, String email) {
		super();
		this.username = name;
		this.password = password;
		this.email = email;

		long now = new java.util.Date().getTime();
		this.registrationDate = new java.sql.Timestamp(now);

		sharedNotes = new HashMap<Note, Integer>();
		ownNotes = new ArrayList<Note>();
	}

	public User() {
		super();
	}

	public boolean canRead(Note note) {
		if (ownNotes.contains(note))
			return true;
		else
			return sharedNotes.containsKey(note) && sharedNotes.get(note) >= 1;
	}

	public boolean canReadAndWrite(Note note) {
		if (ownNotes.contains(note))
			return true;
		else
			return sharedNotes.containsKey(note) && sharedNotes.get(note) == 2;
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

	public Map<Note, Integer> getSharedNotes() {
		return sharedNotes;
	}

	public List<Note> getOwnNotes() {
		return ownNotes;
	}
}

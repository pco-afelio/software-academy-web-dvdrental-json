package be.afelio.pco.web;

import java.util.List;

/*
 { "title": "Avengers", "description": "super hero movie", "releaseYear": 2019, 
  "languageName": "English", "length": 180,
  "actorIds": [1, 2, 3]
}
 */
public class CreateFilmParameters {

	private String title;
	private String description;
	private int releaseYear;
	private String languageName;
	private short length;
	private List<Integer> actorIds;
	
	public CreateFilmParameters() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(int releaseYear) {
		this.releaseYear = releaseYear;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public short getLength() {
		return length;
	}

	public void setLength(short length) {
		this.length = length;
	}

	public List<Integer> getActorIds() {
		return actorIds;
	}

	public void setActorIds(List<Integer> actorIds) {
		this.actorIds = actorIds;
	}

	@Override
	public String toString() {
		return "CreateFilmParameters [title=" + title + ", description=" + description + ", releaseYear=" + releaseYear
				+ ", languageName=" + languageName + ", length=" + length + ", actorIds=" + actorIds + "]";
	}
}

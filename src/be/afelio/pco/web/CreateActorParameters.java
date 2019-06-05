package be.afelio.pco.web;

// { "firstname": "Betty", "name": "Boop" }
public class CreateActorParameters {

	public String firstname;
	public String name;
	
	@Override
	public String toString() {
		return "CreateActorParameters [firstname=" + firstname + ", name=" + name + "]";
	}
}

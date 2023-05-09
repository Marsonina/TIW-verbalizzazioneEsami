package beans;

public class User {
	private String matricola;
	private String role;
	private String name;
	private String surname;
	
	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}

	public String getMatricola() {
		return matricola;
	}

	public String getRole() {
		return role;
	}
	
	public void setMatricola(String m) {
		 matricola = m;
	}

	public void setRole(String r) {
		role = r;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public void setSurname(String s) {
		surname = s;
	}

}

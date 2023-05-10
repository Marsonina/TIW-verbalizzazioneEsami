package beans;

public class Student {
	private String matricola;
	private String name;
	private String surname;
	private String degree;
	private String email;
	
	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public String getDegree() {
		return degree;
	}
	
	public String getEmail() {
		return email;
	}

	public String getMatricola() {
		return matricola;
	}
	
	public void setMatricola(String m) {
		 matricola = m;
	}

	public void setName(String n) {
		name = n;
	}
	
	public void setSurname(String s) {
		surname = s;
	}
	
	public void setDegree(String d) {
		degree = d;
	}
	
	public void setEmail(String e) {
		email = e;
	}

}

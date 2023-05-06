package beans;

public class EnrolledStudent {
	private int matricola;
	private String name;
	private String surname;
	private String email;
	private String result = null;
	private String resultState = "non iserito";

	public int getMatricola() {
		return matricola;
	}

	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getResult() {
		return result;
	}
	
	public String getResultState() {
		return resultState;
	}
	
	public void setMatricola(int m) {
		 matricola = m;
	}

	public void setName(String n) {
		name = n;
	}
	
	public void setSurname(String s) {
		surname = s;
	}
	
	public void setEmail(String e) {
		email = e;
	}
	
	public void setResult(String r) {
		result = r;
	}
	
	public void setResultState(String rs) {
		resultState = rs;
	}

}

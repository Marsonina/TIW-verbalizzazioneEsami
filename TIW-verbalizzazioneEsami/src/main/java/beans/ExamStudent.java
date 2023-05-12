package beans;

public class ExamStudent{
	
	private String matricola;
	private String name;
	private String surname;
	private String email;
	private String degree;
	private String result = null;
	private String resultState = "NON INSERITO";

	public String getMatricola() {
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
	
	public String getDegree() {
		return degree;
	}
	
	
	public String getResult() {
		return result;
	}
	
	public String getResultState() {
		return resultState;
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
	
	public void setEmail(String e) {
		email = e;
	}
	
	public void setResult(String r) {
		result = r;
	}
	
	public void setResultState(String rs) {
		resultState = rs;
	}
	public void setDegree(String d) {
		degree = d;
	}

}

package beans;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Verbal {
	private int verbalId = 0;
	private LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);;
	private String matricolaTeacher;
	
	public String getMatricolaTeacher() {
		return matricolaTeacher;
	}

	public int getVerbalId() {
		return verbalId;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}
	
	
	public void setVerbalId(int i) {
		 verbalId = i;
	}

	public void setMatricolaTeacher(String m) {
		matricolaTeacher = m;
	}
}
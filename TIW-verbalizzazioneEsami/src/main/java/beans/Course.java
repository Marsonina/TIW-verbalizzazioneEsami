//course bean
package beans;

public class Course {
	private int courseId;
	private String courseName;

	public int getCourseId() {
		return courseId;
	}

	public String getCourseName() {
		return courseName;
	}
	
	public void setCourseId(int i) {
		 courseId = i;
	}

	public void setCourseName(String n) {
		courseName = n;
	}

}

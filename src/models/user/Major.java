package src.user;

public class Major {
    private int majorID;
    private String majorName;
    private String department;
    private String description;

    public Major(int majorID, String name, String department){
        this.majorID = majorID;
        this.majorName = name;
        this.department = department;
        this.description = "";
    }

    public Major(int majorID, String name, String department, String description){
        this.majorID = majorID;
        this.majorName = name;
        this.description = description;
        this.department = department;
    }

    // will be stored in db, so just temporary method
    private static int generateMajorID(){
        return (int) (Math.random() * 100000);
    }

    public static Major createdMajor(String name, String department){
        int newID = generateMajorID();
        Major major = new Major(newID, name, department);
        System.out.println("Major created: " + name);
        return major;
    }

    public static Major createMajor(String name, String department, String description) {
        int newId = generateMajorID();
        Major major = new Major(newId, name, department, description);
        System.out.println("Major created: " + name);
        return major;
    }

    public int getMajorId() {
        return majorID;
    }

    public String getName() {
        return majorName;
    }

    public void setName(String name) {
        this.majorName = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Major{" +
                "majorId=" + majorID +
                ", name='" + majorName + '\'' +
                ", department='" + department + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Major major = (Major) obj;
        return majorID == major.majorID;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(majorID);
    }

}

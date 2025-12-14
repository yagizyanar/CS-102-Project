package src.user;

public class University {
    private int uniID;
    private String uniName;
    private String uniCountry;
    private String uniCity;
    private String logoPath;

    public University(int uniID, String uniName, String uniCountry, String uniCity){
        this.uniID = uniID;
        this.uniName = uniName;
        this.uniCountry = uniCountry;
        this.uniCity = uniCity;
        this.logoPath = "logos/default_university.png";
    }

    public University(int universityId, String name, String country, String city, String logoPath) {
        this.uniID = universityId;
        this.uniName = name;
        this.uniCountry = country;
        this.uniCity = city;
        this.logoPath = logoPath;
    }

    // will be stored in database, so it's just temporary
    private static int generateUniID(){
        return (int) (Math.random() * 100000);
    }

    public static University createUniversity(String name, String country, String city){
        int newUniID = generateUniID();
        University university = new University(newUniID, name, country, city);
        System.out.println("University created: " + name);
        return university;
    }

    public int getUniID(){
        return uniID;
    }

    public String getName() {
        return uniName;
    }

    public void setName(String name) {
        this.uniName = name;
    }

    public String getCountry() {
        return uniCountry;
    }

    public void setCountry(String country) {
        this.uniCountry = country;
    }

    public String getCity() {
        return uniCity;
    }

    public void setCity(String city) {
        this.uniCity = city;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath){
        this.logoPath = logoPath;
    }

    @Override
    public String toString(){
        return "University{" +
                "universityId=" + uniID +
                ", name='" + uniName + '\'' +
                ", city='" + uniCity + '\'' +
                ", country='" + uniCity + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }

        University that = (University) obj;
        return uniID == that.uniID;
    }


    @Override
    public int hashCode(){
        return Integer.hashCode(uniID);
    }

}

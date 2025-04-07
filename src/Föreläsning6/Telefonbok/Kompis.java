package Föreläsning6.Telefonbok;

public class Kompis {
    String Name;
    String Address;
    String Phone;

    public Kompis(String name, String address, String phone) {
        Name = name;
        Address = address;
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }


    @Override
    public String toString() {
        return "Kompis{" +
                "Name='" + Name + '\'' +
                ", Address='" + Address + '\'' +
                ", Phone='" + Phone + '\'' +
                '}';
    }

}

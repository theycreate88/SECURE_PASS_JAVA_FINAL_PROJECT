import java.io.*;

class Student implements Serializable{

     int id;
     String name;

    public Student(int id,String name){

        this.id=id;
        this.name=name;
    }

}

public class Serialization{

    public static void main(String[] args) {
        
        Student s1=new Student(1,"Ali");
        Student s2=new Student(2,"Sara");
        Student s3=new Student(3,"Misbah");

        try{
            ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream("student.txt"));

            oos.writeObject(s1);
            oos.writeObject(s2);
            oos.writeObject(s3);

            System.out.println("Students Stored in student.txt");

            oos.close();
        }
        catch(IOException e){

            System.out.println("Serialization Error "+e);
            
        }


    }
}
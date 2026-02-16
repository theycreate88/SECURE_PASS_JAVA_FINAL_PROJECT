import java.io.EOFException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class Deserialization {
    
    public static void main(String[] args) {
        
        try {
            ObjectInputStream ois= new ObjectInputStream(new FileInputStream("student.txt"));

            while (true) {

                Student s= (Student) ois.readObject();

                System.out.println(s.id+" "+s.name);
                
            }
        } catch (EOFException e) {
            System.out.println("All Objects Read!");
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }
}

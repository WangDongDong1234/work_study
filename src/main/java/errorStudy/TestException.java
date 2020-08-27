package errorStudy;

import java.io.IOException;
import java.sql.SQLException;

public class TestException {

    public static void main(String[] args) {
        new TestException().f1();
    }

    private void f1(){
        try{
            f2();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void f2(){
        try{
            f3();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void f3()throws IOException {
        try{
            System.out.println("popup");
            throw new SQLException("info one");
        }catch (Exception e){
            throw new IOException(e);
        }
    }
}

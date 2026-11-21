package pe.edu.cssp.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneradorHashTest {

    public static void generarHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String nuevaPassword = "123456";
        String hash = encoder.encode(nuevaPassword);

        System.out.println("Contraseña: " + nuevaPassword);
        System.out.println("Hash BCrypt: " + hash);

        System.out.println(
            "¿Coincide?: " + encoder.matches(nuevaPassword, hash)
        );
    }

    public static void main(String[] args) {
        generarHash();
    }
}

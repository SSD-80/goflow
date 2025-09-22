package service.auth;

import jakarta.servlet.http.HttpSession;
import model.Driver;
import model.Rider;

public interface IOAuthService {
    Rider handleRiderLogin(String code, String state, HttpSession session) throws Exception;
    Driver handleDriverLogin(String code, String state, HttpSession session) throws Exception;
}

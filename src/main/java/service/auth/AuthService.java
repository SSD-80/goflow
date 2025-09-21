package service.auth;

import model.Driver;
import model.Rider;
import service.driver.DriverServiceImpl;
import service.driver.IDriverService;
import service.rider.IRiderService;
import service.rider.RiderServiceImpl;
import util.CommonConstants;
import util.Md5;
import util.PasswordUtil;

// This class is used to authenticate users
public class AuthService implements IAuthService {

    // default constructor
    public AuthService() {
    }

    // login method
    public boolean login(String email, String password, String role) {

        if (role.equals("Admin")) {

            String hashedPassword = Md5.generate(password);
            return email.equals(CommonConstants.ADMIN_USERNAME) && hashedPassword.equals(CommonConstants.ADMIN_PASSWORD);

        } else if (role.equals("Rider")) {

            IRiderService iRiderService = new RiderServiceImpl();
            Rider rider = iRiderService.getRiderByEmail(email);

            String dbUname = rider.getEmail();
            String dbPwd = rider.getPassword();

            // Case 1: Looks like bcrypt (starts with $2a$, $2b$, or $2y$)
            if (dbPwd.startsWith("$2a$") || dbPwd.startsWith("$2b$") || dbPwd.startsWith("$2y$")) {
                if (PasswordUtil.checkPassword(password, dbPwd)) {
                    return email.equals(dbUname);
                } else {
                    return false;
                }
            }

            // Case 2: Looks like MD5 (32-character hex string)
            if (dbPwd.length() == 32 && dbPwd.matches("[0-9a-fA-F]+")) {
                String hashedPassword = Md5.generate(password);
                if (hashedPassword.equalsIgnoreCase(dbPwd)) {
                    return email.equals(dbUname);
                } else {
                    return false;
                }
            }
            // Otherwise: invalid
            return false;

        } else if (role.equals("Driver")) {

            IDriverService iDriverService = new DriverServiceImpl();
            Driver driver = iDriverService.getDriverByEmail(email);

            String dbUname = driver.getEmail();
            String dbPwd = driver.getPassword();

            // Case 1: Looks like bcrypt (starts with $2a$, $2b$, or $2y$)
            if (dbPwd.startsWith("$2a$") || dbPwd.startsWith("$2b$") || dbPwd.startsWith("$2y$")) {
                if (PasswordUtil.checkPassword(password, dbPwd)) {
                    return email.equals(dbUname);
                } else {
                    return false;
                }
            }

            // Case 2: Looks like MD5 (32-character hex string)
            if (dbPwd.length() == 32 && dbPwd.matches("[0-9a-fA-F]+")) {
                String hashedPassword = Md5.generate(password);
                if (hashedPassword.equalsIgnoreCase(dbPwd)) {
                    return email.equals(dbUname);
                } else {
                    return false;
                }
            }
            // Otherwise: invalid
            return false;
        } else {
            return false;
        }

    }
}

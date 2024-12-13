package com.blue.mom.user.resources;

import com.blue.mom.user.entity.User;
import com.blue.mom.user.exeption.UserNotFoundException;
import com.blue.mom.user.models.AuthRequest;
import com.blue.mom.user.models.AuthResponse;
import com.blue.mom.user.service.impl.UserService;
import com.blue.mom.user.utils.PBKDF2Encoder;
import com.blue.mom.user.utils.TokenUtils;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.vertx.web.Body;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.wildfly.security.password.Password;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.WildFlyElytronPasswordProvider;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationResource {
    @Inject
    PBKDF2Encoder passwordEncoder;


    private final UserService userService;

    @ConfigProperty(name = "com.blue.mom.user.quarkusjwt.jwt.duration") public Long duration;
    @ConfigProperty(name = "mp.jwt.verify.issuer") public String issuer;

    @Inject
    public AuthenticationResource(UserService userService) {
        this.userService = userService;
    }

    @PermitAll
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(AuthRequest authRequest) throws Exception {
        User u = userService.getUserByUsername(authRequest.username);
        if (u != null && verifyBCryptPassword(u.getPassword(), authRequest.password)) {
            System.out.println(u.getUsername());
            System.out.println(u.getPassword());
            System.out.println(verifyBCryptPassword(u.getPassword(), authRequest.password));
            return Response.ok(new AuthResponse(TokenUtils.generateToken(u.getUsername(), u.getRole(), duration, issuer))).build();
//                return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    public static boolean verifyBCryptPassword(String bCryptPasswordHash, String passwordToVerify) throws Exception {

        WildFlyElytronPasswordProvider provider = new WildFlyElytronPasswordProvider();

        // 1. Create a BCrypt Password Factory
        PasswordFactory passwordFactory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT, provider);

        // 2. Decode the hashed user password
        Password userPasswordDecoded = ModularCrypt.decode(bCryptPasswordHash);

        // 3. Translate the decoded user password object to one which is consumable by this factory.
        Password userPasswordRestored = passwordFactory.translate(userPasswordDecoded);

        // Verify existing user password you want to verify
        return passwordFactory.verify(userPasswordRestored, passwordToVerify.toCharArray());

    }
}

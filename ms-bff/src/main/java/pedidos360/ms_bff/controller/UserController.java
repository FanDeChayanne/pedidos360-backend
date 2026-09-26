package pedidos360.ms_bff.controller;

import java.util.Map;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/user")
    public Map<String,Object> user(
            JwtAuthenticationToken authentication) {

        return authentication.getTokenAttributes();
    }
}

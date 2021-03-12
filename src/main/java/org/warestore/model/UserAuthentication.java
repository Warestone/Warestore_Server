package org.warestore.model;

import lombok.Data;
import javax.validation.constraints.NotEmpty;

@Data
public class UserAuthentication {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}

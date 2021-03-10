package org.warestore.model;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class UserAuthentication {
    @NotNull
    private String username;
    @NotNull
    private String password;
}

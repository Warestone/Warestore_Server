package org.warestore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class UserAuthentication {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}

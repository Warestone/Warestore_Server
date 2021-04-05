package org.warestore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class UserAuthentication {
    @NotEmpty
    @Pattern(regexp = "[a-z][a-z0-9]{3,20}")
    private String username;

    @NotEmpty
    @Size(min = 4, max = 12)
    private String password;
}

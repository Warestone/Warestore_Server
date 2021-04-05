package org.warestore.model;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class EditPassword {
    @NotEmpty
    @Size(min = 4, max = 12)
    private String currentPassword;

    @NotEmpty
    @Size(min = 4, max = 12)
    private String newPassword;
}

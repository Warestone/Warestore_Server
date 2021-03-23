package org.warestore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Token {
    private String token;

    @JsonIgnore
    public String getTokenWithoutBearer(){
        return token.replace(" ","").replace("Bearer","");
    }
}

package workable.movieRama.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Builder
@CompoundIndexes({
        @CompoundIndex(def = "{ 'userId': 1}", background = true, useGeneratedName = true),
        @CompoundIndex(def = "{ 'email': 1}", background = true, useGeneratedName = true),
})
@Document(collection = "user")
public class User {
    @Id
    private String  id;
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")
    private String password;
    @NotBlank
    @Pattern(regexp = "^(.+)@(\\S+)$")
    private String email;
    private String userId;
    private LocalDateTime registratioDate;

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonProperty
    public void setId(String id) {
        this.id = id;
    }
}
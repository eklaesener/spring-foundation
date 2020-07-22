package de.eklaesener.inventorizer.greeting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

public final class GreetingDTO extends RepresentationModel<GreetingDTO> {

    @Getter
    private final String content;


    @JsonCreator
    public GreetingDTO(@JsonProperty final String content) {
        super();
        this.content = content;
    }
}

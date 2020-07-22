package de.eklaesener.inventorizer.greeting;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/greeting")
public class GreetingController {

    private static final String TEMPLATE = "Hello, %s!";

    public GreetingController() {

    }

    @GetMapping
    public HttpEntity<GreetingDTO> showGreeting(
        @RequestParam(value = "name", defaultValue = "World") final String name) {

        final GreetingDTO greetingDTO = new GreetingDTO(String.format(TEMPLATE, name));
        greetingDTO.add(linkTo(methodOn(GreetingController.class)
            .showGreeting(name)).withSelfRel());

        return new ResponseEntity<>(greetingDTO, HttpStatus.OK);
    }
}

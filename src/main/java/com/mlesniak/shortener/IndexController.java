package com.mlesniak.shortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class Contact {
    public static int nextId = 0;

    public String name;
    public String email;
    public int id = nextId++;

    public Contact(String name, String email) {
        this.name = name;
        this.email = email;
    }
}

class Data {
    public List<Contact> contacts = new LinkedList<>();

    public boolean hasEmail(String email) {
        return contacts
                .stream()
                .anyMatch(contact -> contact.email.equals(email));
    }

    public void delete(int id) {
        contacts.removeIf(contact -> contact.id == id);
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
    }
}

class FormData {
    public Contact values;
    public Map<String, String> errors = new HashMap<>();
}

class Page {
    public Data data = dummyData();
    public FormData formData = new FormData();

    static Data dummyData() {
        Data data = new Data();
        data.contacts.add(new Contact("John Doe", "j"));
        data.contacts.add(new Contact("Jane Doe", "jane@gmail.com"));
        return data;
    }
}

@HtmxController
public class IndexController {
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);
    private Page page = new Page();

    @GetMapping("/")
    public HtmxResponse index() {
        return new HtmxBuilder()
                .add("index", page)
                .build();
    }

    @PostMapping(value = "/contacts")
    public HtmxResponse update(@RequestParam String name, @RequestParam String email) throws Exception {
        log.info("name={}, email={}", name, email);
        var builder = new HtmxBuilder();

        if (page.data.hasEmail(email)) {
            var fd = new FormData();
            fd.values = new Contact(name, email);
            fd.errors.put("email", "Email already exists");

            builder.add("form", fd).status(HttpStatus.UNPROCESSABLE_ENTITY);
            return builder.build();
        }

        Contact newContact = new Contact(name, email);
        // "Database" update.
        page.data.addContact(newContact);

        builder.add("form", new FormData());
        builder.add("oob-contact", newContact);

        return builder.build();
    }

    @DeleteMapping(value = "/contacts/{id}")
    public HtmxResponse delete(@PathVariable int id) throws InterruptedException {
        log.info("id={}", id);
        Thread.sleep(3000);
        page.data.delete(id);
        return new HtmxBuilder().build();
    }
}
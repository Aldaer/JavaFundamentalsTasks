package model;

import lombok.Data;

@Data
public class Food {
    int id;
    String name;
    String price;
    String description;
    Integer calories;
}

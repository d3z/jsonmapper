package com.joocy.jsonmapper;

import static org.junit.Assert.*;
import org.junit.Test;

public class JSONMapperTest {

    @Test
    public void testFromJSON() throws Exception {
        String json = "{\"id\":\"1\",\"name\":\"tester\"}";
        TestModel modelInstance = JSONMapper.fromJSON(TestModel.class, json);
        assertEquals("Oops. Id didn't parse properly", 1, modelInstance.getId());
        assertEquals("Oops. Name didn't parse properly", "tester", modelInstance.getName());
    }

    @Test
    public void testToJSON() throws Exception {
        TestModel modelInstance = new TestModel(2, "another_test");
        String json = JSONMapper.toJSON(modelInstance);
        String expected = "{\"id\":\"2\",\"name\":\"another_test\"}";
        assertEquals("Oops. toJSON didn't work out too well.", expected, json);
    }
}

class TestModel {
    @JSONProperty("id") private int id;
    @JSONProperty("name") private String name;

    public TestModel() {}

    public TestModel(int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }
}

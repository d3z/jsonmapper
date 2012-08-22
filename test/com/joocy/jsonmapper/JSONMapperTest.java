package com.joocy.jsonmapper;

import static org.junit.Assert.*;
import org.junit.Test;

public class JSONMapperTest {

    @Test
    public void testFromJSON() throws Exception {
        String json = "{\"id\":\"1\",\"name\":\"tester\",\"test\":\"true\"}";
        TestModel modelInstance = JSONMapper.fromJSON(TestModel.class, json);
        assertEquals("Oops. Id didn't parse properly", 1, modelInstance.getId());
        assertEquals("Oops. Name didn't parse properly", "tester", modelInstance.getName());
        assertEquals("Oops. Test didn't parse properly", true, modelInstance.isTest());
    }

    @Test
    public void testToJSON() throws Exception {
        TestModel modelInstance = new TestModel(2, "another_test", false);
        String json = JSONMapper.toJSON(modelInstance);
        String expected = "{\"id\":\"2\",\"name\":\"another_test\",\"test\":\"false\"}";
        assertEquals("Oops. toJSON didn't work out too well.", expected, json);
    }
}

class TestModel {
    @JSONProperty("id") private int id;
    @JSONProperty("name") private String name;
    @JSONProperty("test") private boolean test;

    public TestModel() {}

    public TestModel(int id, final String name, boolean test) {
        this.id = id;
        this.name = name;
        this.test = test;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    public boolean isTest() { return test; }
    public void setTest(boolean test) { this.test = test; }
}

package com.zerjioang.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * Created by .local on 27/09/2016.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestVelocityConfig {

    private VelocityEngine ve;
    private Template t;
    private VelocityContext context;

    @Before
    public void setUp() {
        /*  first, get and initialize an om  */
        ve = new VelocityEngine();
    }

    @Test
    public void test00_init() throws Exception {
        ve.init();
        assertEquals(ve != null, true);
    }

    @Test
    public void test02_getContext() {
        /*  create a context and add data */
        context = new VelocityContext();
        context.put("name", "World");
        assertEquals(context != null, true);
    }

    @Test
    public void test03_getWriter() throws Exception {
        /* now render the template into a StringWriter */
        test01_getTemplate();
        test02_getContext();
        StringWriter writer = new StringWriter();
        assertEquals(context != null, true);
        assertEquals(t != null, true);
        t.merge(context, writer);
        /* show the World */
        assertEquals("Hello World!  Welcome to Velocity!", writer.toString());
    }

    @Test
    public void test01_getTemplate() throws Exception {
        t = ve.getTemplate("src/main/resources/templates/helloworld.vm", "UTF-8");
        assertEquals(t != null, true);
    }
}

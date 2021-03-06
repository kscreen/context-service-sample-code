package com.cisco.thunderhead.doc.examples;


import com.cisco.thunderhead.datatypes.LanguageType;
import com.cisco.thunderhead.dictionary.Field;
import com.cisco.thunderhead.dictionary.FieldSet;
import com.cisco.thunderhead.pod.Pod;
import com.cisco.thunderhead.util.DataElementUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@Ignore
public class FieldSetsTest extends BaseExamplesTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(FieldSetsTest.class);

    private final String FIELDSET = "sdkExample_fieldSet";
    private final String FIELD_ONE = "sdkExample_fieldOne";
    private final String FIELD_TWO = "sdkExample_fieldTwo";
    private final String FIELD_THREE = "sdkExample_fieldThree";

    @Before
    public void BeforeTest(){
        LOGGER.info("Deleting fieldsets and field before the test");
        deleteExistingFieldAndFieldSet();
    }

    @After
    public void AfterTest() throws InterruptedException {
        LOGGER.info("Deleting fieldsets and field after the test");
        deleteExistingFieldAndFieldSet();
    }

    @Test
    public void testCreateFieldWithTranslations(){
        Field field = FieldSets.createFieldWithTranslations(contextServiceClient);
        assertEquals(FIELD_ONE, field.getId());
        assertEquals("Prenom", field.getTranslations().get(LanguageType.FR));
        assertEquals("First Name",field.getTranslations().get(LanguageType.EN_US));
        assertEquals(Arrays.asList("en_US", "en_GB", "zh_CN"),field.getLocales());
    }

    @Test
    public void testCreateFieldSet(){
        FieldSet fieldSet = FieldSets.createFieldSet(contextServiceClient);
        Set<String> expectedSet = new HashSet<>();
        expectedSet.add(FIELD_ONE);
        expectedSet.add(FIELD_TWO);
        assertEquals(expectedSet, fieldSet.getFields());
        assertEquals(FIELDSET, fieldSet.getId());
    }

    @Test
    public void testUpdateFieldSet(){
        FieldSet fieldSet = FieldSets.createFieldSet(contextServiceClient);
        fieldSet = FieldSets.updateFieldSet(contextServiceClient,fieldSet);
        Set<String> expectedSet = new HashSet<>();
        expectedSet.add(FIELD_ONE);
        expectedSet.add(FIELD_TWO);
        expectedSet.add(FIELD_THREE);
        assertEquals(expectedSet, fieldSet.getFields());
        assertEquals(FIELDSET, fieldSet.getId());
    }

    @Test
    public void testDeleteFieldSet(){
        FieldSet fieldSet = FieldSets.createFieldSet(contextServiceClient);
        FieldSets.deleteFieldSet(contextServiceClient, fieldSet);
    }

    @Test
    public void testSearchFieldSet(){
        //First Create FieldSet
        FieldSet fieldSet = FieldSets.createFieldSet(contextServiceClient);

        List<FieldSet> list = FieldSets.searchFieldSet(contextServiceClient);

        assertEquals( 1, list.size() );
        assertEquals( FIELDSET, list.get(0).getId());
        assertTrue(fieldSet.getFields().contains(FIELD_ONE));
        assertTrue(fieldSet.getFields().contains(FIELD_TWO));
    }

    @Test
    public void testSearchField(){
        // creating field to search
        Field field = FieldSets.createFieldWithTranslations(contextServiceClient);

        List<Field> list = FieldSets.searchField(contextServiceClient);

        assertEquals(1, list.size());
        assertEquals( FIELD_ONE, list.get(0).getId());
    }

    @Test
    public void testCiscoBaseFieldSetUsage(){
        Pod pod = FieldSets.ciscoBaseFieldSetUsage(contextServiceClient);
        assertEquals(pod.getFieldsets(), Arrays.asList("cisco.base.pod"));
        String contextNotes = (String) DataElementUtils.convertDataSetToMap(pod.getDataElements()).get("Context_Notes");
        assertEquals("Notes about this context.", contextNotes);
    }

    @Test
    public void testCustomFieldSetUsage() throws InterruptedException {
        Thread.sleep(2000);
        Pod pod = FieldSets.customFieldSetUsage(contextServiceClient);
        assertEquals(pod.getFieldsets(), Arrays.asList(FIELDSET));

        String dataElemValue = (String) DataElementUtils.convertDataSetToMap(pod.getDataElements()).get(FIELD_ONE);
        assertEquals("receipt of purchase", dataElemValue);
    }

    @Test
    public void testCiscoAndCustomFieldSetUsage(){

        //Create pod with custom and cisco base fieldset
        Pod pod = FieldSets.customAndCiscoFieldSetUsage(contextServiceClient);
        assertEquals(pod.getFieldsets(), Arrays.asList(FIELDSET, "cisco.base.pod"));

        String contextNotes = (String) DataElementUtils.convertDataSetToMap(pod.getDataElements()).get("Context_Notes");
        assertEquals("Notes about this context.", contextNotes);

        String dataElemValue = (String) DataElementUtils.convertDataSetToMap(pod.getDataElements()).get(FIELD_ONE);
        assertEquals("Receipt of purchase", dataElemValue);
    }

    private void deleteExistingFieldAndFieldSet() {

        List<FieldSet> list = FieldSets.searchFieldSet(contextServiceClient);
        for(FieldSet fieldSet : list){
            if(fieldSet.getId().equals(FIELDSET)){
               LOGGER.info("Deleting fieldset: "+fieldSet.getId());
                contextServiceClient.delete(fieldSet);
            }
        }

        List<Field> fieldlist = FieldSets.searchField(contextServiceClient);
        for(Field field : fieldlist){
            if(field.getId().equals(FIELD_ONE) || field.getId().equals(FIELD_TWO) || field.getId().equals(FIELD_THREE)){
                LOGGER.info("Deleting field: "+field.getId());
                contextServiceClient.delete(field);
            }
        }
    }
}

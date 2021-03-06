package com.example.avjindersinghsekhon.minimaltodo;

import com.example.avjindersinghsekhon.minimaltodo.Utility.CategoryItem;
import com.example.avjindersinghsekhon.minimaltodo.Utility.ToDoItem;
import com.example.avjindersinghsekhon.minimaltodo.Utility.TodoNotificationService;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

public class TestCategoryItem extends TestCase {

    private final String CAT_TITLE = "This is some title";

    /**
     * Check that a new category item with the default constructor has no title
     */
    public void testDefaultConstructor(){
        CategoryItem categoryItem = new CategoryItem();
        assertEquals("",categoryItem.getTitle());
    }

    /**
     * Check that a category created with a title saves that title
     */
    public void testParameterConstructor(){
        CategoryItem categoryItem = new CategoryItem(CAT_TITLE);
        assertEquals(CAT_TITLE,categoryItem.getTitle());
    }

    /**
     * Ensure that we can convert CategoryItem objects to JSON
     */
    public void testObjectMarshallingToJson() {
        CategoryItem categoryItem = new CategoryItem(CAT_TITLE);

        try {
            JSONObject json = categoryItem.toJSON();
            assertEquals(CAT_TITLE, json.getString("cattitle"));
        } catch (JSONException e) {
            fail("Exception thrown during test execution: " + e.getMessage());
        }
    }

    /**
     * Ensure that we can create CategoryItem objects from JSON data
     */
    public void testObjectUnmarshallingFromJson() {
        CategoryItem categoryItem = new CategoryItem();

        try {

            JSONObject json = categoryItem.toJSON();
            CategoryItem itemFromJson = new CategoryItem();
            itemFromJson.jsonToItem(json);

            assertEquals(categoryItem.getTitle(), itemFromJson.getTitle());

        } catch (JSONException e) {
            fail("Exception thrown during test execution: " + e.getMessage());
        }

    }

    /**
     * Check if setting the category to which a task belongs works
     */
    public void testAddingTasktoCategory(){

        ToDoItem item = new ToDoItem();
        CategoryItem categoryItem = new CategoryItem(CAT_TITLE);

        item.setCategoryBelongs(CAT_TITLE);

        assertEquals(item.getCategoryBelongs(),categoryItem.getTitle());

    }

    /**
     * Ensure that when converting a ToDoItem, that belongs to a category, to a JSON and then BACK to a ToDoItem, that it retains the category it's associated with
     */
    public void testAddingTasktoCategoryJSON(){

        ToDoItem item = new ToDoItem();
        CategoryItem categoryItem = new CategoryItem(CAT_TITLE);

        item.setCategoryBelongs(CAT_TITLE);

        try {

            JSONObject json = item.toJSON();
            ToDoItem itemFromJson = new ToDoItem();
            itemFromJson.jsonToItem(json);

            assertEquals(categoryItem.getTitle(), itemFromJson.getCategoryBelongs());

        } catch (JSONException e) {
            fail("Exception thrown during test execution: " + e.getMessage());
        }

    }

    /**
     * Ensure that when converting a ToDoItem to a JSON that it holds the Category with which it's associated with
     */
    public void testAddingTasktoCategoryFromJSON() {

        ToDoItem item = new ToDoItem();
        CategoryItem categoryItem = new CategoryItem(CAT_TITLE);

        item.setCategoryBelongs(CAT_TITLE);

        try {
            JSONObject json = item.toJSON();
            assertEquals(categoryItem.getTitle(), json.getString("todocategory"));
        } catch (JSONException e) {
            fail("Exception thrown during test execution: " + e.getMessage());
        }
    }

    /**
     * Simple test to check if removing a task from an existing category to no category works
     */
    public void testRemovingTaskFromCategory(){

        ToDoItem item = new ToDoItem();
        CategoryItem categoryItem = new CategoryItem(CAT_TITLE);
        CategoryItem noneCategory = new CategoryItem("None");

        item.setCategoryBelongs(categoryItem.getTitle());
        item.setCategoryBelongs(noneCategory.getTitle());

        assertEquals(item.getCategoryBelongs(),noneCategory.getTitle());

    }

    /**
     * Simple test to check if moving a task from one category to another works
     */
    public void testMovingTaskBetweenCategories(){

        ToDoItem item = new ToDoItem();
        CategoryItem categoryItem = new CategoryItem("Category1");
        CategoryItem categoryItem2 = new CategoryItem("Category2");

        item.setCategoryBelongs(categoryItem.getTitle());
        item.setCategoryBelongs(categoryItem2.getTitle());

        assertEquals(item.getCategoryBelongs(),categoryItem2.getTitle());
    }

    /**
     * Ensure that when removing a task from a category and converting it to a JSON, it has no category
     */
    public void testRemovingTaskFromCategoryJSON(){

        ToDoItem item = new ToDoItem();
        CategoryItem categoryItem = new CategoryItem(CAT_TITLE);
        CategoryItem noneCategory = new CategoryItem("None");

        item.setCategoryBelongs(categoryItem.getTitle());
        item.setCategoryBelongs(noneCategory.getTitle());

        try {
            JSONObject json = item.toJSON();
            assertEquals(noneCategory.getTitle(), json.getString("todocategory"));
        } catch (JSONException e) {
            fail("Exception thrown during test execution: " + e.getMessage());
        }

    }

    /**
     * Ensure that when moving a task between categories and converting it to a JSON, it has the new category
     */
    public void testMovingTaskBetweenCategoriesJSON(){

        ToDoItem item = new ToDoItem();
        CategoryItem categoryItem = new CategoryItem("Category1");
        CategoryItem categoryItem2 = new CategoryItem("Category2");

        item.setCategoryBelongs(categoryItem.getTitle());
        item.setCategoryBelongs(categoryItem2.getTitle());

        try {
            JSONObject json = item.toJSON();
            assertEquals(categoryItem2.getTitle(), json.getString("todocategory"));
        } catch (JSONException e) {
            fail("Exception thrown during test execution: " + e.getMessage());
        }

    }

    /**
     * Ensure that task can be deleted from category
     */
    public void testDeletingTaskFromCategory() {
        CategoryItem categoryItem = new CategoryItem(CAT_TITLE);

        boolean retVal = categoryItem.delete();

        assertEquals(retVal,true);
    }

}

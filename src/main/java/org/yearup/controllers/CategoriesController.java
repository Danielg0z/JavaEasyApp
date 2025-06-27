package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

// add the annotations to make this a REST controller
// add the annotation to make this controller the endpoint for the following url
    // http://localhost:8080/categories
// add annotation to allow cross site origin requests

@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoriesController
{

    private CategoryDao categoryDao;
    private ProductDao productDao;

    @Autowired
    // create an Autowired controller to inject the categoryDao and ProductDao
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;

}


    // add the appropriate annotation for a get action
    // get all categories
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Category> getAll()
    {
        // find and return all categories
        return categoryDao.getAllCategories();
    }

    // add the appropriate annotation for a get action
    // get all categories by the category ID
    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Category> getById(@PathVariable int id)
    {
        Category category = categoryDao.getById(id);
        if(category == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(category);

    }

    // the url to return all products in category 1 would look like this
    // GET /api/categories/{categoryId}/products
    // get products by category
    @GetMapping("{categoryId}/products")
    @PreAuthorize("permitAll()")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        return productDao.listByCategoryId(categoryId);
    }

    // add annotation to call this method for a POST action
    // add annotation to ensure that only an ADMIN can call this function
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // only Admins have access to create
    //Response Entities give more precise control for HTTPS response
    public ResponseEntity<Category> addCategory(@RequestBody Category category)
    {
        // insert the category
        Category created = categoryDao.create(category);
        //This is clearer than just returning an object,
        //It tells the user: "Hey this was created successfully"
        return ResponseEntity.status(201).body(created);
    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')") // only Admins have access to update
    //Response Entities give more precise control for HTTPS response
    public ResponseEntity<Category> updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        Category updated = categoryDao.update(id, category);
        return ResponseEntity.status(201).body(updated);
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // only Admins have access to delete
    public ResponseEntity<Category> deleteCategory(@PathVariable int id)
    {
        Category deleted = categoryDao.delete(id);
        if(deleted == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deleted);
    }
}

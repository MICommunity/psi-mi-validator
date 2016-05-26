/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.faces.controller.search;

import uk.ac.ebi.faces.controller.BaseController;

import javax.faces.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Example search controller
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id: SearchController.java 42 2008-09-17 11:58:35Z brunoaranda $
 */
public class SearchController extends BaseController{

    private String searchQuery;

    private List<String> results;

    public SearchController() {
        results = new ArrayList<String>();
    }

    public void doSearch(ActionEvent evt) {
        results.add("Result "+new Random().nextInt());
        results.add("Result "+new Random().nextInt());
        results.add("Result "+new Random().nextInt());
        results.add("Result "+new Random().nextInt());
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }
}

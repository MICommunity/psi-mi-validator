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
package uk.ac.ebi.faces.controller;

import org.apache.myfaces.trinidad.component.UIXCollection;
import org.apache.myfaces.trinidad.component.UIXTable;
import org.apache.myfaces.trinidad.component.UIXTree;
import org.apache.myfaces.trinidad.model.RowKeySet;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Contains useful methods for all controllers - backing beans.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id: BaseController.java 42 2008-09-17 11:58:35Z brunoaranda $
 */
public abstract class BaseController implements Serializable {

	protected void addMessage(String message, String detail) {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			FacesMessage facesMessage = new FacesMessage(message, detail);
			context.addMessage(null, facesMessage);
		}
	}

	protected void addInfoMessage(String message, String detail) {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, message, detail);
			context.addMessage(null, facesMessage);
		}
	}

	protected void addWarningMessage(String message, String detail) {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, message, detail);
			context.addMessage(null, facesMessage);
		}
	}

	protected void addErrorMessage(String message, String detail) {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, detail);
			context.addMessage(null, facesMessage);
		}
	}

    protected List getSelected(UIComponent component) {
        UIXCollection table = (UIXCollection) component;

        final RowKeySet state;
        if (table instanceof UIXTable) {
            state = ((UIXTable) table).getSelectedRowKeys();
        } else {
            state = ((UIXTree) table).getSelectedRowKeys();
        }

        Iterator<Object> selection = state.iterator();
        Object oldKey = table.getRowKey();

        List selectedEntries = new ArrayList();
        while (selection.hasNext()) {
            table.setRowKey(selection.next());
            selectedEntries.add(table.getRowData());
        }
        table.setRowKey(oldKey);
        return selectedEntries;
    }

    /**
     * Use this method to get a value using a list of parameter names. The names are iterated in order
     * and if a value is found, that value is return. This method is useful to create synonym parameters.
     * @param paramNames The parameter names, which are synonyms
     * @return The value
     */
    protected String getParameterValue(String ... paramNames) {
        for (String paramName : paramNames) {
            String value = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestParameterMap().get(paramName);

            if (value != null) {
                return value;
            }
        }

        return null;
    }
}



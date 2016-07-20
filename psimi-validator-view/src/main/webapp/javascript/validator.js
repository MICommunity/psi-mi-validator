dojo.require("dojo.NodeList-traverse");

function displayFileUpload(){
    if (document.forms['ebiForm']['sourceSelector'][0].checked){
        dojo.query('.urlUpload').style('display', 'none');
        dojo.query('.localFileUpload').style('display', 'block');
    }
    else{
        dojo.query('.urlUpload').style('display', 'block');
        dojo.query('.localFileUpload').style('display', 'none');
    }
}

function displayModel(){
    if (document.forms['ebiForm']['modelSelector'][0].checked){
        dojo.query('.parScopes').style('display', 'none');
        dojo.query('.miScopes').style('display', 'block');
    }
    else{
        dojo.query('.parScopes').style('display', 'block');
        dojo.query('.miScopes').style('display', 'none');
    }
}

function displayRuleCustomization(){
    if (document.forms['ebiForm']['scopeSelector'][5].checked){
        dojo.query('.customizeRules').style('display', 'block');
    }
    else{
        dojo.query('.customizeRules').style('display', 'none');
    }
}

dojo.addOnLoad(function(){
    dojo.query('.showErrorContextDetails').onclick(function(){
        var thisList = new dojo.NodeList(this);
        thisList.parent().children('.errorContextDetails').style('display', 'block');
        thisList.parent().children('.showErrorContextDetails').style('display', 'none');
    });

    dojo.query('.hideErrorContextDetails').onclick(function(){
        var thisList = new dojo.NodeList(this);
        thisList.parent().children('.errorContextDetails').style('display', 'none');
        thisList.parent().children('.showErrorContextDetails').style('display', null);

    });

    dojo.query('.selectAllRules').onclick(function(){
        var thisList = new dojo.NodeList(this);

        var field = thisList.parent().parent().query('input[type="checkbox"]')
        for (i = 0; i < field.length; i++)
            field[i].checked = true ;

    });

    dojo.query('.unSelectAllRules').onclick(function(){
        var thisList = new dojo.NodeList(this);

        var field = thisList.parent().parent().query('input[type="checkbox"]')
        for (i = 0; i < field.length; i++)
            field[i].checked = false ;


    });

    dojo.query('.validatorScopes').onclick(displayRuleCustomization);

    displayFileUpload();
    displayModel();
    displayRuleCustomization();
});

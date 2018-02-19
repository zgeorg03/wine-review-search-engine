var host="http://localhost:8192";

var selectedCollection = null;
var showingDocs = false;
$(document).ready(function(){

    loadCollections();

});



function loadCollections(){
    $("#view").empty()
    selectedCollection = null;
    showingDocs = false;

    var elements = $("<div class='row'></div>");

    var container = $("<form class='form-inline'></form>");
    var group = container.append("<div class='form-group'></div>")
    group.append("<input id='collection-name' class='form-control' type='text' placeholder='Collection'></input>")
    group.append("<button type='submit' onclick='postCollection()' class='btn btn-default'>New collection</button>")

    elements.append(container);

    viewCollections()
    //Clear view and append elements
    $("#view").prepend(elements)
}

function viewCollections(){
    console.log('Collections Loading...')
    var elements = $("<div class='row'> </div>");
    var container = $('<div class=col-xs-6></div>');
    var panel = $('<div class="card w-400"></div>');
    var heading = $('<div class="card-header">Available Collections</div>');
    var body = $('<div class="card-block"></div>');
    panel.append(heading);
    panel.append(body);
    body.append('<div class="loader"></div>')

    $.getJSON(host+'/collections/', function(data){
        var ul = $('<div id="collections-list" class="list-group"></div>')
        for (var i in data){
            var collection = data[i];
            var name = collection.name;
            var size = collection.size;
            if(selectedCollection==name)
                ul.append('<a id="collections-name-'+name+'" href="#" onclick="selectCollection(\''+name+'\')" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center active">'+name +
                  '&nbsp<span class="badge badge-primary badge-pill">'+size+'</span></a>');
            else
                ul.append('<a id="collections-name-'+name+'" href="#" onclick="selectCollection(\''+name+'\')" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">'+name +
                  '&nbsp<span class="badge badge-primary badge-pill">'+size+'</span></a>');
            console.log(collection)
        }
        body.empty()
        body.append(ul);
    });

    container.append(panel);
    elements.append(container)
    $('#view').append("<hr/>");
    $('#view').append(elements);
}


/**Post new collection **/
function postCollection(){
    var text = $('#collection-name').val().trim();

    if(text){
        $.post(host+'/collections/'+text,"",function(data){
            if(data.message.endsWith('exists'))
                showMsg("Info",data.message)
            console.log(data)
            loadCollections();
        });
    }else{
        showMsg("Info","Please type a name for the collection")
    }
}

function selectCollection(name){
    console.log('Collection:'+name)
    selectedCollection = name;
    $('#collections-list').children().removeClass("active");
    $('#collections-name-'+name).addClass("active");


    addDocuments();

}


function addDocuments(){
    if(showingDocs){
        $("#documents-container-1").remove();
        $("#documents-container-2").remove();
    }
    var container1 = $('<div id="documents-container-1" class="row"></div>')
    var container2 = $('<div id="documents-container-2" class="row"></div>')
    var functions1 = $('<div class="col-xs-12"></div>');
    var main = $('<div id="documents-list" class="col-xs-12"></div>');
    container1.append(functions1);
    container2.append(main);
    functions1.append("<hr/>")
    functions1.append("<h3>Collection: "+selectedCollection+"</h3>")
    functions1.append("<hr/>")

    functions1.append("<button onclick='loadDocuments()' class='btn btn-primary'>List Documents</button>")

    var form = $('<form class="form-inline"></form>')

    var fileInput = $('<input id="documents-path" type="text" placeholder="Absolute Directory Path..." class="form-control-text"/>')
    form.append(fileInput);

    var bttn = $('<button type="submit" onclick="uploadDocuments()" class="btn btn-default">Upload Documents</button>');
    form.append(bttn);

    functions1.append(form);

    //Finish
    $("#view").append(container1);
    $("#view").append(container2);
    showingDocs = true;
}
function loadDocuments(){
    $.getJSON(host+'/collections/'+selectedCollection, function(data){
    var main = $("#documents-list");
        var ul = $('<div class="list-group ul"></div>')
        data = data.data;
        for (var i in data){
            var collection = data[i];
            var name = collection;
            ul.append('<a id="document-name-'+name+'" href="#" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">'+name + '</a>');
        }
        main.empty()
        main.append(ul);
    });

}

function uploadDocuments(){

    var text = $('#documents-path').val().trim();
    if(text){
    console.log(text);
        $.post(host+'/collections/'+selectedCollection+"/documents","directory="+text,function(data){
            showMsg("Info",data.message)
            console.log(data)
            loadCollections();
        });
    }else{
        showMsg("Info","Please type the directory where documents are")
    }


}
function showMsg(title, msg){
    $('#modal-header').empty();
    $('#modal-header').append("<h5>"+title+"<h5>");
    $('#modal-body').empty();
    $('#modal-body').append("<p>"+msg+"<p>");
    $('#modal-info').modal()
}







function searchCollection(){
 if(!selectedCollection){
    showMsg("Info","Please select a collection");
    return;
 }

}
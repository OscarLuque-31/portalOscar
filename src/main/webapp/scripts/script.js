
function cambiarContenido(url, id) {
 
    let element;
    
    for (let i = 0; i < 6; i++){
        element = document.getElementById(i.toString());
        if (element!== null && element.classList.contains("selected")){
            element.classList.remove("selected");
        }
    }
 
    document.getElementById(id).classList.add("selected");
 
 
    let iframe = document.getElementById("contenido");
    iframe.src = url;
}
 
function irLogin() {
    window.location.href = "./jsp/login.jsp";
}


function detColoresCentro(id){

    var color1 = document.getElementsByTagName("h4")[0];
    var color2 = document.getElementsByTagName("h4")[1];

    switch(id){
        case 1 :
            color1.classList.add('cesurColor');
            color2.classList.add('cesurColor');
            break;

        case 2 :
            color1.classList.add('picassoColor');
            color2.classList.add('picassoColor');
            break;

        case 3 :
            color1.classList.add('belenColor');
            color2.classList.add('belenColor');
            break;

        case 4 :
            color1.classList.add('turingColor');
            color2.classList.add('turingColor');
            break;

        case 5 :
            color1.classList.add('joseColor');
            color2.classList.add('joseColor');
            break;

        default : break;
    }
}  

function detColoresNoticias(id){

    var cont = document.getElementsByClassName("cont")[0];
    var img= document.getElementsByTagName("img")[0];
    var hr=document.getElementsByTagName("hr")[0];

    switch(id){
        case 1 :
            cont.classList.add('cesurColor');
            img.classList.add('cesurColor');
            hr.classList.add('cesurColor');
            break;

        case 2 :
            cont.classList.add('picassoColor');
            img.classList.add('picassoColor');
            hr.classList.add('picassoColor');
            break;

        case 3 :
            cont.classList.add('belenColor');
            img.classList.add('belenColor');
            hr.classList.add('belenColor');
            break;

        case 4 :
            cont.classList.add('turingColor');
            img.classList.add('turingColor');
            hr.classList.add('turingColor');
            break;

        case 5 :
            cont.classList.add('joseColor');
            img.classList.add('joseColor');
            hr.classList.add('joseColor');
            break;

        default : break;
    }
}
 
function determinarColores(id){
var headerElement = document.getElementsByTagName('header')[0];
var tarjetaElement = document.getElementsByClassName('tarjetaAlumno')[0];
var menuElement = document.getElementsByClassName('menu')[0];
var menuOpcionElements = document.getElementsByClassName('menuOpcion');
var cerrarElement = document.getElementsByClassName('cerrarSesion')[0];
	switch (id) {
  case 1:
    headerElement.classList.add('cesurColor');
    tarjetaElement.classList.add('cesurColor');
    menuElement.classList.add('cesurColor');
     for (var i = 0; i < menuOpcionElements.length; i++) {
                menuOpcionElements[i].classList.add('cesurColor');
            }
   
    cerrarElement.classList.add('cesurColor');
    
    break;
    
  case 2:
    headerElement.classList.add('picassoColor');
    tarjetaElement.classList.add('picassoColor');
    menuElement.classList.add('picassoColor');
         for (var i = 0; i < menuOpcionElements.length; i++) {
                menuOpcionElements[i].classList.add('picassoColor');
            }
           
    cerrarElement.classList.add('picassoColor');
   
    break;
    
  case 3:
    headerElement.classList.add('belenColor');
    tarjetaElement.classList.add('belenColor');
    menuElement.classList.add('belenColor');
         for (var i = 0; i < menuOpcionElements.length; i++) {
                menuOpcionElements[i].classList.add('belenColor');
            }
            
    cerrarElement.classList.add('belenColor');
    
    break;
    
  case 4:
    headerElement.classList.add('turingColor');
    tarjetaElement.classList.add('turingColor');
    menuElement.classList.add('turingColor');
         for (var i = 0; i < menuOpcionElements.length; i++) {
                menuOpcionElements[i].classList.add('turingColor');
            }
            
    cerrarElement.classList.add('turingColor');
    

    break;
    
  case 5:
	
    headerElement.classList.add('joseColor');
    tarjetaElement.classList.add('joseColor');
    menuElement.classList.add('joseColor');
         for (var i = 0; i < menuOpcionElements.length; i++) {
                menuOpcionElements[i].classList.add('joseColor');
            }
            
    cerrarElement.classList.add('joseColor');
    

    break;
    
    default : break;
	}
}
	
function detColorInicio(id){
	
  var elementoBorrar=document.getElementsByClassName("divider")[0];
		
			switch (id) {
  case 1:
    elementoBorrar.classList.add('cesurColor');
    break;
    
  case 2:
    elementoBorrar.classList.add('picassoColor');
    break;
    
  case 3:
    elementoBorrar.classList.add('belenColor');
    break;
    
  case 4:
    elementoBorrar.classList.add('turingColor');
    break;
    
  case 5:
    elementoBorrar.classList.add('joseColor');
    break;
    
    default : break;
	}
	
}

